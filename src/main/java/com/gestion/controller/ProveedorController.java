package com.gestion.controller;

import com.gestion.dto.ProveedorDTO;
import com.gestion.model.Compra;
import com.gestion.model.Proveedor;
import com.gestion.repository.CompraRepository;
import com.gestion.repository.ProveedorRepository;
import com.gestion.util.mappers.ProveedorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/proveedor")
public class ProveedorController {

    private final ProveedorRepository repository;
    private final CompraRepository compraRepository;

    @Autowired
    public ProveedorController(ProveedorRepository repository, CompraRepository compraRepository) {
        this.repository = repository;
        this.compraRepository = compraRepository;
    }


    @RequestMapping("/{id}")
    public ResponseEntity<ProveedorDTO> getProveedor(@PathVariable Long id) {

        Optional<Proveedor> optionalProveedor = repository.findById(id);
        if (optionalProveedor.isPresent()){
            Proveedor proveedor = optionalProveedor.get();
            return new ResponseEntity<>(ProveedorMapper.getProveedorDTO(proveedor,getSaldoProveedor(proveedor)), HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

    }

    @RequestMapping("/cuit/{cuit}")
    public ResponseEntity<Proveedor> getProveedorByCuit(@PathVariable String cuit) {

        Optional<Proveedor> optionalProveedor = repository.findProveedorByCuit(cuit);
        if (optionalProveedor.isPresent()){
            return new ResponseEntity<>(optionalProveedor.get(),HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

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
    public ResponseEntity<Proveedor> updateProveedor(@PathVariable Long id, @RequestBody Proveedor newProveedor){

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

    @PostMapping("/edit")
    public ResponseEntity<Proveedor> editProveedor(@RequestBody Proveedor newProveedor) {
        return repository.findById(newProveedor.getId())
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

    @RequestMapping("/buscar/{campo}/{value}")
    public ResponseEntity<List<Proveedor>> findProveedorBy(@PathVariable String campo, @PathVariable String value) {
        try{
            List<Proveedor> proveedorList = new ArrayList<>();
            switch (campo){
                case "nombre":
                    proveedorList = repository.findAllByNombreStartingWithIgnoreCaseAndActivoTrue(value);
                    break;
                case "nombre_fantasia":
                    proveedorList = repository.findAllByNombreFantasiaStartingWithIgnoreCaseAndActivoTrue(value);
                    break;
                case "cuit":
                    proveedorList = repository.findAllByCuitStartingWithAndActivoTrue(value);
                    break;
                default:
                    proveedorList = new ArrayList<>();
            }
            return new ResponseEntity<>(proveedorList,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }
    }

    private double getSaldoProveedor(Proveedor proveedor){
        List<Compra> compras = compraRepository.findAllByProveedorAndActivoTrueAndPagoFalse(proveedor);
        if (compras.isEmpty()){
            return 0;
        }
        return compras.stream()
                .mapToDouble(compra -> compra.getPrecioUnidad() * compra.getCantidad())
                .sum();
    }
}
