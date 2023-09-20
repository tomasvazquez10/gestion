package com.gestion.controller;

import com.gestion.model.Articulo;
import com.gestion.model.Cliente;
import com.gestion.model.Pedido;
import com.gestion.model.Producto;
import com.gestion.repository.ArticuloRepository;
import com.gestion.repository.ClienteRepository;
import com.gestion.repository.PedidoRepository;
import com.gestion.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    
    @Autowired
    public PedidoController(PedidoRepository repository, ProductoRepository productoRepository, ClienteRepository clienteRepository, ArticuloRepository articuloRepository) {
        this.repository = repository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
        this.articuloRepository = articuloRepository;
    }

    @RequestMapping("/{id}")
    public ResponseEntity<Pedido> getPedido(@PathVariable Long id) {

        Optional<Pedido> optionalPedido = repository.findById(id);
        if (optionalPedido.isPresent()){
            Pedido pedido = optionalPedido.get();
            pedido.setEstadoTexto(pedido.getEstado());
            return new ResponseEntity<>(pedido, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new Pedido(),HttpStatus.NOT_FOUND);
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
            List<Cliente> clientes = clienteRepository.findByNroReparto(nroReparto);

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

            return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void restarStock(Producto producto) throws Exception {

        Optional<Articulo> articuloOptional = articuloRepository.findById(producto.getNroArticulo());
        if (articuloOptional.isPresent()) {
            Articulo articulo = articuloOptional.get();
            articulo.setStock(articulo.getStock() - producto.getCantidad());
            articuloRepository.save(articulo);
        }
    }

    
    
}
