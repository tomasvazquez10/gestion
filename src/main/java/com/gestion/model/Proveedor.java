package com.gestion.model;

import javax.persistence.*;

@Entity
@Table(name = "proveedor")
public class Proveedor {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Column
    private String cuit;
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
    private boolean activo;

    public Proveedor(){}

    public Proveedor(String cuit, String nombre, String nombreFantasia, String email, String direccion, String telefono) {
        this.cuit = cuit;
        this.nombre = nombre;
        this.nombreFantasia = nombreFantasia;
        this.email = email;
        this.direccion = direccion;
        this.telefono = telefono;
        this.activo = true;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
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
                ", cuit='" + cuit + '\'' +
                ", nombre='" + nombre + '\'' +
                ", nombreFantasia='" + nombreFantasia + '\'' +
                ", email='" + email + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}

