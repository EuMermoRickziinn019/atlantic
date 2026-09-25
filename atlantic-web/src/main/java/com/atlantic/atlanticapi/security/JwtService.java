package com.atlantic.atlanticapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    private final SecretKey key;
    private final String issuer;
    private final long expirationSeconds;
    private final Clock clock;
    private final JwtParser parser;

    @Autowired
    public JwtService(@Value("${security.jwt.secret}") String secret,
                      @Value("${security.jwt.issuer:AtlanticAPI}") String issuer,
                      @Value("${security.jwt.expiration-seconds:900}") long expirationSeconds) {
        this(secret, issuer, expirationSeconds, Clock.systemUTC());
    }

    JwtService(String secret, String issuer, long expirationSeconds, Clock clock) {
        try {
            this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        } catch (IllegalArgumentException | io.jsonwebtoken.security.WeakKeyException ex) {
            throw new IllegalArgumentException("JWT_SECRET deve ser Base64 de uma chave aleatória com pelo menos 32 bytes.");
        }
        if (issuer == null || issuer.isBlank() || expirationSeconds < 1 || expirationSeconds > 86400) {
            throw new IllegalArgumentException("Configure um emissor JWT e validade entre 1 e 86400 segundos.");
        }
        this.issuer = issuer;
        this.expirationSeconds = expirationSeconds;
        this.clock = clock;
        this.parser = Jwts.parser().verifyWith(key).requireIssuer(issuer)
                .clock(() -> Date.from(clock.instant()))
                .sig().clear().add(Jwts.SIG.HS256).and().build();
    }

    public IssuedToken issue(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("O usuário do token é obrigatório.");
        }
        Instant issuedAt = clock.instant().truncatedTo(ChronoUnit.SECONDS);
        Instant expiresAt = issuedAt.plusSeconds(expirationSeconds);
        String token = Jwts.builder()
                .issuer(issuer).subject(email).id(UUID.randomUUID().toString())
                .issuedAt(Date.from(issuedAt)).expiration(Date.from(expiresAt))
                .signWith(key, Jwts.SIG.HS256).compact();
        return new IssuedToken(token, expirationSeconds, expiresAt);
    }

    public String subject(String token) {
        Claims claims = parser.parseSignedClaims(token).getPayload();
        if (claims.getSubject() == null || claims.getSubject().isBlank()
                || claims.getExpiration() == null || claims.getIssuedAt() == null
                || !claims.getExpiration().toInstant().isAfter(clock.instant())
                || claims.getIssuedAt().toInstant().isAfter(clock.instant())
                || !claims.getExpiration().after(claims.getIssuedAt())) {
            throw new JwtException("Token inválido.");
        }
        return claims.getSubject();
    }

    public record IssuedToken(String value, long expiresIn, Instant expiresAt) {
    }
}
