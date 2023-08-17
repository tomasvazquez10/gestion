package com.gestion.model;

import javax.persistence.*;

@Entity
@Table(name = "reparto")
public class Reparto {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Column
    private int nroReparto;
    @Column
    private String diaSemana;
    @Column
    private boolean activo;

    public Reparto(int nroReparto, String diaSemana) {
        this.nroReparto = nroReparto;
        this.diaSemana = diaSemana;
        this.activo = true;
    }

    public Reparto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "Reparto{" +
                "id=" + id +
                ", nroReparto=" + nroReparto +
                ", diaSemana='" + diaSemana + '\'' +
                '}';
    }
}
