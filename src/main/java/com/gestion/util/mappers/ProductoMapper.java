package com.gestion.util.mappers;

import com.gestion.dto.ProductoDTO;
import com.gestion.model.Articulo;
import com.gestion.model.Producto;

public class ProductoMapper {

    public static ProductoDTO getProductoDTO(Articulo articulo, Producto producto){
        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setId(producto.getId());
        productoDTO.setNombre(articulo.getNombre());
        productoDTO.setCantidad(producto.getCantidad());
        productoDTO.setPrecio(producto.getPrecio());
        productoDTO.setNroArticulo(producto.getNroArticulo());

        return productoDTO;
    }
}
