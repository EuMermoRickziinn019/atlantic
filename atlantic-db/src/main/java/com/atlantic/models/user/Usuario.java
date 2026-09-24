package com.atlantic.models.user;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuario")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(nullable = false)
    private String senha;

    private boolean inativo;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_perfil",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "perfil_id")
    )
    private Set<Perfil> perfis = new HashSet<>();
    @Override
    public String getPassword() {
        return this.senha;
    }
    @Override
    public String getUsername() {
        return this.email;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        perfis.forEach(perfil -> {
            // Adiciona a Role (ex: ROLE_ADMIN)
            authorities.add(new SimpleGrantedAuthority(perfil.getNome()));

            // Adiciona as Permissões individuais (ex: PESSOA_CRIAR)
            if (perfil.getPermissoes() != null) {
                perfil.getPermissoes().forEach(permissao ->
                        authorities.add(new SimpleGrantedAuthority(permissao.getNome()))
                );
            }
        });

        return authorities;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.FALSE.equals(this.inativo);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getEmail() {
        return email;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Set<Perfil> getPerfis() { return perfis; }
    public void setPerfis(Set<Perfil> perfis) { this.perfis = perfis; }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
