package com.gestion.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "precio_articulo")
public class PrecioArticulo {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "articulo_id", nullable = false)
    private Articulo articulo;
    @Column
    private Date fecha;
    @Column
    private Double precio;
    @Column
    private boolean activo;

    public PrecioArticulo(Articulo articulo, Date fecha, Double precio) {
        this.articulo = articulo;
        this.fecha = fecha;
        this.precio = precio;
        this.activo = true;
    }

    public PrecioArticulo() {
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

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "PrecioArticulo{" +
                "id=" + id +
                ", idArticulo=" + articulo.getId() +
                ", fecha=" + fecha +
                ", precio=" + precio +
                '}';
    }
}
