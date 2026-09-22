package com.atlantic.atlanticapi.models.adm;

import jakarta.persistence.*;

@Entity
@Table(name = "permissao")
public class Permissao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permissao")
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nome; // Ex: "PESSOA_CRIAR", "PESSOA_EXCLUIR"

    public Permissao() {
    }

    public Permissao(String nome) {
        this.nome = nome;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}