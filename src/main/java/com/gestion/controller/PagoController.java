package com.gestion.controller;

import com.gestion.dto.PagoDTO;
import com.gestion.model.*;
import com.gestion.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/pago")
public class PagoController {

    private final PagoRepository repository;
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;

    @Autowired
    public PagoController(PagoRepository repository, PedidoRepository pedidoRepository, ClienteRepository clienteRepository) {
        this.repository = repository;
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
    }

    @RequestMapping("/{id}")
    public ResponseEntity<Pago> getPago(@PathVariable Long id) {

        Optional<Pago> optionalPago = repository.findById(id);
        if (optionalPago.isPresent()){
            return new ResponseEntity<>(optionalPago.get(), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new Pago(),HttpStatus.NOT_FOUND);
        }

    }

    @RequestMapping("/saldoPendiente/{idPedido}")
    public ResponseEntity<Double> getSaldoPendienteByPedido(@PathVariable Long idPedido) {

        Optional<Pedido> optionalPedido = pedidoRepository.findById(idPedido);
        if (optionalPedido.isPresent()){
            Pedido pedido = optionalPedido.get();
            Double saldo = optionalPedido.get().getPrecioTotal() - pedido.getTotalPagos();
            return new ResponseEntity<>(saldo, HttpStatus.OK);

        }else{
            return new ResponseEntity<>(-1.0,HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping()
    public ResponseEntity<Pago> crearPago(@RequestBody PagoDTO pago) {
        try {
            Pedido pedido = pedidoRepository.findById(pago.getIdPedido()).get();
            Pago nuevoPago = repository
                        .save(new Pago(pago.getMonto(),pago.getFecha(),pago.getFormaPago(),pago.getDescuento(),pedido));


            //sumo al saldo el pago
            Cliente cliente = pedido.getCliente();
            cliente.setSaldo(cliente.getSaldo() + pago.getMonto());
            clienteRepository.save(cliente);

            //si la suma de pagos cancela el total del pedido, cambio el estado del pedido
            double totalPagos = 0;
            for (Pago p : repository.findAllByPedido(pedido)){
                totalPagos += p.getMonto();
            }
            if (nuevoPago.getPedido().getPrecioTotal()-totalPagos == 0){
                Pedido nuevoPedido = nuevoPago.getPedido();
                nuevoPedido.setEstado(2);
                nuevoPedido.setEstadoTexto("PAGO");
                pedidoRepository.save(nuevoPedido);
            }

            return new ResponseEntity<>(nuevoPago, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/all")
    public ResponseEntity<List<Pago>> getPagos(){
        try {
            List<Pago> pagos = repository.findAll();

            if (pagos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(pagos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/pedido/{idPedido}")
    public ResponseEntity<List<Pago>> getPagosByIdPedido(@PathVariable Long idPedido){
        try {
            Optional<Pedido> optionalPedido = pedidoRepository.findById(idPedido);
            if (optionalPedido.isPresent()){
                return new ResponseEntity<>(optionalPedido.get().getPagos(), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
