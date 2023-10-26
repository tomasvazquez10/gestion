package com.gestion.util.mappers;


import com.gestion.dto.PagoDTO;
import com.gestion.model.Pago;
import com.gestion.model.Venta;

public class PagoMapper {

    public static PagoDTO getPagoDTO(Pago pago, Venta venta){
        PagoDTO pagoDTO = new PagoDTO();

        pagoDTO.setId(pago.getId());
        pagoDTO.setFormaPago(pago.getFormaPago());
        pagoDTO.setDescuento(pago.getDescuento());
        pagoDTO.setFecha(pago.getFecha());
        pagoDTO.setMonto(pago.getMonto());
        pagoDTO.setDniCliente(venta.getPedido().getDniCliente());
        pagoDTO.setIdPedido(venta.getPedido().getId());

        return pagoDTO;
    }
}
