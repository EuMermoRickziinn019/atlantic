package com.atlantic.atlanticapi.controller;

import com.atlantic.atlanticapi.security.JwtService;
import com.atlantic.models.dto.adm.Login.LoginRequestDTO;
import com.atlantic.models.dto.adm.Login.LoginResponseDTO;
import com.atlantic.models.user.Usuario;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO dto) {
        try {
            var authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(dto.email(), dto.senha()));
            var user = (UserDetails) authentication.getPrincipal();
            var token = jwtService.issue(user.getUsername());
            var authorities = user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
            String nome = user instanceof Usuario usuario ? usuario.getNome() : user.getUsername();
            return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(new LoginResponseDTO(
                    nome, user.getUsername(), authorities, "Autenticação realizada com sucesso!",
                    token.value(), "Bearer", token.expiresIn(), token.expiresAt()));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).cacheControl(CacheControl.noStore())
                    .body(Map.of("status", 401, "mensagem", "E-mail ou senha inválidos."));
        }
    }
}
