package com.atlantic.ISBServices.Facade.RN;

import com.atlantic.models.adm.Pessoa;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PessoaRN {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Pessoa criarPessoa(Pessoa pessoa) {
        em.persist(pessoa);
        em.flush();
        return pessoa;
    }

    @Transactional
    public List<Pessoa> getTodasPessoas() {
        String jpql = "SELECT p FROM Pessoa p";
        List<Pessoa> pessoas = em.createQuery(jpql, Pessoa.class)
                .getResultList();
        return pessoas;
    }

    @Transactional
    public Pessoa consultarPessoa(Integer id) {
        if(id != null) {
            return em.find(Pessoa.class, id);
        }
        return null;
    }

    @Transactional
    public Pessoa atualizarPessoa(Pessoa pessoa) {
        if(pessoa.getIdpessoa() != null) {
            pessoa.setDataAlteracao(LocalDateTime.now());
            Pessoa regisAtual = em.merge(pessoa);
            em.flush();
            return regisAtual;
        } else {
            return this.criarPessoa(pessoa);
        }
    }

    @Transactional
    public boolean removerPessoa(Integer id) {
        try {
            Pessoa pessoa = em.find(Pessoa.class, id);
            if(pessoa != null) {
                em.remove(pessoa);
                return true;
            }
            return false;
        } catch (PersistenceException e) {
            throw new RuntimeException(
                    "Não é possível excluir este produto pois ele possui vínculos no sistema.",e);
        }
    }
}
