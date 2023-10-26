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
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/pedido")
public class PedidoController {
    
    private final PedidoRepository repository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final ArticuloRepository articuloRepository;
    private final CuentaRepository cuentaRepository;
    private final VentaRepository ventaRepository;
    
    @Autowired
    public PedidoController(PedidoRepository repository, ProductoRepository productoRepository, ClienteRepository clienteRepository, ArticuloRepository articuloRepository, CuentaRepository cuentaRepository, VentaRepository ventaRepository) {
        this.repository = repository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
        this.articuloRepository = articuloRepository;
        this.cuentaRepository = cuentaRepository;
        this.ventaRepository = ventaRepository;
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
    public ResponseEntity<List<Pedido>> getPedidos(){
        try {
            List<Pedido> pedidos = repository.findAllByEstadoTextoNotOrderByFechaDesc("CANCELADO");

            if (pedidos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(pedidos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/dniCliente/{dniCliente}")
    public ResponseEntity<List<Pedido>> getPedidosByDniCliente(@PathVariable String dniCliente) {

        try {
            List<Pedido> pedidos = new ArrayList<>();
            pedidos = repository.findAllByDniClienteOrderByFechaDesc(dniCliente);

            if (pedidos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(pedidos, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @RequestMapping("/cliente/{idCliente}")
    public ResponseEntity<List<Pedido>> getPedidosByIdCliente(@PathVariable Long idCliente) {

        try {
            List<Pedido> pedidos = new ArrayList<>();
            Optional<Cliente> optCliente = clienteRepository.findById(idCliente);
            if (!optCliente.isPresent()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            pedidos = repository.findAllByDniClienteOrderByFechaDesc(optCliente.get().getDni());

            if (pedidos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(pedidos, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @RequestMapping("/reparto/{nroReparto}")
    public ResponseEntity<List<Pedido>> getPedidosByNroReparto(@PathVariable int nroReparto) {

        try {
            List<Pedido> pedidos = new ArrayList<>();
            List<Cliente> clientes = clienteRepository.findByNroRepartoAndActivoTrue(nroReparto);

            pedidos = repository.findAllByDniClienteIn(clientes.stream()
                    .map(Cliente::getDni).collect(Collectors.toList()));

            if (pedidos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(pedidos, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping()
    public ResponseEntity<Pedido> crearPedido(@RequestBody Pedido pedido) {
        try {

            if(pedido.getFecha() == null || pedido.getDniCliente() == null || pedido.getDniCliente().equals("")){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }

            Pedido nuevoPedido = repository
                .save(new Pedido(pedido.getFecha(),pedido.getEstado(),pedido.getDniCliente(),pedido.getPrecioTotal(),pedido.getProductos()));

            Set<Producto> nuevosProductos = new HashSet<>();
            for (Producto producto: pedido.getProductos()) {
                //resto la cantidad del stock del articulo
                restarStock(producto);
                nuevosProductos.add(productoRepository.save(new Producto(producto.getNroArticulo(),producto.getCantidad(),producto.getPrecio(),nuevoPedido)));
            }
            nuevoPedido.setProductos(nuevosProductos);

            //resto el total de la cuenta
            Cuenta cuenta = getCuentaByDniCliente(pedido.getDniCliente());
            if (cuenta.getId() != null){
                cuenta.setSaldo(cuenta.getSaldo() - pedido.getPrecioTotal());
                Cuenta nuevaCuenta = cuentaRepository.save(cuenta);
            }else{
                //creo cuenta al cliente
                crearCuenta(pedido.getDniCliente(), pedido.getPrecioTotal());
            }
            return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
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
    public ResponseEntity<Pedido> editPedido(@RequestBody Pedido newPedido) {
        return repository.findById(newPedido.getId())
                .map(pedido -> {
                    pedido.setFecha(newPedido.getFecha());
                    pedido.setEstadoTexto(newPedido.getEstadoTexto());
                    pedido.setEstado(newPedido.getEstado());
                    pedido.setDniCliente(newPedido.getDniCliente());
                    pedido.setPrecioTotal(newPedido.getPrecioTotal());
                    pedido.setProductos(newPedido.getProductos());
                    if(newPedido.getEstadoTexto().equals("CANCELADO") && newPedido.getEstado()==0){
                        for (Producto producto: newPedido.getProductos()) {
                            sumarStock(producto);
                        }
                    }

                    return new ResponseEntity<>(repository.save(pedido), HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(repository.save(newPedido), HttpStatus.CREATED));
    }

    @PostMapping("/pdf")
    public ResponseEntity<InputStreamResource> getListadoPDF(@RequestBody Pedido pedido){

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=listado-clientes.pdf");
        Optional<Venta> optionalVenta = ventaRepository.findByPedido(pedido);

        ByteArrayInputStream bis = GeneratePDFReport.getPedidoPDF(pedido,optionalVenta.get());

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));


    }

    private void crearCuenta(String dniCliente, Double precioTotal) {

        try {
            Optional<Cliente> optionalCliente = clienteRepository.findClienteByDni(dniCliente);
            if (optionalCliente.isPresent()){
                Cuenta nuevaCuenta = cuentaRepository.save(new Cuenta(optionalCliente.get().getId(),-precioTotal));
            }

        } catch (Exception e) {
            System.out.println("ERROR");
        }
    }

    private void restarStock(Producto producto) {

        Optional<Articulo> articuloOptional = articuloRepository.findById(producto.getNroArticulo());
        if (articuloOptional.isPresent()) {
            Articulo articulo = articuloOptional.get();
            articulo.setStock(articulo.getStock() - producto.getCantidad());
            articuloRepository.save(articulo);
        }
    }

    private void sumarStock(Producto producto){
        Optional<Articulo> articuloOptional = articuloRepository.findById(producto.getNroArticulo());
        if (articuloOptional.isPresent()) {
            Articulo articulo = articuloOptional.get();
            articulo.setStock(articulo.getStock() + producto.getCantidad());
            articuloRepository.save(articulo);
        }
    }

    private Set<ProductoDTO> getProductosDTO(Set<Producto> productos){
        Set<ProductoDTO> productoDTOS = new HashSet<>();
        for(Producto producto : productos){
            Optional<Articulo> articuloOptional = articuloRepository.findById(producto.getNroArticulo());
            productoDTOS.add(ProductoMapper.getProductoDTO(articuloOptional.get(),producto));
        }
        return productoDTOS;
    }

    private Cuenta getCuentaByDniCliente(String dni) {

        Optional<Cliente> optionalCliente = clienteRepository.findClienteByDni(dni);
        if(optionalCliente.isPresent()){
            Optional<Cuenta> optionalCuenta = cuentaRepository.findCuentaByIdUsuario(optionalCliente.get().getId());
            Cuenta cuenta = (optionalCuenta.isPresent()?optionalCuenta.get():new Cuenta());
            return cuenta;
        }else{
            return new Cuenta();
        }

    }




    
}
