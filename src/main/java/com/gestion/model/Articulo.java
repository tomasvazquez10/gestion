package com.gestion.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "ARTICULO")
public class Articulo implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Column
    private String nombre;
    @Column
    private String descripcion;

    @ManyToOne()
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;
    @Column
    private int stock;
    @Column
    private boolean activo;

    @JsonIgnore
    @OneToMany(mappedBy = "articulo")
    private Set<Compra> compras;

    @JsonIgnore
    @OneToMany(mappedBy = "articulo")
    private Set<PrecioArticulo> precios;

    @Column
    private Long nroArticulo;

    @Column
    private double precio;

    public Set<PrecioArticulo> getPrecios() {
        return precios;
    }

    public void setPrecios(Set<PrecioArticulo> precios) {
        this.precios = precios;
    }

    public Articulo(String nombre, String descripcion, Proveedor proveedor) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.proveedor = proveedor;
        activo = true;
        stock = 0;
    }

    public Articulo(String nombre, String descripcion, Long nroArticulo, Proveedor proveedor, double precio, int stock) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.proveedor = proveedor;
        activo = true;
        this.stock = stock;
        this.precio = precio;
        this.nroArticulo = nroArticulo;
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

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Set<Compra> getCompras() {
        return compras;
    }

    public void setCompras(Set<Compra> compras) {
        this.compras = compras;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "Articulo{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", cuitProveedor='" + proveedor.getCuit() + '\'' +
                ", activo=" + activo +
                '}';
    }
}
