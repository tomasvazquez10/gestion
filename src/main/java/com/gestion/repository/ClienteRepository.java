package com.gestion.repository;

import com.gestion.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByNroReparto(int nroReparto);

    Optional<Cliente> findClienteByDni(String dni);
}
