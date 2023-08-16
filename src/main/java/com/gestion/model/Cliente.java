package com.gestion.model;

import javax.persistence.*;

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
    private int nroReparto;
    @Column
    private boolean activo;

    public Cliente(){}

    public Cliente(String dni, String nombre, String nombreFantasia, String email, String direccion, String telefono, int nroReparto) {
        this.dni = dni;
        this.nombre = nombre;
        this.nombreFantasia = nombreFantasia;
        this.email = email;
        this.direccion = direccion;
        this.telefono = telefono;
        this.nroReparto = nroReparto;
        this.activo = true;
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

    public int getNroReparto() {
        return nroReparto;
    }

    public void setNroReparto(int nroReparto) {
        this.nroReparto = nroReparto;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
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
                ", nroReparto=" + nroReparto +
                '}';
    }
}
