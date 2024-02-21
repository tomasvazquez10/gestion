package com.gestion.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "reparto")
public class Reparto {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long idReparto;
    @Column
    private int nroReparto;
    @Column
    private String diaSemana;
    @Column
    private String zonaEntrega;
    @Column
    private boolean activo;

    @JsonIgnore
    @OneToMany(mappedBy = "reparto")
    private Set<Cliente> clientes;

    public Reparto(int nroReparto, String diaSemana, String zonaEntrega) {
        this.nroReparto = nroReparto;
        this.diaSemana = diaSemana;
        this.zonaEntrega = zonaEntrega;
        this.activo = true;
    }

    public Reparto() {}

    public Set<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(Set<Cliente> clientes) {
        this.clientes = clientes;
    }

    public Long getId() {
        return idReparto;
    }

    public void setId(Long idReparto) {
        this.idReparto = idReparto;
    }

    public int getNroReparto() {
        return nroReparto;
    }

    public void setNroReparto(int nroReparto) {
        this.nroReparto = nroReparto;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getZonaEntrega() {
        return zonaEntrega;
    }

    public void setZonaEntrega(String zonaEntrega) {
        this.zonaEntrega = zonaEntrega;
    }

    @Override
    public String toString() {
        return "Reparto{" +
                "id=" + idReparto +
                ", nroReparto=" + nroReparto +
                ", diaSemana='" + diaSemana + '\'' +
                '}';
    }
}
