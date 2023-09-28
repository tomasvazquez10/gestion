package com.gestion.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "COMPRA")
public class Compra {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "articulo_id", nullable = false)
    private Articulo articulo;
    @Column
    private Date fecha;
    @Column
    private double precioUnidad;
    @Column
    private int cantidad;
    @Column
    private boolean activo;

    public Compra() {}

    public Compra(Articulo articulo, Date fecha, double precioUnidad, int cantidad, boolean activo) {
        this.articulo = articulo;
        this.fecha = fecha;
        this.precioUnidad = precioUnidad;
        this.cantidad = cantidad;
        this.activo = activo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public void setArticulo(Articulo articulo) {
        this.articulo = articulo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getPrecioUnidad() {
        return precioUnidad;
    }

    public void setPrecioUnidad(double precioUnidad) {
        this.precioUnidad = precioUnidad;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
