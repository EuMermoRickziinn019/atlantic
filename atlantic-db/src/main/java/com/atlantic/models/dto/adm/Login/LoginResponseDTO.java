package com.atlantic.models.dto.adm.Login;

import java.util.Set;

public record LoginResponseDTO(
        String nome,
        String email,
        Set<String> autorizacoes,
        String mensagem) {
}
