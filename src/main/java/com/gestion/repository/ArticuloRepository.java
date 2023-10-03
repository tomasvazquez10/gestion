package com.gestion.repository;

import com.gestion.model.Articulo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArticuloRepository extends JpaRepository<Articulo, Long> {

    List<Articulo> findAllByCuitProveedorStartingWithAndActivoTrue(String cuitProveedor);
    List<Articulo> findAllByNombreStartingWithIgnoreCaseAndActivoTrue(String nombre);

    Optional<Articulo> getArticuloByNroArticulo(Long nroArticulo);
    Optional<Articulo> findArticuloByNroArticulo(Long nroArticulo);


}
