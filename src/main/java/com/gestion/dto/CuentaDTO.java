package com.gestion.dto;

import com.gestion.model.Compra;
import com.gestion.model.Pago;

import java.util.ArrayList;
import java.util.List;

public class CuentaDTO {

    private Long id;
    private Long idUsuario;
    private double saldo;
    private String dniCliente;
    private List<Pago> pagos;
    private List<CompraDTO> compras;
    private List<GastoDTO> gastos;

    public CuentaDTO() {}

    public CuentaDTO(Long id, Long idUsuario, double saldo, String dniCliente){
        this.id = id;
        this.idUsuario = idUsuario;
        this.saldo = saldo;
        this.dniCliente = dniCliente;
        this.pagos = new ArrayList<>();
        this.compras = new ArrayList<>();
        this.gastos = new ArrayList<>();
    }

    public String getDniCliente() {
        return dniCliente;
    }

    public void setDniCliente(String dniCliente) {
        this.dniCliente = dniCliente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }

    public List<GastoDTO> getGastos() {
        return gastos;
    }

    public void setGastos(List<GastoDTO> gastos) {
        this.gastos = gastos;
    }

    public List<CompraDTO> getCompras() {
        return compras;
    }

    public void setCompras(List<CompraDTO> compras) {
        this.compras = compras;
    }
}
