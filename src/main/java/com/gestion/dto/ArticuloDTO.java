package com.gestion.dto;

import com.gestion.model.Proveedor;

import javax.persistence.Column;

public class ArticuloDTO {

    private Long id;
    private String nombre;
    private Long nroArticulo;
    private String descripcion;
    private Proveedor proveedor;
    private String cuitProveedor;
    private int stock;
    private Double precio;
    private int ventasTotales;

    public ArticuloDTO() {
    }

    public ArticuloDTO(Long id, String nombre, Long nroArticulo, String descripcion, String cuitProveedor, int stock, Double precio) {
        this.id = id;
        this.nombre = nombre;
        this.nroArticulo = nroArticulo;
        this.descripcion = descripcion;
        this.cuitProveedor = cuitProveedor;
        this.stock = stock;
        this.precio = precio;
    }

    public int getVentasTotales() {
        return ventasTotales;
    }

    public void setVentasTotales(int ventasTotales) {
        this.ventasTotales = ventasTotales;
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

    public Long getNroArticulo() {
        return nroArticulo;
    }

    public void setNroArticulo(Long nroArticulo) {
        this.nroArticulo = nroArticulo;
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

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getCuitProveedor() {
        return cuitProveedor;
    }

    public void setCuitProveedor(String cuitProveedor) {
        this.cuitProveedor = cuitProveedor;
    }
}
