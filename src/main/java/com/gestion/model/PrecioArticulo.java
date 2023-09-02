package com.gestion.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "precio_articulo")
public class PrecioArticulo {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Column
    private Long idArticulo;
    @Column
    private Date fecha;
    @Column
    private Double precio;

    public PrecioArticulo(Long idArticulo, Date fecha, Double precio) {
        this.idArticulo = idArticulo;
        this.fecha = fecha;
        this.precio = precio;
    }

    public PrecioArticulo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(Long idArticulo) {
        this.idArticulo = idArticulo;
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

    @Override
    public String toString() {
        return "PrecioArticulo{" +
                "id=" + id +
                ", idArticulo=" + idArticulo +
                ", fecha=" + fecha +
                ", precio=" + precio +
                '}';
    }
}
