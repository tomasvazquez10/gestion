package com.gestion.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Column
    private String dni;
    @Column
    private String nombre;
    @Column
    private String nombreFantasia;
    @Column
    private String email;
    @Column
    private String direccion;
    @Column
    private String telefono;
    @Column
    private double saldo;

    @ManyToOne()
    @JoinColumn(name = "reparto_id", nullable = false)
    private Reparto reparto;

    @JsonManagedReference
    @OneToMany(mappedBy = "cliente")
    private List<Pedido> pedidos = new ArrayList<>();

    @Column
    private boolean activo;

    public Cliente(){}

    public Cliente(String dni, String nombre, String nombreFantasia, String email, String direccion, String telefono, Reparto reparto) {
        this.dni = dni;
        this.nombre = nombre;
        this.nombreFantasia = nombreFantasia;
        this.email = email;
        this.direccion = direccion;
        this.telefono = telefono;
        this.reparto = reparto;
        this.activo = true;
        this.saldo = 0;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreFantasia() {
        return nombreFantasia;
    }

    public void setNombreFantasia(String nombreFantasia) {
        this.nombreFantasia = nombreFantasia;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Reparto getReparto() {
        return reparto;
    }

    public void setReparto(Reparto reparto) {
        this.reparto = reparto;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", dni='" + dni + '\'' +
                ", nombre='" + nombre + '\'' +
                ", nombreFantasia='" + nombreFantasia + '\'' +
                ", email='" + email + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                ", nroReparto=" + reparto.getNroReparto() +
                '}';
    }
}
