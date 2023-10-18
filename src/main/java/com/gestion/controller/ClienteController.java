package com.gestion.controller;

import com.gestion.dto.ArticuloDTO;
import com.gestion.model.Articulo;
import com.gestion.model.Cliente;
import com.gestion.model.Proveedor;
import com.gestion.util.GeneratePDFReport;
import com.gestion.util.mappers.ArticuloMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.gestion.repository.ClienteRepository;


import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/cliente")
public class ClienteController {

    private final ClienteRepository repository;

    @Autowired
    public ClienteController(ClienteRepository repository){
        this.repository = repository;
    }


    @RequestMapping("/{id}")
    public ResponseEntity<Cliente> getCliente(@PathVariable Long id) {

        Optional<Cliente> optCliente = repository.findById(id);
        return new ResponseEntity<>(optCliente.get(),HttpStatus.OK);
    }

    @RequestMapping("/dni/{dni}")
    public ResponseEntity<Cliente> getClienteByDni(@PathVariable String dni) {

        Optional<Cliente> optCliente = repository.findClienteByDni(dni);
        if (optCliente.isPresent()){
            return new ResponseEntity<>(optCliente.get(),HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

    }

    @RequestMapping("/buscar/{campo}/{value}")
    public ResponseEntity<List<Cliente>> findClientesBy(@PathVariable String campo, @PathVariable String value) {
        try{
            List<Cliente> clientes = new ArrayList<>();
            switch (campo){
                case "nombre":
                    clientes = repository.findAllByNombreStartingWithIgnoreCaseAndActivoTrue(value);
                    break;
                case "nombre_fantasia":
                    clientes = repository.findAllByNombreFantasiaStartingWithIgnoreCaseAndActivoTrue(value);
                    break;
                case "nro_reparto":
                    clientes = repository.findByNroRepartoAndActivoTrue(Integer.parseInt(value));
                    break;
                case "dni":
                    clientes = repository.findAllByDniStartingWithAndActivoTrue(value);
                    break;
                default:
                    clientes = new ArrayList<>();
            }
            return new ResponseEntity<>(clientes,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping()
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

    @RequestMapping("/all")
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

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> updateCliente(@PathVariable("id") Long id, @RequestBody Cliente newCliente){

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
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));
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

    @PostMapping("/edit")
    public ResponseEntity<Cliente> editCliente(@RequestBody Cliente newCliente) {
        return repository.findById(newCliente.getId())
                .map(proveedor -> {
                    proveedor.setNombre(newCliente.getNombre());
                    proveedor.setNombreFantasia(newCliente.getNombreFantasia());
                    proveedor.setDireccion(newCliente.getDireccion());
                    proveedor.setEmail(newCliente.getEmail());
                    proveedor.setTelefono(newCliente.getTelefono());
                    proveedor.setDni(newCliente.getDni());
                    proveedor.setNroReparto(newCliente.getNroReparto());
                    return new ResponseEntity<>(repository.save(proveedor), HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(repository.save(newCliente), HttpStatus.CREATED));
    }

    @PostMapping("/pdf")
    public ResponseEntity<InputStreamResource> getListadoPDF(@RequestBody List<Cliente> clientes){

            ByteArrayInputStream bis = GeneratePDFReport.getClientesPDF(clientes);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=listado-clientes.pdf");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(bis));


    }
}
