package com.gestion.controller;

import com.gestion.dto.ArticuloDTO;
import com.gestion.dto.PagoDTO;
import com.gestion.model.*;
import com.gestion.repository.*;
import com.gestion.util.GeneratePDFReport;
import com.gestion.util.mappers.ArticuloMapper;
import com.gestion.util.mappers.PagoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@CrossOrigin
@RestController
@RequestMapping("/reporte")
public class ReporteController {

    private final VentaRepository ventaRepository;
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ArticuloRepository articuloRepository;


    @Autowired
    public ReporteController(VentaRepository ventaRepository, PedidoRepository pedidoRepository, ClienteRepository clienteRepository, ArticuloRepository articuloRepository) {
        this.ventaRepository = ventaRepository;
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.articuloRepository = articuloRepository;
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

    @RequestMapping("pedidos/fecha/{fecha}")
    public ResponseEntity<List<Pedido>> getPedidosByFecha(@PathVariable String fecha) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaDate = sdf.parse(fecha);
            List<Pedido> pedidos = pedidoRepository.findAllByFecha(fechaDate);

            if (pedidos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(pedidos, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @RequestMapping("pedidos/fechas/{fechaDesde}/{fechaHasta}")
    public ResponseEntity<List<Pedido>> getPedidosByFechas(@PathVariable String fechaDesde, @PathVariable String fechaHasta) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            List<Pedido> pedidos = pedidoRepository.findAllByFechaBetweenOrderByFechaAsc(sdf.parse(fechaDesde),sdf.parse(fechaHasta));

            if (pedidos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(pedidos, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @RequestMapping("/articulos/masVentas/{fechaDesde}/{fechaHasta}")
    public ResponseEntity<List<ArticuloDTO>> getArticulosMasVendidos(@PathVariable String fechaDesde, @PathVariable String fechaHasta) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            List<Pedido> pedidos = pedidoRepository.findAllByFechaBetweenOrderByFechaAsc(sdf.parse(fechaDesde), sdf.parse(fechaHasta));
            List<ArticuloDTO> articuloDTOList = getListaArticulosMasVendidos(pedidos);

            if (articuloDTOList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(articuloDTOList, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/articulos/masVendidos")
    public ResponseEntity<List<ArticuloDTO>> getArticulosMasVendidos() {

        try {
            List<ArticuloDTO> articuloDTOList = getListaArticulosMasVendidos(pedidoRepository.findAll());

            if (articuloDTOList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(articuloDTOList, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private List<ArticuloDTO> getListaArticulosMasVendidos(List<Pedido> pedidos){

        try {
            List<Producto> productos = pedidos.stream().flatMap(
                    pedido -> pedido.getProductos().stream())
                    .collect(Collectors.toList());
            Map<Long, Integer> articuloMap = new HashMap<>();
            for (Producto producto : productos) {
                Long nroArticulo = producto.getNroArticulo();
                if (articuloMap.containsKey(nroArticulo)){
                    int cant = articuloMap.remove(nroArticulo);
                    articuloMap.put(nroArticulo,producto.getCantidad()+cant);
                }else {
                    articuloMap.put(nroArticulo,producto.getCantidad());
                }
            }

            List<ArticuloDTO> articuloDTOS = new ArrayList<>();
            for (Map.Entry<Long, Integer> entry : articuloMap.entrySet()) {
                Long nroArticulo = entry.getKey();
                Integer cantidad = entry.getValue();

                Optional<Articulo> optionalArticulo = articuloRepository.findById(nroArticulo);
                optionalArticulo.ifPresent(articulo -> {
                    articuloDTOS.add(ArticuloMapper.getArticuloDTOVentas(articulo, cantidad));
                });
            }

            articuloDTOS.sort((a, b) -> Integer.compare(b.getVentasTotales(), a.getVentasTotales()));
            return articuloDTOS;
        } catch (Exception e) {
            return new ArrayList<>();
        }

    }
}
