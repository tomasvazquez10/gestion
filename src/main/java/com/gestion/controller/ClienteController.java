package com.gestion.controller;

import com.gestion.model.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.gestion.repository.ClienteRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class ClienteController {

    private final ClienteRepository repository;

    @Autowired
    public ClienteController(ClienteRepository repository){
        this.repository = repository;
    }


    @RequestMapping("/cliente/{id}")
    public ResponseEntity<Cliente> getCliente(@PathVariable Long id) {

        Optional<Cliente> optCliente = repository.findById(id);
        return new ResponseEntity<>(optCliente.get(),HttpStatus.OK);
    }

    @PostMapping("/cliente")
    public ResponseEntity<Cliente> crearCliente(@RequestBody Cliente cliente) {
        try {
            Cliente nuevoCliente = repository
                    .save(new Cliente(cliente.getDni(),
                            cliente.getNombre(),
                            cliente.getNombreFantasia(),
                            cliente.getEmail(),
                            cliente.getDireccion(),
                            cliente.getTelefono(),
                            cliente.getNroReparto()));

            return new ResponseEntity<>(nuevoCliente, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/clientes")
    public ResponseEntity<List<Cliente>> getClientes(){
        try {
            List<Cliente> clientes = new ArrayList<>();
            clientes = repository.findAll().stream()
                    .filter(Cliente::isActivo)
                    .collect(Collectors.toList());


            if (clientes.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(clientes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/cliente/{id}")
    public ResponseEntity<Cliente> updateCliente(@RequestBody Cliente newCliente, @PathVariable Long id){

        return repository.findById(id)
                .map(cliente -> {
                    cliente.setNombre(newCliente.getNombre());
                    cliente.setNombreFantasia(newCliente.getNombreFantasia());
                    cliente.setDireccion(newCliente.getDireccion());
                    cliente.setEmail(newCliente.getEmail());
                    cliente.setTelefono(newCliente.getTelefono());
                    cliente.setNroReparto(newCliente.getNroReparto());
                    cliente.setDni(newCliente.getDni());
                    return new ResponseEntity<>(repository.save(cliente), HttpStatus.CREATED);
                })
                .orElseGet(() -> new ResponseEntity<>(repository.save(newCliente), HttpStatus.CREATED));
    }

    @RequestMapping("delete/{id}")
    public ResponseEntity deleteCliente(@PathVariable Long id){

        return repository.findById(id)
                .map(cliente -> {
                    cliente.setActivo(false);
                    repository.save(cliente);
                    return new ResponseEntity(HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
    }
}
