package com.atlantic.models.dto.cmc;

import com.atlantic.models.adm.Produto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EstoqueResponseDTO(
        Integer idEstoque,
        Integer idProduto,
        BigDecimal qtdeProduto,
        BigDecimal qtdeReserva,
        BigDecimal qtdeMin,
        BigDecimal qtdeMaximo,
        LocalDateTime dataCriacao,
        LocalDateTime dataAlteracao,
        String nomeUsuarioCriacao,
        String nomeUsuarioAlteracao
) {
}
