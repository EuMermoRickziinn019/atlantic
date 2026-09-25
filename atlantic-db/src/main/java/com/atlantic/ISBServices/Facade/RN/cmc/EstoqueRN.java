package com.atlantic.ISBServices.Facade.RN.cmc;

import com.atlantic.models.adm.Empresa;
import com.atlantic.models.cmc.Estoque;
import com.atlantic.models.user.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import org.hibernate.Hibernate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EstoqueRN {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Estoque criarEstoque(Estoque estoque) {
        Usuario userCriacao = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        estoque.setUsuarioCriacao(userCriacao);
        estoque.setDataCriacao(LocalDateTime.now());
        em.persist(estoque);
        em.flush();
        return estoque;
    }

    @Transactional
    public List<Estoque> getTodasEstoque() {
        String jpql = "SELECT e FROM Estoque e";
        List<Estoque> estoque = em.createQuery(jpql, Estoque.class)
                .getResultList();
        if(!estoque.isEmpty() && estoque != null) {
            for(Estoque e : estoque) {
                initialize(e);
            }
        }
        return estoque;
    }

    @Transactional
    public Estoque consultarEstoque(Integer id) {
        Estoque e = em.find(Estoque.class, id);
        initialize(e);
        return e;
    }

    @Transactional
    public Estoque atualizarEstoque(Estoque estoque) {
        if(estoque.getIdEstoque() != null) {
            Usuario userAlteracao = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            estoque.setUsuarioAlteracao(userAlteracao);
            estoque.setDataModificacao(LocalDateTime.now());
            Estoque regisAtual = em.merge(estoque);
            em.flush();
            return regisAtual;
        } else {
            return this.criarEstoque(estoque);
        }
    }

    @Transactional
    public void removerEstoque(Estoque estoque) {
        try {
            if(estoque.getIdEstoque() != null) {
                em.remove(estoque);
            }
        } catch (PersistenceException e) {
            throw new RuntimeException(
                    "Não é possível excluir este produto pois ele possui vínculos no sistema.",e);
        }
    }

    @Transactional
    public void initialize(Estoque estoque) {
        if(estoque.getProduto() != null) {
            Hibernate.initialize(estoque.getProduto());
        }
        if(estoque.getUsuarioCriacao() != null) {
            Hibernate.initialize(estoque.getUsuarioCriacao());
        }
        if(estoque.getUsuarioAlteracao() != null) {
            Hibernate.initialize(estoque.getUsuarioAlteracao());
        }
    }
}
