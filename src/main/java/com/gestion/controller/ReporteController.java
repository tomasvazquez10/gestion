package com.gestion.controller;

import com.gestion.dto.ArticuloDTO;
import com.gestion.dto.PagoDTO;
import com.gestion.dto.PedidoDTO;
import com.gestion.dto.ProductoDTO;
import com.gestion.model.*;
import com.gestion.repository.*;
import com.gestion.util.GeneratePDFReport;
import com.gestion.util.mappers.ArticuloMapper;
import com.gestion.util.mappers.PagoMapper;
import com.gestion.util.mappers.PedidoMapper;
import com.gestion.util.mappers.ProductoMapper;
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

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ArticuloRepository articuloRepository;
    private final RepartoRepository repartoRepository;


    @Autowired
    public ReporteController(PedidoRepository pedidoRepository, ClienteRepository clienteRepository, ArticuloRepository articuloRepository, RepartoRepository repartoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.articuloRepository = articuloRepository;
        this.repartoRepository = repartoRepository;
    }

    @RequestMapping("/pagos/cliente/{dniCliente}")
    public ResponseEntity<List<PagoDTO>> getPagosByDniCliente(@PathVariable String dniCliente) {

        List<Pedido> pedidos = new ArrayList<>();
        Optional<Cliente> optionalCliente = clienteRepository.findClienteByDni(dniCliente);
        if (optionalCliente.isPresent()){
            pedidos = pedidoRepository.findAllByClienteOrderByFechaDesc(optionalCliente.get());
        }
        List<PagoDTO> pagos = new ArrayList<>();

        pedidos.forEach(pedido -> {
            pedido.getPagos().forEach( pago -> pagos.add(PagoMapper.getPagoDTO(pago)));
        });

        return new ResponseEntity<>(pagos,HttpStatus.OK);
    }

    @RequestMapping("/pagos/reparto/{nroReparto}")
    public ResponseEntity<List<PagoDTO>> getPagosByNroReparto(@PathVariable int nroReparto) {

        //String diaSemana = DiaSemanaConverter.getDiaSemanaByFecha(fecha);
        List<Reparto> repartos = repartoRepository.findAllByNroRepartoAndActivoTrueOrderByNroReparto(nroReparto);
        List<Cliente> clientes = new ArrayList<>();
        if (!repartos.isEmpty()){
            clientes = clienteRepository.findByRepartoAndActivoTrue(repartos.get(0));
        }
        List<Pedido> pedidos = new ArrayList<>();
        clientes.forEach(cliente -> pedidoRepository.findAllByClienteOrderByFechaDesc(cliente)
                .forEach(pedido -> pedidos.add(pedido)));

        List<PagoDTO> pagos = new ArrayList<>();
        pedidos.forEach(pedido -> {
            pedido.getPagos().forEach( pago -> pagos.add(PagoMapper.getPagoDTO(pago)));
        });

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
    public ResponseEntity<List<PedidoDTO>> getPedidosByFecha(@PathVariable String fecha) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaDate = sdf.parse(fecha);
            List<Pedido> pedidos = pedidoRepository.findAllByFecha(fechaDate);

            if (pedidos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(getPedidosDTO(pedidos), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @RequestMapping("pedidos/fechas/{fechaDesde}/{fechaHasta}")
    public ResponseEntity<List<PedidoDTO>> getPedidosByFechas(@PathVariable String fechaDesde, @PathVariable String fechaHasta) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            List<Pedido> pedidos = pedidoRepository.findAllByFechaBetweenOrderByFechaAsc(sdf.parse(fechaDesde),sdf.parse(fechaHasta));

            if (pedidos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(getPedidosDTO(pedidos), HttpStatus.OK);

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

    @PostMapping("/articulos/pdf")
    public ResponseEntity<InputStreamResource> getArticulosPDF(@RequestBody List<ArticuloDTO> articulos){

        ByteArrayInputStream bis = GeneratePDFReport.getArticuloDTOPDF(articulos);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=listado-articulos.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));


    }

    @PostMapping("/pedidos/pdf")
    public ResponseEntity<InputStreamResource> getPedidosPDF(@RequestBody List<PedidoDTO> pedidos){

        ByteArrayInputStream bis = GeneratePDFReport.getPedidosPDF(pedidos);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=listado-pedidos.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));


    }

    private List<ArticuloDTO> getListaArticulosMasVendidos(List<Pedido> pedidos){

        try {
            List<Producto> productos = pedidos.stream().flatMap(
                    pedido -> pedido.getProductos().stream())
                    .collect(Collectors.toList());
            Map<Long, Integer> articuloMap = new HashMap<>();
            for (Producto producto : productos) {
                Long nroArticulo = producto.getPk().getArticulo().getId();
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

    private List<PedidoDTO> getPedidosDTO(List<Pedido> pedidos){
        return pedidos.stream().map(pedido -> PedidoMapper.getPedidoDTO(pedido, getProductosDTO(pedido.getProductos()))).collect(Collectors.toList());
    }

    private Set<ProductoDTO> getProductosDTO(List<Producto> productos){
        Set<ProductoDTO> productoDTOS = new HashSet<>();
        for(Producto producto : productos){
            productoDTOS.add(ProductoMapper.getProductoDTO(producto));
        }
        return productoDTOS;
    }
}
