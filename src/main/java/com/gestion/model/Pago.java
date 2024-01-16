package com.gestion.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long idPago;
    @Column
    private Double monto;
    @Column
    private Date fecha;
    @Column
    private String formaPago;
    @Column
    private int descuento;

    @JsonIgnore
    @ManyToOne()
    @JoinColumn(name = "idPedido")
    private Pedido pedido;

    public Pago(){};

    public Pago(Double monto, Date fecha, String formaPago, int descuento, Pedido pedido) {
        this.monto = monto;
        this.fecha = fecha;
        this.formaPago = formaPago;
        this.descuento = descuento;
        this.pedido = pedido;
    }

    public Long getIdPago() {
        return idPago;
    }

    public void setIdPago(Long idPago) {
        this.idPago = idPago;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public int getDescuento() {
        return descuento;
    }

    public void setDescuento(int descuento) {
        this.descuento = descuento;
    }
}
