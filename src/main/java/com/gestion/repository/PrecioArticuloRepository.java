package com.gestion.repository;

import com.gestion.model.Articulo;
import com.gestion.model.PrecioArticulo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrecioArticuloRepository extends JpaRepository<PrecioArticulo, Long> {

    List<PrecioArticulo> getPrecioArticuloByArticuloOrderByFechaDesc(Articulo articulo);

    List<PrecioArticulo> getPrecioArticulosByArticuloOrderByFechaDesc(Articulo articulo);
}
