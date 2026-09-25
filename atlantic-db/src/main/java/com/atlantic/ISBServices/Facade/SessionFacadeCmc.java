package com.atlantic.ISBServices.Facade;

import com.atlantic.ISBServices.Facade.RN.cmc.EstoqueRN;
import com.atlantic.ISBServices.ISBServicesCmc;
import com.atlantic.models.cmc.Estoque;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionFacadeCmc implements ISBServicesCmc {
    EstoqueRN estoqueRN;
    public SessionFacadeCmc(
            EstoqueRN estoqueRN
    ) {
        this.estoqueRN = estoqueRN;
    }

    @Override
    public Estoque criarEstoque(Estoque estoque) {
        return this.estoqueRN.criarEstoque(estoque);
    }

    @Override
    public List<Estoque> getTodosEstoque() {
        return this.estoqueRN.getTodasEstoque();
    }

    @Override
    public Estoque consultarEstoque(Integer id) {
        return this.estoqueRN.consultarEstoque(id);
    }

    @Override
    public Estoque atualizarEstoque(Estoque estoque) {
        return this.estoqueRN.atualizarEstoque(estoque);
    }

    @Override
    public void removerEstoque(Estoque estoque) {
        this.estoqueRN.removerEstoque(estoque);
    }
}
