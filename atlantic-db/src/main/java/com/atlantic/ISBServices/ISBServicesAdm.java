package com.atlantic.ISBServices;

import com.atlantic.models.adm.Empresa;
import com.atlantic.models.adm.Pessoa;
import com.atlantic.models.adm.Produto;

import java.util.List;

public interface ISBServicesAdm {
    //Pessoa
    public Pessoa criarPessoa(Pessoa pessoa);
    public List<Pessoa> getTodasPessoas();
    public Pessoa consultarPessoa(Integer id);
    public Pessoa atualizarPessoa(Pessoa pessoa);
    public boolean removerPessoa(Integer id);

    //Produto
    public Produto criarProduto(Produto produto);
    public List<Produto> getTodosProdutos();
    public Produto consultarProduto(Integer id);
    public Produto atualizarProduto(Produto produto);
    public void removerProduto(Produto produto);

    //Empresa
    public Empresa criarEmpresa(Empresa empresa);
    public List<Empresa> getTodosEmpresa();
    public Empresa consultarEmpresa(Integer id);
    public Empresa atualizarEmpresa(Empresa empresa);
    public void removerEmpresa(Empresa empresa);
}
