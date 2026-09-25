package com.atlantic.atlanticapi.core.mapper;

import com.atlantic.models.adm.Pessoa;
import com.atlantic.models.adm.Produto;
import com.atlantic.models.cmc.Estoque;
import com.atlantic.models.dto.adm.Pessoa.PessoaResponseDTO;
import com.atlantic.models.dto.cmc.EstoqueRequestDTO;
import com.atlantic.models.dto.cmc.EstoqueResponseDTO;

public class EstoqueMapper {
    public static Estoque toEntity(EstoqueRequestDTO dto) {
        Estoque estoque = new Estoque();
        estoque.setIdEstoque(dto.idEstoque());
        Produto produto = new Produto();
        produto.setIdProduto(dto.idproduto());
        estoque.setProduto(produto);
        estoque.setQtdeMaximo(dto.qtdeMaximo());
        estoque.setQtdeReserva(dto.qtdeReserva());
        estoque.setQtdeMin(dto.qtdeMin());
        estoque.setQtdeProduto(dto.qtdeProduto());
        return estoque;
    }

    public static EstoqueResponseDTO toDto(Estoque estoque) {
        return new EstoqueResponseDTO(
                estoque.getIdEstoque(),
                estoque.getProduto().getIdProduto(),
                estoque.getQtdeProduto(),
                estoque.getQtdeReserva(),
                estoque.getQtdeMin(),
                estoque.getQtdeMaximo(),
                estoque.getDataCriacao(),
                estoque.getDataModificacao(),
                estoque.getUsuarioCriacao().getNome(),
                estoque.getUsuarioAlteracao().getNome()
        );
    }
}
