package com.gestion.controller;

import com.gestion.model.Articulo;
import com.gestion.model.PrecioArticulo;
import com.gestion.repository.ArticuloRepository;
import com.gestion.repository.PrecioArticuloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/precio")
public class PrecioArticuloController {

    private final PrecioArticuloRepository repository;
    private final ArticuloRepository articuloRepository;

    @Autowired
    public PrecioArticuloController(PrecioArticuloRepository repository, ArticuloRepository articuloRepository) {
        this.repository = repository;
        this.articuloRepository = articuloRepository;
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
                    .save(new PrecioArticulo(precioArticulo.getArticulo(),new Date(),precioArticulo.getPrecio()));
            Articulo nuevo = precioArticulo.getArticulo();
            nuevo.setPrecio(precioArticulo.getPrecio());
            articuloRepository.save(nuevo);
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
            Optional<Articulo> optionalArticulo = articuloRepository.findById(idArticulo);
            List<PrecioArticulo> precioArticulo = new ArrayList<>();
            if (optionalArticulo.isPresent()){
                precioArticulo = repository.getPrecioArticuloByArticuloOrderByFechaDesc(optionalArticulo.get());
            }


            if (precioArticulo.isEmpty()) {
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
            Optional<Articulo> optionalArticulo = articuloRepository.findById(idArticulo);
            List<PrecioArticulo> precioArticulos = new ArrayList<>();
            if (optionalArticulo.isPresent()){
                precioArticulos = repository.getPrecioArticuloByArticuloOrderByFechaDesc(optionalArticulo.get());
            }

            if (precioArticulos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(precioArticulos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

