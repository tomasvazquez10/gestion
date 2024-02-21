package com.gestion.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Detalle_Pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idPedido;
    @Column
    private Date fecha;
    @Column
    private int estado;

    @ManyToOne()
    @JoinColumn(name = "id_cliente", nullable = true)
    private Cliente cliente;
    @Column
    private Double precioTotal;

    private String estadoTexto;

    @JsonManagedReference
    @OneToMany(mappedBy = "pk.pedido")
    private List<Producto> productos = new ArrayList<>();

    @Transient
    public Double getTotalOrderPrice() {
        double sum = 0D;
        List<Producto> productos = getProductos();
        for (Producto p : productos) {
            sum += p.getPrecioTotal();
        }
        return sum;
    }

    @Transient
    public Double getTotalPagos() {
        double sum = 0D;
        List<Pago> pagos = getPagos();
        for (Pago p : pagos) {
            sum += p.getMonto();
        }
        return sum;
    }

    @Transient
    public int getCantidadProductos() {
        return this.productos.size();
    }

    @JsonIgnore
    @OneToMany(mappedBy = "pedido")
    private List<Pago> pagos;

    public Pedido(Date fecha, int estado, Cliente cliente, Double precioTotal) {
        this.fecha = fecha;
        this.estado = estado;
        this.cliente = cliente;
        this.precioTotal = precioTotal;
        this.estadoTexto = "PENDIENTE";
    }

    public Pedido() {
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
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

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
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
