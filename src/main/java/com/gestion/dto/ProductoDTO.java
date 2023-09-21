package com.gestion.dto;

import javax.persistence.Column;

public class ProductoDTO {

    private Long id;
    private Long nroArticulo;
    private int cantidad;
    private Double precio;
    private String nombre;

    public ProductoDTO() {}

    public ProductoDTO(Long id, Long nroArticulo, int cantidad, Double precio, String nombre) {
        this.id = id;
        this.nroArticulo = nroArticulo;
        this.cantidad = cantidad;
        this.precio = precio;
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNroArticulo() {
        return nroArticulo;
    }

    public void setNroArticulo(Long nroArticulo) {
        this.nroArticulo = nroArticulo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
