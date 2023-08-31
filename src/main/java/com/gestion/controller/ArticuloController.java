package com.gestion.controller;

import com.gestion.model.Articulo;
import com.gestion.repository.ArticuloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/articulo")
public class ArticuloController {

    private final ArticuloRepository repository;

    @Autowired
    public ArticuloController(ArticuloRepository repository) {
        this.repository = repository;
    }

    @RequestMapping("/{id}")
    public ResponseEntity<Articulo> getArticulo(@PathVariable Long id) {

        Optional<Articulo> optionalArticulo = repository.findById(id);
        if (optionalArticulo.isPresent()){
            return new ResponseEntity<>(optionalArticulo.get(), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new Articulo(),HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping()
    public ResponseEntity<Articulo> crearArticulo(@RequestBody Articulo articulo) {
        try {
            Articulo nuevoArticulo = repository
                    .save(new Articulo(articulo.getNombre(),articulo.getDescripcion(),articulo.getCuitProveedor()));

            return new ResponseEntity<>(nuevoArticulo, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/all")
    public ResponseEntity<List<Articulo>> getArticulos(){
        try {
            List<Articulo> articulos = repository.findAll().stream()
                    .filter(Articulo::isActivo)
                    .collect(Collectors.toList());;

            if (articulos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(articulos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("delete/{id}")
    public ResponseEntity deleteArticulo(@PathVariable Long id){

        return repository.findById(id)
                .map(articulo -> {
                    articulo.setActivo(false);
                    repository.save(articulo);
                    return new ResponseEntity(HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/edit")
    public ResponseEntity<Articulo> editArticulo(@RequestBody Articulo newArticulo) {
        return repository.findById(newArticulo.getId())
                .map(articulo -> {
                    articulo.setNombre(newArticulo.getNombre());
                    articulo.setDescripcion(newArticulo.getDescripcion());
                    articulo.setCuitProveedor(newArticulo.getCuitProveedor());
                    return new ResponseEntity<>(repository.save(articulo), HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(repository.save(newArticulo), HttpStatus.CREATED));
    }
}
