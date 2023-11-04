package com.gestion.repository;

import com.gestion.model.Cliente;
import com.gestion.model.Reparto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByRepartoAndActivoTrue(Reparto reparto);

    Optional<Cliente> findClienteByDni(String dni);

    List<Cliente> findAllByNombreStartingWithIgnoreCaseAndActivoTrue(String nombre);
    List<Cliente> findAllByNombreFantasiaStartingWithIgnoreCaseAndActivoTrue(String nombreFantasia);
    List<Cliente> findAllByDniStartingWithAndActivoTrue(String dni);
}
