package com.gestion.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "COMPRA")
public class Compra {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long idCompra;

    @ManyToOne()
    @JoinColumn(name = "idArticulo", nullable = false)
    private Articulo articulo;

    @ManyToOne()
    @JoinColumn(name = "idProveedor", nullable = false)
    private Proveedor proveedor;

    @Column
    private Date fecha;
    @Column
    private double precioUnidad;
    @Column
    private int cantidad;
    @Column
    private boolean pago;
    @Column
    private boolean activo;

    public Compra() {}

    public Compra(Articulo articulo, Proveedor proveedor, Date fecha, double precioUnidad, int cantidad, boolean pago, boolean activo) {
        this.articulo = articulo;
        this.proveedor = proveedor;
        this.fecha = fecha;
        this.precioUnidad = precioUnidad;
        this.cantidad = cantidad;
        this.pago = pago;
        this.activo = activo;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Long getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Long idCompra) {
        this.idCompra = idCompra;
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

    public boolean isPago() {
        return pago;
    }

    public void setPago(boolean pago) {
        this.pago = pago;
    }
}
