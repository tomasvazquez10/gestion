package com.gestion.controller;

import com.gestion.dto.PagoDTO;
import com.gestion.model.*;
import com.gestion.repository.ClienteRepository;
import com.gestion.repository.PedidoRepository;
import com.gestion.repository.RepartoRepository;
import com.gestion.repository.VentaRepository;
import com.gestion.util.GeneratePDFReport;
import com.gestion.util.mappers.PagoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
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
    public ResponseEntity<List<PagoDTO>> getPagosByDniCliente(@PathVariable String dniCliente) {


        List<Pedido> pedidos = pedidoRepository.findAllByDniClienteOrderByFechaDesc(dniCliente);
        List<Venta> ventas = new ArrayList<>();
        pedidos.forEach(pedido -> {
            Optional<Venta> optionalVenta = ventaRepository.findByPedido(pedido);
            optionalVenta.ifPresent(ventas::add);
        });

        List<PagoDTO> pagos = ventas.stream()
                .flatMap(venta -> venta.getPagos().stream().map( pago -> PagoMapper.getPagoDTO(pago, venta)
                ))
                .collect(Collectors.toList());

        return new ResponseEntity<>(pagos,HttpStatus.OK);
    }

    @RequestMapping("/pagos/reparto/{nroReparto}")
    public ResponseEntity<List<PagoDTO>> getPagosByNroReparto(@PathVariable int nroReparto) {

        //String diaSemana = DiaSemanaConverter.getDiaSemanaByFecha(fecha);
        List<Venta> ventas = new ArrayList<>();
        List<Cliente> clientes = clienteRepository.findByNroRepartoAndActivoTrue(nroReparto);
        List<Pedido> pedidos = new ArrayList<>();
        clientes.forEach(cliente -> pedidoRepository.findAllByDniClienteOrderByFechaDesc(cliente.getDni())
                .forEach(pedido -> pedidos.add(pedido)));

        pedidos.forEach(pedido -> {
            Optional<Venta> optionalVenta = ventaRepository.findByPedido(pedido);
            optionalVenta.ifPresent(ventas::add);
        });

        List<PagoDTO> pagos = ventas.stream()
                .flatMap(venta -> venta.getPagos().stream().map( pago -> PagoMapper.getPagoDTO(pago, venta)
                ))
                .collect(Collectors.toList());

        return new ResponseEntity<>(pagos,HttpStatus.OK);
    }

    @PostMapping("/pagos/pdf")
    public ResponseEntity<InputStreamResource> getListadoPDF(@RequestBody List<PagoDTO> pagos){

        ByteArrayInputStream bis = GeneratePDFReport.getPagosPDF(pagos);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=listado-pagos.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));


    }
}
