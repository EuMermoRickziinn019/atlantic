package com.atlantic.models.dto.cmc;

import java.math.BigDecimal;

public record EstoqueRequestDTO(
        Integer idEstoque,
        Integer idproduto,
        BigDecimal qtdeProduto,
        BigDecimal qtdeReserva,
        BigDecimal qtdeMin,
        BigDecimal qtdeMaximo
) {
}
