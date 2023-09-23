package com.gestion.controller;

import com.gestion.model.Cliente;
import com.gestion.model.Cuenta;
import com.gestion.repository.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/cuenta")
public class CuentaController {

    private final CuentaRepository repository;

    @Autowired
    public CuentaController(CuentaRepository repository) {
        this.repository = repository;
    }

    @RequestMapping("/{id}")
    public ResponseEntity<Cuenta> getCuenta(@PathVariable Long id) {

        Optional<Cuenta> optionalCuenta = repository.findById(id);
        return new ResponseEntity<>(optionalCuenta.get(), HttpStatus.OK);
    }

    @RequestMapping("/usuario/{idUsuario}")
    public ResponseEntity<Cuenta> getCuentaByUsuario(@PathVariable Long idUsuario) {

        Optional<Cuenta> optionalCuenta = Optional.ofNullable(repository.findCuentaByIdUsuario(idUsuario));
        Cuenta cuenta = (optionalCuenta.isPresent()?optionalCuenta.get():new Cuenta());
        return new ResponseEntity<>(cuenta, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Cuenta> crearCuenta(@RequestBody Cuenta cuenta) {
        try {
            Cuenta nuevaCuenta = repository.save(new Cuenta(cuenta.getIdUsuario(),0));

            return new ResponseEntity<>(nuevaCuenta, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/all")
    public ResponseEntity<List<Cuenta>> getCuentas(){
        try {
            List<Cuenta> cuentas = new ArrayList<>();
            cuentas = repository.findAll();


            if (cuentas.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(cuentas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cuenta> updateCuenta(@RequestBody Cuenta newCuenta, @PathVariable Long id){

        return repository.findById(id)
                .map(cuenta -> {
                    cuenta.setIdUsuario(newCuenta.getIdUsuario());
                    cuenta.setSaldo(newCuenta.getSaldo());

                    return new ResponseEntity<>(repository.save(cuenta), HttpStatus.CREATED);
                })
                .orElseGet(() -> new ResponseEntity<>(repository.save(newCuenta), HttpStatus.CREATED));
    }

    @PostMapping("/pedido")
    public ResponseEntity<Cuenta> restarCuenta(@RequestBody Cuenta newCuenta) {
        return repository.findById(newCuenta.getId())
                .map(cuenta -> {
                    cuenta.setSaldo(cuenta.getSaldo() - newCuenta.getSaldo());

                    return new ResponseEntity<>(repository.save(cuenta), HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(repository.save(newCuenta), HttpStatus.CREATED));
    }

}
