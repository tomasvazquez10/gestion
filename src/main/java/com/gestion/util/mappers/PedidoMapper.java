package com.gestion.util.mappers;

import com.gestion.dto.PedidoDTO;
import com.gestion.dto.ProductoDTO;
import com.gestion.model.Pedido;

import java.util.Set;

public class PedidoMapper {

    public static PedidoDTO getPedidoDTO(Pedido pedido, Set<ProductoDTO> productos){
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setId(pedido.getId());
        pedidoDTO.setDniCliente(pedido.getCliente().getDni());
        pedidoDTO.setEstadoTexto(pedido.getEstadoTexto());
        pedidoDTO.setFecha(pedido.getFecha());
        pedidoDTO.setProductos(productos);
        pedidoDTO.setPrecioTotal(pedido.getPrecioTotal());

        return pedidoDTO;
    }
}
