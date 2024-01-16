package com.gestion.util.mappers;

import com.gestion.dto.GastoDTO;
import com.gestion.model.Pedido;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GastoMapper {

    public static List<GastoDTO> getGastoDTOList(List<Pedido> pedidos){
        List<GastoDTO> gastoDTOList = new ArrayList<>();
        gastoDTOList = pedidos.stream().map( pedido -> {
            return new GastoDTO(pedido.getIdPedido(),pedido.getPrecioTotal(),pedido.getFecha());
        }).collect(Collectors.toList());
        return gastoDTOList;
    }
}
