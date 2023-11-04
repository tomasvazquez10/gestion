package com.gestion.controller;

import com.gestion.dto.FacturaDTO;
import com.gestion.dto.PedidoDTO;
import com.gestion.dto.ProductoDTO;
import com.gestion.model.*;
import com.gestion.repository.*;
import com.gestion.util.GeneratePDFReport;
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
@RequestMapping("/pedido")
public class PedidoController {
    
    private final PedidoRepository repository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final ArticuloRepository articuloRepository;
    private final RepartoRepository repartoRepository;
    
    @Autowired
    public PedidoController(PedidoRepository repository, ProductoRepository productoRepository, ClienteRepository clienteRepository,
                            ArticuloRepository articuloRepository, RepartoRepository repartoRepository) {
        this.repository = repository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
        this.articuloRepository = articuloRepository;
        this.repartoRepository = repartoRepository;
    }

    @RequestMapping("/{id}")
    public ResponseEntity<PedidoDTO> getPedido(@PathVariable Long id) {

        Optional<Pedido> optionalPedido = repository.findById(id);
        if (optionalPedido.isPresent()){
            PedidoDTO pedidoDTO = PedidoMapper.getPedidoDTO(optionalPedido.get(),getProductosDTO(optionalPedido.get().getProductos()));
            return new ResponseEntity<>(pedidoDTO, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new PedidoDTO(),HttpStatus.NO_CONTENT);
        }

    }

    @RequestMapping("/all")
    public ResponseEntity<List<PedidoDTO>> getPedidos(){
        try {
            List<PedidoDTO> pedidos = getPedidosDTO(repository.findAllByEstadoTextoNotOrderByFechaDesc("CANCELADO"));

            if (pedidos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(pedidos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/dniCliente/{dniCliente}")
    public ResponseEntity<List<PedidoDTO>> getPedidosByDniCliente(@PathVariable String dniCliente) {

        try {
            List<PedidoDTO> pedidos = new ArrayList<>();
            Optional<Cliente> optionalCliente = clienteRepository.findClienteByDni(dniCliente);
            if (optionalCliente.isPresent()){
                pedidos = getPedidosDTO(repository.findAllByClienteOrderByFechaDesc(optionalCliente.get()));
            }


            if (pedidos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(pedidos, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @RequestMapping("/cliente/{idCliente}")
    public ResponseEntity<List<PedidoDTO>> getPedidosByIdCliente(@PathVariable Long idCliente) {

        try {
            List<PedidoDTO> pedidos = new ArrayList<>();
            Optional<Cliente> optCliente = clienteRepository.findById(idCliente);
            if (!optCliente.isPresent()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            pedidos = getPedidosDTO(repository.findAllByClienteOrderByFechaDesc(optCliente.get()));

            if (pedidos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(pedidos, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @RequestMapping("/reparto/{nroReparto}")
    public ResponseEntity<List<PedidoDTO>> getPedidosByNroReparto(@PathVariable int nroReparto) {

        try {
            List<PedidoDTO> pedidos = new ArrayList<>();
            List<Reparto> repartos = repartoRepository.findAllByNroRepartoAndActivoTrueOrderByNroReparto(nroReparto);
            List<Cliente> clientes = new ArrayList<>();
            if (!repartos.isEmpty()){
                clientes = clienteRepository.findByRepartoAndActivoTrue(repartos.get(0));
            }


            pedidos = getPedidosDTO(repository.findAllByClienteIn(clientes));

            if (pedidos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(pedidos, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping()
    public ResponseEntity<Pedido> crearPedido(@RequestBody PedidoDTO pedidoDTO) {
        try {

            if(pedidoDTO.getFecha() == null || pedidoDTO.getDniCliente() == null){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            Cliente cliente = clienteRepository.findClienteByDni(pedidoDTO.getDniCliente()).get();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaDate = sdf.parse(pedidoDTO.getFechaStr());
            Pedido nuevoPedido = repository.save(new Pedido(fechaDate, 0, cliente, pedidoDTO.getPrecioTotal()));

            List<Producto> nuevosProds = new ArrayList<>();
            for(ProductoDTO productoDTO : pedidoDTO.getProductos()){
                Articulo articulo = articuloRepository.findById(productoDTO.getNroArticulo()).get();
                Producto producto = new Producto(nuevoPedido, articulo, productoDTO.getCantidad());
                productoRepository.save(producto);
                restarStock(producto);
                nuevosProds.add(producto);
            }
            //restar saldo
            cliente.setSaldo( cliente.getSaldo() - pedidoDTO.getPrecioTotal());
            clienteRepository.save(cliente);

            //seteo productos
            nuevoPedido.setProductos(nuevosProds);
            repository.save(nuevoPedido);

            return new ResponseEntity<>(new Pedido(), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/factura/{id}")
    public ResponseEntity<InputStreamResource> getFacturaPDF(@PathVariable Long id) {

        Optional<Pedido> optionalPedido = repository.findById(id);
        PedidoDTO pedidoDTO = PedidoMapper.getPedidoDTO(optionalPedido.get(),getProductosDTO(optionalPedido.get().getProductos()));
        Optional<Cliente> optionalCliente = clienteRepository.findClienteByDni(pedidoDTO.getDniCliente());

        FacturaDTO facturaDTO = new FacturaDTO();
        facturaDTO.setPedido(pedidoDTO);
        facturaDTO.setCliente(optionalCliente.get());
        facturaDTO.setNumero(pedidoDTO.getId().intValue());

        ByteArrayInputStream bis = GeneratePDFReport.getFacturaPDF(facturaDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=factura.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));


    }

    @PostMapping("/edit")
    public ResponseEntity<Pedido> editPedido(@RequestBody PedidoDTO newPedido) {
        return repository.findById(newPedido.getId())
                .map(pedido -> {
                    pedido.setFecha(newPedido.getFecha());
                    pedido.setEstadoTexto(newPedido.getEstadoTexto());
                    if(newPedido.getEstadoTexto().equals("CANCELADO")){
                        for (ProductoDTO productoDTO: newPedido.getProductos()) {
                            sumarStock(productoDTO);
                        }
                        pedido.setEstado(3);
                    }else if (newPedido.getEstadoTexto().equals("ENTREGADO")){
                        pedido.setEstado(1);
                    }

                    return new ResponseEntity<>(repository.save(pedido), HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(repository.save(new Pedido()), HttpStatus.CREATED));
    }

    @PostMapping("/pdf")
    public ResponseEntity<InputStreamResource> getListadoPDF(@RequestBody Pedido pedido){

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=listado-clientes.pdf");

        ByteArrayInputStream bis = GeneratePDFReport.getPedidoPDF(pedido);

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));


    }


    private void restarStock(Producto producto) {

        Articulo articulo = producto.getPk().getArticulo();
        articulo.setStock(articulo.getStock() - producto.getCantidad());
        articuloRepository.save(articulo);

    }

    private void sumarStock(ProductoDTO producto){
        Articulo articulo = articuloRepository.findById(producto.getId()).get();
        articulo.setStock(articulo.getStock() + producto.getCantidad());
        articuloRepository.save(articulo);
    }

    private Set<ProductoDTO> getProductosDTO(List<Producto> productos){
        Set<ProductoDTO> productoDTOS = new HashSet<>();
        for(Producto producto : productos){
            productoDTOS.add(ProductoMapper.getProductoDTO(producto));
        }
        return productoDTOS;
    }

    private List<PedidoDTO> getPedidosDTO(List<Pedido> pedidos){
        return pedidos.stream()
                .map( pedido -> PedidoMapper.getPedidoDTO(pedido, getProductosDTO(pedido.getProductos())))
                .collect(Collectors.toList());
    }




    
}
