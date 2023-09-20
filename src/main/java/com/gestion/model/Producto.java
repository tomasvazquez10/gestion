package com.gestion.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "PRODUCTOS")
public class Producto {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Column
    private Long nroArticulo;
    @Column
    private int cantidad;
    @Column
    private Double precio;

    //@JsonBackReference
    @JsonIgnore
    @ManyToOne()
    //@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    public Producto(Long nroArticulo, int cantidad, Double precio, Pedido pedido) {
        this.nroArticulo = nroArticulo;
        this.cantidad = cantidad;
        this.precio = precio;
        this.pedido = pedido;
    }

    public Producto() {
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

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
}
