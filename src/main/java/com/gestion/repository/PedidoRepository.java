package com.gestion.repository;

import com.gestion.model.Cliente;
import com.gestion.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findAllByClienteOrderByFechaDesc(Cliente dniCliente);
    List<Pedido> findAllByEstadoTextoNotOrderByFechaDesc(String estadoTexto);
    List<Pedido> findAllByClienteIn(List<Cliente> clientes);
    List<Pedido> findAllByFecha(Date fecha);
    List<Pedido> findAllByFechaBetweenOrderByFechaAsc(Date fechaDesde, Date fechaHasta);
}
