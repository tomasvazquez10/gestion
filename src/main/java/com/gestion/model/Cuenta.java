package com.gestion.model;

import javax.persistence.*;

@Entity
@Table(name = "cuenta")
public class Cuenta {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Column
    private Long idUsuario;
    @Column
    private double saldo;

    public Cuenta(Long idUsuario) {
        this.idUsuario = idUsuario;
        this.saldo = 0;
    }

    public Cuenta(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    @Override
    public String toString() {
        return "Cuenta{" +
                "id=" + id +
                ", idUsuario=" + idUsuario +
                ", saldo=" + saldo +
                '}';
    }
}
