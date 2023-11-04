package com.gestion.repository;

import com.gestion.model.Pago;
import com.gestion.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findAllByPedido(Pedido pedido);
}
