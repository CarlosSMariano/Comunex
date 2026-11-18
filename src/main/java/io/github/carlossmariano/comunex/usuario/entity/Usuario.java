package io.github.carlossmariano.comunex.usuario.entity;

import io.github.carlossmariano.comunex.usuario.enums.TipoPerfil;
import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "USUARIO")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USUARIO")
    private Long idUsuario;

    @Column(name = "ID_BAIRRO", nullable = false)
    private Long idBairro;

    @Column(name = "NOME", nullable = false, length = 120)
    private String nome;

    @Column(name = "EMAIL", nullable = false, length = 150)
    private String email;

    @Column(name = "SENHA", nullable = false, length = 255)
    private String senha;

    @Column(name = "DESCRICAO", length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_PERFIL", nullable = false, length = 20)
    private TipoPerfil tipoPerfil;

    @Column(
            name = "DATA_CADASTRO",
            insertable = false,
            updatable = false
    )
    private LocalDateTime dataCadastro;

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdBairro() {
        return idBairro;
    }

    public void setIdBairro(Long idBairro) {
        this.idBairro = idBairro;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoPerfil getTipoPerfil() {
        return tipoPerfil;
    }

    public void setTipoPerfil(TipoPerfil tipoPerfil) {
        this.tipoPerfil = tipoPerfil;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}