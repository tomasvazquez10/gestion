package com.gestion.util.mappers;

import com.gestion.dto.CompraDTO;
import com.gestion.model.Compra;

public class CompraMapper {

    public static CompraDTO getCompraDTO(Compra compra){
        CompraDTO compraDTO = new CompraDTO();
        compraDTO.setId(compra.getIdCompra());
        compraDTO.setArticulo(ArticuloMapper.getArticuloDTOSimple(compra.getArticulo()));
        compraDTO.setFecha(compra.getFecha());
        compraDTO.setPrecioUnidad(compra.getPrecioUnidad());
        compraDTO.setCantidad(compra.getCantidad());
        compraDTO.setActivo(compra.isActivo());

        return compraDTO;
    }
}
