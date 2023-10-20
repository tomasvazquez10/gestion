package com.gestion.repository;

import com.gestion.model.Cliente;
import com.gestion.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    Optional<Cuenta> findCuentaByIdUsuario(Long idUsuario);
}
