package io.github.carlossmariano.comunex.bairro.entity;

import jakarta.persistence.*;

@Entity
@Table(name="BAIRRO")
public class Bairro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idBairro;

    @Column(name="NOME", nullable = false, length = 100)
    private String nome;

    @Column(name="MUNICIPIO", nullable = false, length = 100)
    private String municipio;

    @Column(name="ESTADO", nullable = false, length = 2)
    private String estado;

    public long getIdBairro() {
        return idBairro;
    }

    public void setIdBairro(long idBairro) {
        this.idBairro = idBairro;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
