package com.atlantic.ISBServices.Facade;

import com.atlantic.ISBServices.Facade.RN.adm.EmpresaRN;
import com.atlantic.ISBServices.Facade.RN.adm.PessoaRN;
import com.atlantic.ISBServices.Facade.RN.adm.ProdutoRN;
import com.atlantic.ISBServices.ISBServicesAdm;
import com.atlantic.models.adm.Empresa;
import com.atlantic.models.adm.Pessoa;
import com.atlantic.models.adm.Produto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionFacadeADM implements ISBServicesAdm {
    private PessoaRN pessoaRN;
    private ProdutoRN produtoRN;
    private EmpresaRN empresaRN;

    public SessionFacadeADM (
            PessoaRN pessoaRN,
            ProdutoRN produtoRN,
            EmpresaRN empresaRN
    ) {
        this.pessoaRN = pessoaRN;
        this.produtoRN = produtoRN;
        this.empresaRN = empresaRN;
    }

    //Pessoa
    @Override
    public Pessoa criarPessoa(Pessoa pessoa) {
        return pessoaRN.criarPessoa(pessoa);
    }

    @Override
    public List<Pessoa> getTodasPessoas() {
        return pessoaRN.getTodasPessoas();
    }

    @Override
    public Pessoa consultarPessoa(Integer id) {
        return pessoaRN.consultarPessoa(id);
    }

    @Override
    public Pessoa atualizarPessoa(Pessoa pessoa) {
        return pessoaRN.atualizarPessoa(pessoa);
    }

    @Override
    public boolean removerPessoa(Integer id) {
        return pessoaRN.removerPessoa(id);
    }

    //Produto
    @Override
    public Produto criarProduto(Produto produto) {
        return produtoRN.criarProduto(produto);
    }

    @Override
    public List<Produto> getTodosProdutos() {
        return produtoRN.getTodosProdutos();
    }

    @Override
    public Produto consultarProduto(Integer id) {
        return produtoRN.consultarProduto(id);
    }

    @Override
    public Produto atualizarProduto(Produto produto) {
        return produtoRN.atualizarProduto(produto);
    }

    @Override
    public void removerProduto(Produto produto) {
        produtoRN.removerProduto(produto);
    }

    //Empresa
    @Override
    public Empresa criarEmpresa(Empresa empresa) {
        return empresaRN.criarEmpresa(empresa);
    }

    @Override
    public List<Empresa> getTodosEmpresa() {
        return empresaRN.getTodasEmpresas();
    }

    @Override
    public Empresa consultarEmpresa(Integer id) {
        return empresaRN.consultarEmpresa(id);
    }

    @Override
    public Empresa atualizarEmpresa(Empresa empresa) {
        return empresaRN.atualizarEmpresa(empresa);
    }

    @Override
    public void removerEmpresa(Empresa empresa) {
        empresaRN.removerEmpresa(empresa);
    }
}
