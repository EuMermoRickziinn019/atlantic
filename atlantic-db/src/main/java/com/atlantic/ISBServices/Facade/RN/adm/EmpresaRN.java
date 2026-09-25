package com.atlantic.ISBServices.Facade.RN.adm;

import com.atlantic.models.adm.Empresa;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmpresaRN {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Empresa criarEmpresa(Empresa empresa) {
        em.persist(empresa);
        em.flush();
        return empresa;
    }

    @Transactional
    public List<Empresa> getTodasEmpresas() {
        String jpql = "SELECT p FROM Empresa p";
        List<Empresa> empresas = em.createQuery(jpql, Empresa.class)
                .getResultList();
        return empresas;
    }

    @Transactional
    public Empresa consultarEmpresa(Integer id) {
        return em.find(Empresa.class, id);
    }

    @Transactional
    public Empresa atualizarEmpresa(Empresa empresa) {
        if(empresa.getIdEmpresa() != null) {
            empresa.setDataAtualizacao(LocalDateTime.now());
            Empresa regisAtual = em.merge(empresa);
            em.flush();
            return regisAtual;
        } else {
            return this.criarEmpresa(empresa);
        }
    }

    @Transactional
    public void removerEmpresa(Empresa empresa) {
        try {
            if(empresa.getIdEmpresa() != null) {
                em.remove(empresa);
            }
        } catch (PersistenceException e) {
            throw new RuntimeException(
                "Não é possível excluir este produto pois ele possui vínculos no sistema.",e);
        }
    }
}
