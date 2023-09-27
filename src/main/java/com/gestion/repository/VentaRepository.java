package com.gestion.repository;

import com.gestion.model.Pedido;
import com.gestion.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    Optional<Venta> findByPedido(Pedido pedido);
}
