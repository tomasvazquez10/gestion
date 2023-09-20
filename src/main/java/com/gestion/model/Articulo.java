package com.gestion.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "articulo")
public class Articulo implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Column
    private String nombre;
    @Column
    private String descripcion;
    @Column
    private String cuitProveedor;
    @Column
    private int stock;
    @Column
    private boolean activo;

    @Column
    private Long nroArticulo;

    public Articulo(String nombre, String descripcion, String cuitProveedor) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.cuitProveedor = cuitProveedor;
        activo = true;
        stock = 0;
    }

    public Articulo() {
    }

    public Long getNroArticulo() {
        return nroArticulo;
    }

    public void setNroArticulo(Long nroArticulo) {
        this.nroArticulo = nroArticulo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCuitProveedor() {
        return cuitProveedor;
    }

    public void setCuitProveedor(String cuitProveedor) {
        this.cuitProveedor = cuitProveedor;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "Articulo{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", cuitProveedor='" + cuitProveedor + '\'' +
                ", activo=" + activo +
                '}';
    }
}
