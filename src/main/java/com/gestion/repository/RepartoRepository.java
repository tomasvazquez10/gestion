package com.gestion.repository;

import com.gestion.model.Reparto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepartoRepository extends JpaRepository<Reparto, Long> {

    List<Reparto> findAllByNroRepartoAndActivoTrueOrderByNroReparto(int nroReparto);
    List<Reparto> findAllByDiaSemanaAndActivoTrue(String diaSemana);
    List<Reparto> findAllByActivoTrueOrderByNroRepartoAscDiaSemanaAsc();
}
