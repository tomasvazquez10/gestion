package com.gestion.dto;

import javax.persistence.Column;
import java.util.Date;

public class PagoDTO {

    private Long id;
    private double monto;
    private Date fecha;
    private String formaPago;
    private int descuento;
    private Long idPedido;

    public PagoDTO() {
    }

    public PagoDTO(Long id, double monto, Date fecha, String formaPago, int descuento, Long idPedido) {
        this.id = id;
        this.monto = monto;
        this.fecha = fecha;
        this.formaPago = formaPago;
        this.descuento = descuento;
        this.idPedido = idPedido;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }
}
