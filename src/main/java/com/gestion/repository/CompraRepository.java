package com.gestion.repository;

import com.gestion.model.Articulo;
import com.gestion.model.Compra;
import com.gestion.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Long> {

    List<Compra> findAllByArticuloOrderByFechaDesc(Articulo articulo);

    List<Compra> findAllByProveedorAndActivoTrueOrderByFechaDesc(Proveedor proveedor);
    List<Compra> findAllByProveedorAndActivoTrueAndPagoFalse(Proveedor proveedor);
}
