package com.gestion.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "PRODUCTOS")
public class Producto {

    @EmbeddedId
    @JsonIgnore
    private PedidoProductoPK pk;

    @Column(nullable = false)
    private int cantidad;


    public Producto(Pedido pedido, Articulo articulo, int cantidad) {
        pk = new PedidoProductoPK();
        pk.setPedido(pedido);
        pk.setArticulo(articulo);
        this.cantidad = cantidad;
    }

    public Producto() {
    }

    public PedidoProductoPK getPk() {
        return pk;
    }

    public void setPk(PedidoProductoPK pk) {
        this.pk = pk;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecioTotal() { return pk.getArticulo().getPrecio()*cantidad; }
}
