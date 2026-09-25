package com.atlantic.models.cmc;

import com.atlantic.models.adm.Produto;
import com.atlantic.models.user.Usuario;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "estoque")
public class Estoque {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "estoque_seq_generator"
    )
    @SequenceGenerator(
            name = "estoque_seq_generator",
            sequenceName = "seq_estoque",
            allocationSize = 1
    )
    @Column(name = "id_estoque", nullable = false)
    private Integer idEstoque;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_produto",
            nullable = false
    )
    private Produto produto;

    @Column(
            name = "qtde_produto",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal qtdeProduto = BigDecimal.ZERO;

    @Column(
            name = "qtde_reserva",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal qtdeReserva = BigDecimal.ZERO;

    @Column(
            name = "qtde_min",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal qtdeMin = BigDecimal.ZERO;

    @Column(
            name = "qtde_maximo",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal qtdeMaximo = BigDecimal.ZERO;

    @Column(
            name = "data_criacao",
            nullable = false
    )
    private LocalDateTime dataCriacao;

    @Column(name = "data_modificacao")
    private LocalDateTime dataModificacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_user_criacao",
            nullable = false
    )
    private Usuario usuarioCriacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_alteracao")
    private Usuario usuarioAlteracao;

    public Estoque() {
    }

    public Integer getIdEstoque() {
        return idEstoque;
    }

    public void setIdEstoque(Integer idEstoque) {
        this.idEstoque = idEstoque;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public BigDecimal getQtdeProduto() {
        return qtdeProduto;
    }

    public void setQtdeProduto(BigDecimal qtdeProduto) {
        this.qtdeProduto = qtdeProduto;
    }

    public BigDecimal getQtdeReserva() {
        return qtdeReserva;
    }

    public void setQtdeReserva(BigDecimal qtdeReserva) {
        this.qtdeReserva = qtdeReserva;
    }

    public BigDecimal getQtdeMin() {
        return qtdeMin;
    }

    public void setQtdeMin(BigDecimal qtdeMin) {
        this.qtdeMin = qtdeMin;
    }

    public BigDecimal getQtdeMaximo() {
        return qtdeMaximo;
    }

    public void setQtdeMaximo(BigDecimal qtdeMaximo) {
        this.qtdeMaximo = qtdeMaximo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataModificacao() {
        return dataModificacao;
    }

    public void setDataModificacao(LocalDateTime dataModificacao) {
        this.dataModificacao = dataModificacao;
    }

    public Usuario getUsuarioCriacao() {
        return usuarioCriacao;
    }

    public void setUsuarioCriacao(Usuario usuarioCriacao) {
        this.usuarioCriacao = usuarioCriacao;
    }

    public Usuario getUsuarioAlteracao() {
        return usuarioAlteracao;
    }

    public void setUsuarioAlteracao(Usuario usuarioAlteracao) {
        this.usuarioAlteracao = usuarioAlteracao;
    }

    @Transient
    public BigDecimal getQuantidadeDisponivel() {
        return qtdeProduto.subtract(qtdeReserva);
    }
}