package com.gestion.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "PEDIDO")
public class Pedido {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Column
    private Date fecha;
    @Column
    private int estado;
    @Column
    private String dniCliente;
    @Column
    private Double precioTotal;

    private String estadoTexto;

    //@JsonManagedReference
    @OneToMany(mappedBy = "pedido")
    private Set<Producto> productos;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinTable(name = "pedido_venta",
            joinColumns =
                    { @JoinColumn(name = "PEDIDO_id", referencedColumnName = "id") },
            inverseJoinColumns =
                    { @JoinColumn(name = "VENTA_id", referencedColumnName = "id") })
    private Venta venta;


    public Pedido(Date fecha, int estado, String dniCliente, Double precioTotal, Set<Producto> productos) {
        this.fecha = fecha;
        this.estado = estado;
        this.dniCliente = dniCliente;
        this.precioTotal = precioTotal;
        this.productos = productos;
        this.estadoTexto = "PENDIENTE";
    }

    public Pedido() {
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public Set<Producto> getProductos() {
        return productos;
    }

    public void setProductos(Set<Producto> productos) {
        this.productos = productos;
    }

    public String getDniCliente() {
        return dniCliente;
    }

    public void setDniCliente(String dniCliente) {
        this.dniCliente = dniCliente;
    }

    public Double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(Double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public String getEstadoTexto(){ return estadoTexto; }

    public void setEstadoTexto(String estadoTexto) {
        this.estadoTexto = estadoTexto;
    }
}
