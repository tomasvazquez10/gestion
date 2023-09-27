package com.gestion.controller;

import com.gestion.model.Pago;
import com.gestion.repository.PagoRepository;
import com.gestion.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/pago")
public class PagoController {

    private final PagoRepository repository;
    private final VentaRepository ventaRepository;

    @Autowired
    public PagoController(PagoRepository repository, VentaRepository ventaRepository) {
        this.repository = repository;
        this.ventaRepository = ventaRepository;
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

    @PostMapping()
    public ResponseEntity<Pago> crearPago(@RequestBody Pago pago) {
        try {
            Pago nuevoPago = repository
                    .save(new Pago(pago.getMonto(),pago.getFecha(),pago.getFormaPago(),pago.getDescuento(),pago.getVenta()));

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
}
