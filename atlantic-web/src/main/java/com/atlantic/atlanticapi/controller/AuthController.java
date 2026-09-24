package com.atlantic.atlanticapi.controller;

import com.atlantic.ISBServices.Facade.RN.UsuarioRN;
import com.atlantic.models.dto.adm.Login.LoginRequestDTO;
import com.atlantic.models.dto.adm.Login.LoginResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRN usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager auth,
                          UsuarioRN usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = auth;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO dto) {
        var usuarioBanco = usuarioRepository.loadUserByUsername(dto.email());

        if (usuarioBanco != null) {
            System.out.println(">>> [DEBUG] Senha enviada no JSON: " + dto.senha());
            System.out.println(">>> [DEBUG] Hash no banco: " + usuarioBanco.getPassword());
            boolean bate = passwordEncoder.matches(dto.senha(), usuarioBanco.getPassword());
            System.out.println(">>> [DEBUG] BCrypt bate? " + bate);
        } else {
            System.out.println(">>> [DEBUG] Usuário NÃO encontrado no banco com o e-mail: " + dto.email());
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.email(), dto.senha())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            var autorizacoes = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            return ResponseEntity.ok(new LoginResponseDTO(
                    userDetails.getUsername(),
                    userDetails.getUsername(),
                    autorizacoes,
                    "Autenticação realizada com sucesso!"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Erro na autenticação: " + e.getMessage());
        }
    }
}
