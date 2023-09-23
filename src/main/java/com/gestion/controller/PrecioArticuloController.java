package com.gestion.controller;

import com.gestion.model.PrecioArticulo;
import com.gestion.repository.PrecioArticuloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/precio")
public class PrecioArticuloController {

    private final PrecioArticuloRepository repository;

    @Autowired
    public PrecioArticuloController(PrecioArticuloRepository repository) {
        this.repository = repository;
    }

    @RequestMapping("/{id}")
    public ResponseEntity<PrecioArticulo> getPrecioArticulo(@PathVariable Long id) {

        Optional<PrecioArticulo> optionalPrecioArticulo = repository.findById(id);
        if (optionalPrecioArticulo.isPresent()){
            return new ResponseEntity<>(optionalPrecioArticulo.get(), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new PrecioArticulo(),HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping()
    public ResponseEntity<PrecioArticulo> crearPrecioArticulo(@RequestBody PrecioArticulo precioArticulo) {
        try {
            PrecioArticulo nuevoPrecioArticulo = repository
                    .save(new PrecioArticulo(precioArticulo.getIdArticulo(),new Date(),precioArticulo.getPrecio()));

            return new ResponseEntity<>(nuevoPrecioArticulo, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/all")
    public ResponseEntity<List<PrecioArticulo>> getPrecioArticulos(){
        try {
            List<PrecioArticulo> precioArticulos = repository.findAll();

            if (precioArticulos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(precioArticulos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/articulo/{idArticulo}")
    public ResponseEntity<PrecioArticulo> getPrecioArticuloByIdArticuloAndDate(@PathVariable Long idArticulo){
        try {
            List<PrecioArticulo> precioArticulo = repository.getPrecioArticuloByIdArticuloOrderByFechaDesc(idArticulo);

            if (precioArticulo == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(precioArticulo.get(0), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/historico/{idArticulo}")
    public ResponseEntity<List<PrecioArticulo>> getPrecioArticulosByIdArticulo(@PathVariable Long idArticulo){
        try {
            List<PrecioArticulo> precioArticulos = repository.getPrecioArticulosByIdArticuloOrderByFechaDesc(idArticulo);

            if (precioArticulos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(precioArticulos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

