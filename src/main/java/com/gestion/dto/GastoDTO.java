package com.gestion.dto;

import java.util.Date;

public class GastoDTO {

    private Long idPedido;
    private double monto;
    private Date fecha;

    public GastoDTO() {}

    public GastoDTO(Long idPedido, double monto, Date fecha) {
        this.idPedido = idPedido;
        this.monto = monto;
        this.fecha = fecha;
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
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
}
