package com.gestion.dto;

import com.gestion.model.Cliente;

import java.util.Set;

public class FacturaDTO {

    private int numero;
    private PedidoDTO pedido;
    private Set<PagoDTO> pagos;
    private Cliente cliente;

    public FacturaDTO() {}

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public PedidoDTO getPedido() {
        return pedido;
    }

    public void setPedido(PedidoDTO pedido) {
        this.pedido = pedido;
    }

    public Set<PagoDTO> getPagos() {
        return pagos;
    }

    public void setPagos(Set<PagoDTO> pagos) {
        this.pagos = pagos;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}
