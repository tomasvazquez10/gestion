package com.gestion.controller;

import com.gestion.dto.PagoDTO;
import com.gestion.model.*;
import com.gestion.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping("/pago")
public class PagoController {

    private final PagoRepository repository;
    private final VentaRepository ventaRepository;
    private final PedidoRepository pedidoRepository;
    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;

    @Autowired
    public PagoController(PagoRepository repository, VentaRepository ventaRepository, PedidoRepository pedidoRepository, CuentaRepository cuentaRepository, ClienteRepository clienteRepository) {
        this.repository = repository;
        this.ventaRepository = ventaRepository;
        this.pedidoRepository = pedidoRepository;
        this.cuentaRepository = cuentaRepository;
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
            Double saldo = optionalPedido.get().getPrecioTotal() - pedido.getVenta().getTotalPagos();
            return new ResponseEntity<>(saldo, HttpStatus.OK);

        }else{
            return new ResponseEntity<>(-1.0,HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping()
    public ResponseEntity<Pago> crearPago(@RequestBody PagoDTO pagoDTO) {
        try {
            Optional<Pedido> optionalPedido = pedidoRepository.findById(pagoDTO.getIdPedido());
            Pago nuevoPago = new Pago();
            if (optionalPedido.isPresent()){
                Venta venta = optionalPedido.get().getVenta();
                nuevoPago = repository
                        .save(new Pago(pagoDTO.getMonto(),pagoDTO.getFecha(),pagoDTO.getFormaPago(),pagoDTO.getDescuento(),venta));
                //sumo el pago a las venta
                Set<Pago> pagoSets = new HashSet<>();
                if(venta.getPagos() !=null && !venta.getPagos().isEmpty()){
                    pagoSets = venta.getPagos();
                }
                pagoSets.add(nuevoPago);
                venta.setPagos(pagoSets);
                ventaRepository.save(venta);

                //sumo a la cuenta el pago
                Cuenta cuenta = getCuentaByDniCliente(optionalPedido.get().getDniCliente());
                cuenta.setSaldo(cuenta.getSaldo() + nuevoPago.getMonto());
                cuentaRepository.save(cuenta);

                //si la suma de pagos cancela el total del pedido, cambio el estado del pedido
                double totalPagos = 0;
                for (Pago pago : pagoSets){
                    totalPagos += pago.getMonto();
                }
                if (optionalPedido.get().getPrecioTotal()-totalPagos == 0){
                    Pedido nuevoPedido = optionalPedido.get();
                    nuevoPedido.setEstado(3);
                    nuevoPedido.setEstadoTexto("PAGO");
                    pedidoRepository.save(nuevoPedido);
                }
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
