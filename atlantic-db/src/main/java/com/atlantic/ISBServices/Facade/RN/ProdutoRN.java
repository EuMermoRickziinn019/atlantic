package com.atlantic.ISBServices.Facade.RN;

import com.atlantic.models.adm.Produto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProdutoRN {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Produto criarProduto(Produto produto) {
        em.persist(produto);
        em.flush();
        return produto;
    }

    @Transactional
    public List<Produto> getTodosProdutos() {
        String jpql = "SELECT p FROM Produto p";
        List<Produto> produto = em.createQuery(jpql, Produto.class)
                .getResultList();
        return produto;
    }

    @Transactional
    public Produto consultarProduto(Integer id) {
        if(id != null) {
            return em.find(Produto.class, id);
        }
        return null;
    }

    @Transactional
    public Produto atualizarProduto(Produto produto) {
        if(produto.getIdProduto() != null) {
            produto.setDataAtualizacao(LocalDateTime.now());
            Produto regisAtual = em.merge(produto);
            em.flush();
            return regisAtual;
        } else {
            return this.criarProduto(produto);
        }
    }

    @Transactional
    public void removerProduto(Produto produto) {
        try {
            if(produto.getIdProduto() != null) {
                em.remove(produto);
            }
        } catch (PersistenceException e) {
            throw new RuntimeException(
                    "Não é possível excluir este produto pois ele possui vínculos no sistema.",e);
        }
    }
}