package com.atlantic.models.dto.adm.Login;

import java.time.Instant;
import java.util.Set;

public record LoginResponseDTO(
        String nome,
        String email,
        Set<String> autorizacoes,
        String mensagem,
        String accessToken,
        String tokenType,
        long expiresIn,
        Instant expiresAt) {
}
