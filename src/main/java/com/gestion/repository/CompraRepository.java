package com.gestion.repository;

import com.gestion.model.Articulo;
import com.gestion.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Long> {

    List<Compra> findAllByArticuloOrderByPrecioUnidadDesc(Articulo articulo);
}
