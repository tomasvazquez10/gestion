package com.gestion.controller;

import com.gestion.model.Pedido;
import com.gestion.model.Producto;
import com.gestion.repository.PedidoRepository;
import com.gestion.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping("/pedido")
public class PedidoController {
    
    private final PedidoRepository repository;
    private final ProductoRepository productoRepository;
    
    @Autowired
    public PedidoController(PedidoRepository repository, ProductoRepository productoRepository) {
        this.repository = repository;
        this.productoRepository = productoRepository;
    }

    @RequestMapping("/{id}")
    public ResponseEntity<Pedido> getPedido(@PathVariable Long id) {

        Optional<Pedido> optionalPedido = repository.findById(id);
        if (optionalPedido.isPresent()){
            return new ResponseEntity<>(optionalPedido.get(), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new Pedido(),HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping()
    public ResponseEntity<Pedido> crearPedido(@RequestBody Pedido pedido) {
        try {
            Pedido nuevoPedido = repository
                .save(new Pedido(pedido.getFecha(),pedido.getEstado(),pedido.getProductos()));

            Set<Producto> nuevosProductos = new HashSet<>();
            for (Producto producto: pedido.getProductos()) {
                nuevosProductos.add(productoRepository.save(new Producto(producto.getIdArticulo(),producto.getCantidad(),producto.getPrecio())));
            }


            return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    
}
