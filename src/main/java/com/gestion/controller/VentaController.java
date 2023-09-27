package com.gestion.controller;

import com.gestion.model.Pago;
import com.gestion.model.Pedido;
import com.gestion.model.Venta;
import com.gestion.repository.PagoRepository;
import com.gestion.repository.PedidoRepository;
import com.gestion.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/venta")
public class VentaController {

    private final VentaRepository repository;
    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;

    @Autowired
    public VentaController(VentaRepository repository, PagoRepository pagoRepository, PedidoRepository pedidoRepository) {
        this.repository = repository;
        this.pagoRepository = pagoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @RequestMapping("/{id}")
    public ResponseEntity<Venta> getVenta(@PathVariable Long id) {

        Optional<Venta> optionalVenta = repository.findById(id);
        if (optionalVenta.isPresent()){
            return new ResponseEntity<>(optionalVenta.get(), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new Venta(),HttpStatus.NOT_FOUND);
        }

    }

    @RequestMapping("/pedido/{idPedido}")
    public ResponseEntity<Venta> getVentaByPedido(@PathVariable Long idPedido) {
        Optional<Pedido> optionalPedido = pedidoRepository.findById(idPedido);
        if (optionalPedido.isPresent()){
            Optional<Venta> optionalVenta = repository.findByPedido(optionalPedido.get());
            if (optionalVenta.isPresent()){
                return new ResponseEntity<>(optionalVenta.get(), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new Venta(),HttpStatus.NOT_FOUND);
    }

    @PostMapping()
    public ResponseEntity<Venta> crearVenta(@RequestBody Venta venta) {
        try {
            Venta nuevaVenta = repository
                    .save(new Venta(venta.getPedido(),venta.getPagos()));
            if(venta.getPagos() != null){
                Set<Pago> pagoList = new HashSet<>();
                for(Pago pago : venta.getPagos()){
                    pagoList.add(pagoRepository.save(pago));
                }
                nuevaVenta.setPagos(pagoList);
            }
            //guardo venta en pedido
            Pedido pedido = nuevaVenta.getPedido();
            pedido.setVenta(nuevaVenta);
            pedidoRepository.save(pedido);

            return new ResponseEntity<>(nuevaVenta, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/all")
    public ResponseEntity<List<Venta>> getVentas(){
        try {
            List<Venta> ventas = repository.findAll();

            if (ventas.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(ventas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
