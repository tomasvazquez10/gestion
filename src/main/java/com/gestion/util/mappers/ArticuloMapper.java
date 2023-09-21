package com.gestion.util.mappers;

import com.gestion.dto.ArticuloDTO;
import com.gestion.model.Articulo;
import com.gestion.model.PrecioArticulo;

public class ArticuloMapper {

    public static ArticuloDTO getArticuloDTO(Articulo articulo, PrecioArticulo precioArticulo){
        ArticuloDTO articuloDTO = new ArticuloDTO();
        articuloDTO.setId(articulo.getId());
        articuloDTO.setNombre(articulo.getNombre());
        articuloDTO.setDescripcion(articulo.getDescripcion());
        articuloDTO.setCuitProveedor(articulo.getCuitProveedor());
        articuloDTO.setStock(articulo.getStock());
        articuloDTO.setPrecio(precioArticulo.getPrecio());
        articuloDTO.setNroArticulo(articulo.getNroArticulo());

        return articuloDTO;
    }
}
