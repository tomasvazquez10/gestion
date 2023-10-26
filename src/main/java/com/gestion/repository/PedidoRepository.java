package com.gestion.repository;

import com.gestion.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findAllByDniClienteOrderByFechaDesc(String dniCliente);
    List<Pedido> findAllByEstadoTextoNotOrderByFechaDesc(String estadoTexto);
    List<Pedido> findAllByDniClienteIn(List<String> dniClientes);

}
