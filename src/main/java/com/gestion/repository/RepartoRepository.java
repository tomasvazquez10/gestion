package com.gestion.repository;

import com.gestion.model.Reparto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepartoRepository extends JpaRepository<Reparto, Long> {


}
