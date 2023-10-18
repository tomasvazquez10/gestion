package com.gestion.controller;

import com.gestion.model.*;
import com.gestion.repository.ClienteRepository;
import com.gestion.repository.PedidoRepository;
import com.gestion.repository.RepartoRepository;
import com.gestion.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;


@CrossOrigin
@RestController
@RequestMapping("/reporte")
public class ReporteController {

    private final VentaRepository ventaRepository;
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final RepartoRepository repartoRepository;

    @Autowired
    public ReporteController(VentaRepository ventaRepository, RepartoRepository repartoRepository, PedidoRepository pedidoRepository, ClienteRepository clienteRepository) {
        this.ventaRepository = ventaRepository;
        this.repartoRepository = repartoRepository;
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
    }

    @RequestMapping("/pagos/cliente/{dniCliente}")
    public ResponseEntity<List<Pago>> getPagosByDniCliente(@PathVariable String dniCliente) {


        List<Pedido> pedidos = pedidoRepository.findAllByDniClienteOrderByFechaDesc(dniCliente);
        List<Venta> ventas = new ArrayList<>();
        List<Pago> pagos = new ArrayList<>();
        pedidos.forEach(pedido -> {
            Optional<Venta> optionalVenta = ventaRepository.findByPedido(pedido);
            optionalVenta.ifPresent(ventas::add);
        });

        pagos = ventas.stream()
                .flatMap(venta -> venta.getPagos().stream())
                .collect(Collectors.toList());

        return new ResponseEntity<>(pagos,HttpStatus.OK);
    }

    @RequestMapping("/pagos/reparto/{nroReparto}")
    public ResponseEntity<List<Pago>> getPagosByNroReparto(@PathVariable int nroReparto) {

        //String diaSemana = DiaSemanaConverter.getDiaSemanaByFecha(fecha);
        List<Venta> ventas = new ArrayList<>();
        List<Cliente> clientes = clienteRepository.findByNroRepartoAndActivoTrue(nroReparto);
        List<Pedido> pedidos = new ArrayList<>();
        clientes.forEach(cliente -> pedidoRepository.findAllByDniClienteOrderByFechaDesc(cliente.getDni())
                .forEach(pedido -> pedidos.add(pedido)));

        List<Pago> pagos = new ArrayList<>();
        pedidos.forEach(pedido -> {
            Optional<Venta> optionalVenta = ventaRepository.findByPedido(pedido);
            optionalVenta.ifPresent(ventas::add);
        });

        pagos = ventas.stream()
                .flatMap(venta -> venta.getPagos().stream())
                .collect(Collectors.toList());

        return new ResponseEntity<>(pagos,HttpStatus.OK);
    }
}
