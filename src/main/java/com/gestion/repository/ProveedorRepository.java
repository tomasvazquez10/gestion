package com.gestion.repository;

import com.gestion.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    List<Proveedor> findAllByNombreStartingWithIgnoreCaseAndActivoTrue(String nombre);
    List<Proveedor> findAllByNombreFantasiaStartingWithIgnoreCaseAndActivoTrue(String nombreFantasia);
    List<Proveedor> findAllByCuitStartingWithAndActivoTrue(String cuit);

    Optional<Proveedor> findProveedorByCuit(String cuit);
}
