package com.gestion.util.mappers;

import com.gestion.dto.ArticuloDTO;
import com.gestion.model.Articulo;

public class ArticuloMapper {

    public static ArticuloDTO getArticuloDTO(Articulo articulo){
        ArticuloDTO articuloDTO = new ArticuloDTO();
        articuloDTO.setId(articulo.getId());
        articuloDTO.setNombre(articulo.getNombre());
        articuloDTO.setDescripcion(articulo.getDescripcion());
        articuloDTO.setProveedor(articulo.getProveedor());
        articuloDTO.setCuitProveedor(articulo.getProveedor().getCuit());
        articuloDTO.setStock(articulo.getStock());
        articuloDTO.setPrecio(articulo.getPrecio());
        articuloDTO.setNroArticulo(articulo.getNroArticulo());

        return articuloDTO;
    }

    public static ArticuloDTO getArticuloDTOSimple(Articulo articulo){
        ArticuloDTO articuloDTO = new ArticuloDTO();
        articuloDTO.setId(articulo.getId());
        articuloDTO.setNombre(articulo.getNombre());
        articuloDTO.setDescripcion(articulo.getDescripcion());
        articuloDTO.setCuitProveedor(articulo.getProveedor().getCuit());
        articuloDTO.setStock(articulo.getStock());
        articuloDTO.setPrecio(articulo.getPrecio());
        articuloDTO.setNroArticulo(articulo.getNroArticulo());

        return articuloDTO;
    }

    public static ArticuloDTO getArticuloDTOVentas(Articulo articulo, int ventasTotales){
        ArticuloDTO articuloDTO = new ArticuloDTO();
        articuloDTO.setId(articulo.getId());
        articuloDTO.setNombre(articulo.getNombre());
        articuloDTO.setDescripcion(articulo.getDescripcion());
        articuloDTO.setProveedor(articulo.getProveedor());
        articuloDTO.setStock(articulo.getStock());
        articuloDTO.setVentasTotales(ventasTotales);
        articuloDTO.setNroArticulo(articulo.getNroArticulo());

        return articuloDTO;
    }

    public static Articulo getArticulo(ArticuloDTO articuloDTO){
        Articulo articulo = new Articulo();
        articulo.setId(articuloDTO.getId());
        articulo.setNombre(articuloDTO.getNombre());
        articulo.setDescripcion(articuloDTO.getDescripcion());
        articulo.setProveedor(articuloDTO.getProveedor());
        articulo.setStock(articuloDTO.getStock());
        articulo.setNroArticulo(articuloDTO.getNroArticulo());
        articulo.setActivo(true);

        return articulo;
    }
}
