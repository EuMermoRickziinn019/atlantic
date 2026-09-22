package com.atlantic.atlanticapi.models.dto.Login;

import java.util.Set;

public record LoginResponseDTO(
        String nome,
        String email,
        Set<String> autorizacoes,
        String mensagem) {
}
