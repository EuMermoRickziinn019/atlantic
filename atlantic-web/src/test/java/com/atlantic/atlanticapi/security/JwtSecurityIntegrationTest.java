package com.atlantic.atlanticapi.security;

import com.atlantic.ISBServices.Facade.RN.user.UsuarioRN;
import com.atlantic.ISBServices.Facade.SessionFacadeADM;
import com.atlantic.atlanticapi.config.SecurityConfig;
import com.atlantic.atlanticapi.controller.AuthController;
import com.atlantic.atlanticapi.controller.PessoaController;
import com.atlantic.models.adm.Pessoa;
import com.atlantic.models.user.Perfil;
import com.atlantic.models.user.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(JwtSecurityIntegrationTest.TestConfig.class)
@WebAppConfiguration
class JwtSecurityIntegrationTest {
    private static final javax.crypto.SecretKey KEY = Jwts.SIG.HS256.key().build();
    @Autowired WebApplicationContext context;
    @Autowired FilterChainProxy securityFilterChain;
    @Autowired UsuarioRN users;
    @Autowired SessionFacadeADM facade;
    @Autowired PasswordEncoder encoder;
    @Autowired JwtService jwt;
    @Autowired ObjectMapper mapper;
    private MockMvc mvc;
    private String passwordHash;

    @BeforeEach
    void setUp() {
        reset(users, facade);
        passwordHash = encoder.encode("test-password");
        when(users.loadUserByUsername(anyString())).thenThrow(new UsernameNotFoundException("not found"));
        allow("reader@example.com", "VISUALIZAR");
        allow("editor@example.com", "EDITAR");
        allow("creator@example.com", "CRIAR");
        allow("admin@example.com", "ROLE_ADMIN", "EXCLUIR");
        when(facade.getTodasPessoas()).thenReturn(List.of());
        when(facade.atualizarPessoa(any())).thenAnswer(inv -> inv.getArgument(0));
        when(facade.criarPessoa(any())).thenAnswer(inv -> {
            Pessoa pessoa = inv.getArgument(0);
            pessoa.setIdpessoa(1);
            return pessoa;
        });
        when(facade.removerPessoa(1)).thenReturn(true);
        mvc = MockMvcBuilders.webAppContextSetup(context).addFilters(securityFilterChain).build();
    }

    private void allow(String email, String... authorities) {
        doReturn(User.withUsername(email).password(passwordHash).authorities(authorities).build())
                .when(users).loadUserByUsername(email);
    }

    private String bearer(String email) {
        return "Bearer " + jwt.issue(email).value();
    }

    private MvcResult login(String email, String password) throws Exception {
        return mvc.perform(post("/api/v1/auth/login").servletPath("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(java.util.Map.of("email", email, "senha", password))))
                .andReturn();
    }

    @Test
    void loginProducesUsableTokenWithoutSessionOrPasswordLeak() throws Exception {
        var result = login("reader@example.com", "test-password");
        assertEquals(200, result.getResponse().getStatus());
        var body = mapper.readTree(result.getResponse().getContentAsString());
        assertEquals("Bearer", body.get("tokenType").asText());
        assertEquals(900, body.get("expiresIn").asLong());
        assertTrue(body.hasNonNull("expiresAt"));
        assertFalse(result.getResponse().getContentAsString().contains("test-password"));
        assertFalse(result.getResponse().getContentAsString().contains(passwordHash));
        assertTrue(result.getResponse().getHeader("Cache-Control").contains("no-store"));
        assertNull(result.getRequest().getSession(false));
        mvc.perform(get("/api/v1/pessoa/getTodasPessoas")
                        .header("Authorization", "Bearer " + body.get("accessToken").asText()))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
        // O contexto autenticado não pode vazar para a chamada seguinte.
        mvc.perform(get("/api/v1/pessoa/getTodasPessoas")).andExpect(status().isUnauthorized());
    }

    @Test
    void returnsRealUserName() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setEmail("maria@example.com");
        usuario.setNome("Maria Silva");
        usuario.setSenha(passwordHash);
        usuario.setPerfis(Set.of(new Perfil("ROLE_USER")));
        doReturn(usuario).when(users).loadUserByUsername("maria@example.com");
        var result = login("maria@example.com", "test-password");
        assertEquals(200, result.getResponse().getStatus());
        assertEquals("Maria Silva", mapper.readTree(result.getResponse().getContentAsString()).get("nome").asText());
    }

    @Test
    void wrongPasswordUnknownAndDisabledUsersReturnSameGenericError() throws Exception {
        doReturn(User.withUsername("disabled@example.com").password(passwordHash)
                .authorities("VISUALIZAR").disabled(true).build())
                .when(users).loadUserByUsername("disabled@example.com");
        var wrong = login("reader@example.com", "wrong");
        var unknown = login("missing@example.com", "test-password");
        var disabled = login("disabled@example.com", "test-password");
        for (var result : List.of(wrong, unknown, disabled)) {
            assertEquals(401, result.getResponse().getStatus());
            assertEquals(wrong.getResponse().getContentAsString(), result.getResponse().getContentAsString());
            assertFalse(result.getResponse().getContentAsString().contains("accessToken"));
        }
    }

    @Test
    void invalidLoginBodyReturns400() throws Exception {
        assertEquals(400, login("invalid", "").getResponse().getStatus());
    }

    @Test
    void missingMalformedAndUnsupportedAuthorizationReturn401() throws Exception {
        mvc.perform(get("/api/v1/pessoa/getTodasPessoas")).andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", "Bearer"))
                .andExpect(jsonPath("$.status").value(401));
        for (String header : List.of("Bearer broken", "Bearer ", "Basic abc")) {
            mvc.perform(get("/api/v1/pessoa/getTodasPessoas").header("Authorization", header))
                    .andExpect(status().isUnauthorized());
        }
        verifyNoInteractions(facade);
    }

    @Test
    void expiredAndWrongSignatureTokensReturn401() throws Exception {
        String expired = Jwts.builder().issuer("AtlanticAPI").subject("reader@example.com")
                .issuedAt(Date.from(Instant.now().minusSeconds(120)))
                .expiration(Date.from(Instant.now().minusSeconds(60))).signWith(KEY).compact();
        String forged = Jwts.builder().issuer("AtlanticAPI").subject("reader@example.com")
                .issuedAt(new Date()).expiration(Date.from(Instant.now().plusSeconds(900)))
                .signWith(Jwts.SIG.HS256.key().build()).compact();
        for (String token : List.of(expired, forged)) {
            mvc.perform(get("/api/v1/pessoa/getTodasPessoas").header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized());
        }
        verifyNoInteractions(facade);
    }

    @Test
    void disablingOrRemovingUserInvalidatesExistingToken() throws Exception {
        String token = bearer("reader@example.com");
        doReturn(User.withUsername("reader@example.com").password(passwordHash)
                .authorities("VISUALIZAR").disabled(true).build())
                .when(users).loadUserByUsername("reader@example.com");
        mvc.perform(get("/api/v1/pessoa/getTodasPessoas").header("Authorization", token))
                .andExpect(status().isUnauthorized());
        doThrow(new UsernameNotFoundException("removed")).when(users).loadUserByUsername("reader@example.com");
        mvc.perform(get("/api/v1/pessoa/getTodasPessoas").header("Authorization", token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void permissionChangesApplyToExistingToken() throws Exception {
        String token = bearer("reader@example.com");
        allow("reader@example.com", "EDITAR");
        mvc.perform(get("/api/v1/pessoa/getTodasPessoas").header("Authorization", token))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.status").value(403));
        verifyNoInteractions(facade);
    }

    @Test
    void editorCanUpdateWithoutCreatePermission() throws Exception {
        mvc.perform(post("/api/v1/pessoa/1").header("Authorization", bearer("editor@example.com"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Maria\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.idpessoa").value(1));
        verify(facade).atualizarPessoa(any());
    }

    @Test
    void editorCannotCreateAndCreatorCannotUpdate() throws Exception {
        mvc.perform(post("/api/v1/pessoa/criarPessoa").header("Authorization", bearer("editor@example.com"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Maria\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/pessoa/1").header("Authorization", bearer("creator@example.com"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Maria\"}"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(facade);
    }

    @Test
    void creatorCanCreate() throws Exception {
        mvc.perform(post("/api/v1/pessoa/criarPessoa").header("Authorization", bearer("creator@example.com"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Maria\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void deletionStillRequiresBothAdminRoleAndDeletePermission() throws Exception {
        allow("deleter@example.com", "EXCLUIR");
        allow("role-only@example.com", "ROLE_ADMIN");
        for (String email : List.of("deleter@example.com", "role-only@example.com")) {
            mvc.perform(delete("/api/v1/pessoa/1").header("Authorization", bearer(email)))
                    .andExpect(status().isForbidden());
        }
        verify(facade, never()).removerPessoa(anyInt());
        mvc.perform(delete("/api/v1/pessoa/1").header("Authorization", bearer("admin@example.com")))
                .andExpect(status().isOk());
    }

    @Test
    void loginIgnoresOldInvalidTokenAndOtherAuthPathsStayProtected() throws Exception {
        mvc.perform(post("/api/v1/auth/login").servletPath("/api/v1/auth/login")
                        .header("Authorization", "Bearer expired-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"reader@example.com\",\"senha\":\"test-password\"}"))
                .andExpect(status().isOk());
        mvc.perform(get("/api/v1/auth/other")).andExpect(status().isUnauthorized());
    }

    @TestConfiguration
    @EnableWebMvc
    @Import({SecurityConfig.class, AuthController.class, PessoaController.class, ApiSecurityErrors.class})
    static class TestConfig {
        @Bean UsuarioRN users() { return mock(UsuarioRN.class); }
        @Bean SessionFacadeADM facade() { return mock(SessionFacadeADM.class); }
        @Bean ObjectMapper objectMapper() { return new ObjectMapper().findAndRegisterModules(); }
        @Bean JwtService jwtService() {
            return new JwtService(Base64.getEncoder().encodeToString(KEY.getEncoded()), "AtlanticAPI", 900);
        }
    }
}
