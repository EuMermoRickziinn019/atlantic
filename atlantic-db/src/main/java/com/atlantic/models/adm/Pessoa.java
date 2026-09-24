package com.atlantic.models.adm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Pessoa")
public class Pessoa {
    @Id
    @Column(name = "id_pessoa")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_pessoa")
    @SequenceGenerator(name = "seq_pessoa", sequenceName = "seq_pessoa", allocationSize = 0, initialValue = 1, schema = "public")
    private Integer idpessoa;

    @Column(name = "nome")
    private String nome;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "cpf")
    @JsonIgnore
    private String cpf;

    @Column(name = "cnpj")
    @JsonIgnore
    private String cnpj;

    @Column(name = "email")
    private String email;

    @Column(name = "telefone_fixo")
    private String telefoneFixo;

    @Column(name = "telefone_movel")
    private String telefoneModel;

    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    @Column(name = "data_alteracao")
    private LocalDateTime dataAlteracao;

    @Column(name = "inativo")
    private boolean inativo = false;

    public Integer getIdpessoa() {
        return idpessoa;
    }

    public void setIdpessoa(Integer idpessoa) {
        this.idpessoa = idpessoa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefoneFixo() {
        return telefoneFixo;
    }

    public void setTelefoneFixo(String telefoneFixo) {
        this.telefoneFixo = telefoneFixo;
    }

    public String getTelefoneModel() {
        return telefoneModel;
    }

    public void setTelefoneModel(String telefoneModel) {
        this.telefoneModel = telefoneModel;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public LocalDateTime getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(LocalDateTime dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public boolean isInativo() {
        return inativo;
    }

    public void setInativo(boolean inativo) {
        this.inativo = inativo;
    }
}
