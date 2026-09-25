package com.atlantic.ISBServices;

import com.atlantic.models.cmc.Estoque;

import java.util.List;

public interface ISBServicesCmc {
    //Estoque
    public Estoque criarEstoque(Estoque estoque);
    public List<Estoque> getTodosEstoque();
    public Estoque consultarEstoque(Integer id);
    public Estoque atualizarEstoque(Estoque estoque);
    public void removerEstoque(Estoque estoque);
}
