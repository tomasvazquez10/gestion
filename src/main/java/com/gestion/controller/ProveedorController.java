package com.gestion.controller;

import com.gestion.model.Proveedor;
import com.gestion.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/proveedor")
public class ProveedorController {

    private final ProveedorRepository repository;

    @Autowired
    public ProveedorController(ProveedorRepository repository) {
        this.repository = repository;
    }


    @RequestMapping("/{id}")
    public ResponseEntity<Proveedor> getProveedor(@PathVariable Long id) {

        Optional<Proveedor> optionalProveedor = repository.findById(id);
        return new ResponseEntity<>(optionalProveedor.get(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Proveedor> crearProveedor(@RequestBody Proveedor proveedor) {
        try {
            Proveedor nuevoProveedor = repository
                    .save(new Proveedor(proveedor.getCuit(),
                            proveedor.getNombre(),
                            proveedor.getNombreFantasia(),
                            proveedor.getEmail(),
                            proveedor.getDireccion(),
                            proveedor.getTelefono()));

            return new ResponseEntity<>(nuevoProveedor, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/all")
    public ResponseEntity<List<Proveedor>> getProveedores(){
        try {
            List<Proveedor> proveedores = repository.findAll().stream()
                    .filter(Proveedor::isActivo)
                    .collect(Collectors.toList());


            if (proveedores.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(proveedores, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> updateProveedor(@RequestBody Proveedor newProveedor, @PathVariable Long id){

        return repository.findById(id)
                .map(proveedor -> {
                    proveedor.setNombre(newProveedor.getNombre());
                    proveedor.setNombreFantasia(newProveedor.getNombreFantasia());
                    proveedor.setDireccion(newProveedor.getDireccion());
                    proveedor.setEmail(newProveedor.getEmail());
                    proveedor.setTelefono(newProveedor.getTelefono());
                    proveedor.setCuit(newProveedor.getCuit());
                    return new ResponseEntity<>(repository.save(proveedor), HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(repository.save(newProveedor), HttpStatus.CREATED));
    }

    @RequestMapping("delete/{id}")
    public ResponseEntity deleteProveedor(@PathVariable Long id){

        return repository.findById(id)
                .map(proveedor -> {
                    proveedor.setActivo(false);
                    repository.save(proveedor);
                    return new ResponseEntity(HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
    }
}
