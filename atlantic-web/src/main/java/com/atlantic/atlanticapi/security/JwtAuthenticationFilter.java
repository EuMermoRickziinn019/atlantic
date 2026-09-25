package com.atlantic.atlanticapi.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Instanciado apenas na SecurityFilterChain, evitando registro duplicado como filtro servlet.
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService users;
    private final ApiSecurityErrors errors;
    private final AccountStatusUserDetailsChecker accountChecker = new AccountStatusUserDetailsChecker();

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService users, ApiSecurityErrors errors) {
        this.jwtService = jwtService;
        this.users = users;
        this.errors = errors;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return "POST".equals(request.getMethod()) && "/api/v1/auth/login".equals(request.getServletPath());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null) {
            chain.doFilter(request, response);
            return;
        }
        try {
            if (!header.regionMatches(true, 0, "Bearer ", 0, 7) || header.substring(7).isBlank()) {
                throw new BadCredentialsException("Token inválido.");
            }
            String email = jwtService.subject(header.substring(7).trim());
            var user = users.loadUserByUsername(email);
            // Recarregar o usuário aplica bloqueios e alterações de permissão a cada requisição.
            accountChecker.check(user);
            var authentication = UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            var context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        } catch (JwtException | IllegalArgumentException | AuthenticationException ex) {
            SecurityContextHolder.clearContext();
            errors.commence(request, response, new BadCredentialsException("Token inválido."));
            return;
        }
        chain.doFilter(request, response);
    }
}
