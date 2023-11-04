package com.gestion.util.mappers;


import com.gestion.dto.PagoDTO;
import com.gestion.model.Pago;

public class PagoMapper {

    public static PagoDTO getPagoDTO(Pago pago){
        PagoDTO pagoDTO = new PagoDTO();

        pagoDTO.setId(pago.getId());
        pagoDTO.setFormaPago(pago.getFormaPago());
        pagoDTO.setDescuento(pago.getDescuento());
        pagoDTO.setFecha(pago.getFecha());
        pagoDTO.setMonto(pago.getMonto());
        pagoDTO.setDniCliente(pago.getPedido().getCliente().getDni());
        pagoDTO.setIdPedido(pago.getPedido().getId());

        return pagoDTO;
    }
}
