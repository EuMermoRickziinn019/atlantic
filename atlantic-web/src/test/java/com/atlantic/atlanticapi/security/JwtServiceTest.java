package com.atlantic.atlanticapi.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    private static final SecretKey KEY = Jwts.SIG.HS256.key().build();
    private static final String SECRET = Base64.getEncoder().encodeToString(KEY.getEncoded());
    private static final Instant NOW = Instant.parse("2026-09-24T12:00:00Z");
    private final JwtService service = serviceAt(NOW);

    private JwtService serviceAt(Instant instant) {
        return new JwtService(SECRET, "AtlanticAPI", 900, Clock.fixed(instant, ZoneOffset.UTC));
    }

    @Test
    void emitsSignedTokenWithSubjectIssuerAndExpiration() {
        var token = service.issue("maria@example.com");
        assertEquals("maria@example.com", service.subject(token.value()));
        assertEquals(900, token.expiresIn());
        assertEquals(NOW.plusSeconds(900), token.expiresAt());
        var claims = Jwts.parser().verifyWith(KEY).clock(() -> Date.from(NOW))
                .build().parseSignedClaims(token.value()).getPayload();
        assertEquals("AtlanticAPI", claims.getIssuer());
        assertNotNull(claims.getId());
        assertEquals(NOW, claims.getIssuedAt().toInstant());
        assertFalse(claims.containsKey("senha"));
        assertFalse(claims.containsKey("authorities"));
    }

    @Test
    void rejectsExpiredTokenIncludingExactExpirationInstant() {
        String token = service.issue("maria@example.com").value();
        assertThrows(JwtException.class, () -> serviceAt(NOW.plusSeconds(900)).subject(token));
        assertThrows(JwtException.class, () -> serviceAt(NOW.plusSeconds(901)).subject(token));
    }

    @Test
    void rejectsSignatureFromAnotherKey() {
        String token = claims().signWith(Jwts.SIG.HS256.key().build()).compact();
        assertThrows(JwtException.class, () -> service.subject(token));
    }

    @Test
    void rejectsTamperedSubject() {
        String token = service.issue("maria@example.com").value();
        String[] parts = token.split("\\.");
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]), java.nio.charset.StandardCharsets.UTF_8);
        parts[1] = Base64.getUrlEncoder().withoutPadding().encodeToString(
                payload.replace("maria@example.com", "admin@example.com").getBytes(java.nio.charset.StandardCharsets.UTF_8));
        assertThrows(JwtException.class, () -> service.subject(String.join(".", parts)));
    }

    @Test
    void rejectsWrongIssuer() {
        String token = claims().issuer("AnotherAPI").signWith(KEY).compact();
        assertThrows(JwtException.class, () -> service.subject(token));
    }

    @Test
    void rejectsUnsignedAndMalformedTokens() {
        assertThrows(JwtException.class, () -> service.subject(claims().compact()));
        assertThrows(JwtException.class, () -> service.subject("not.a.jwt"));
    }

    @Test
    void requiresSubjectExpirationAndIssuedAt() {
        String missingExpiry = Jwts.builder().issuer("AtlanticAPI").subject("maria@example.com")
                .issuedAt(Date.from(NOW)).signWith(KEY).compact();
        String missingSubject = claims().subject(null).signWith(KEY).compact();
        String missingIssuedAt = claims().issuedAt(null).signWith(KEY).compact();
        assertThrows(JwtException.class, () -> service.subject(missingExpiry));
        assertThrows(JwtException.class, () -> service.subject(missingSubject));
        assertThrows(JwtException.class, () -> service.subject(missingIssuedAt));
    }

    @Test
    void rejectsFutureIssuedAt() {
        String token = claims().issuedAt(Date.from(NOW.plusSeconds(60))).signWith(KEY).compact();
        assertThrows(JwtException.class, () -> service.subject(token));
    }

    @Test
    void rejectsOtherHmacAlgorithmsEvenWithSameKey() {
        SecretKey longKey = Keys.hmacShaKeyFor(new byte[64]);
        var hs256Only = new JwtService(Base64.getEncoder().encodeToString(longKey.getEncoded()),
                "AtlanticAPI", 900, Clock.fixed(NOW, ZoneOffset.UTC));
        String token = claims().signWith(longKey, Jwts.SIG.HS512).compact();
        assertThrows(JwtException.class, () -> hs256Only.subject(token));
    }

    @Test
    void rejectsWeakOrInvalidSecretsAndInvalidLifetime() {
        assertThrows(IllegalArgumentException.class, () -> new JwtService("invalid!", "AtlanticAPI", 900));
        assertThrows(IllegalArgumentException.class, () -> new JwtService("YWJj", "AtlanticAPI", 900));
        assertThrows(IllegalArgumentException.class, () -> new JwtService(SECRET, "AtlanticAPI", 0));
        assertThrows(IllegalArgumentException.class, () -> new JwtService(SECRET, "AtlanticAPI", 86401));
        assertThrows(IllegalArgumentException.class, () -> new JwtService(SECRET, "", 900));
    }

    private io.jsonwebtoken.JwtBuilder claims() {
        return Jwts.builder().issuer("AtlanticAPI").subject("maria@example.com")
                .issuedAt(Date.from(NOW)).expiration(Date.from(NOW.plusSeconds(900)));
    }
}
