package com.gestion.repository;

import com.gestion.model.Pedido;
import com.gestion.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findAllByPedido(Pedido pedido);
}
