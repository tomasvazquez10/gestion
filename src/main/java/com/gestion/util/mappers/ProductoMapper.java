package com.gestion.util.mappers;

import com.gestion.dto.ProductoDTO;
import com.gestion.model.Articulo;
import com.gestion.model.Producto;

public class ProductoMapper {

    public static ProductoDTO getProductoDTO(Producto producto){
        Articulo articulo = producto.getPk().getArticulo();
        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setId(articulo.getId());
        productoDTO.setNombre(articulo.getNombre());
        productoDTO.setCantidad(producto.getCantidad());
        productoDTO.setPrecio(articulo.getPrecio());
        productoDTO.setNroArticulo(articulo.getNroArticulo());

        return productoDTO;
    }
}
