package com.gestion.controller;

import com.gestion.dto.ArticuloDTO;
import com.gestion.model.Articulo;
import com.gestion.model.PrecioArticulo;
import com.gestion.repository.ArticuloRepository;
import com.gestion.repository.PrecioArticuloRepository;
import com.gestion.util.ArticuloMapper;
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
@RequestMapping("/articulo")
public class ArticuloController {

    private final ArticuloRepository repository;
    private final PrecioArticuloRepository precioArticuloRepository;

    @Autowired
    public ArticuloController(ArticuloRepository repository, PrecioArticuloRepository precioArticuloRepository) {
        this.repository = repository;
        this.precioArticuloRepository = precioArticuloRepository;
    }

    @RequestMapping("/{id}")
    public ResponseEntity<ArticuloDTO> getArticulo(@PathVariable Long id) {

        Optional<Articulo> optionalArticulo = repository.findArticuloByNroArticulo(id);
        PrecioArticulo precioArticulo = getPrecioArticuloByNroArticulo(id);
        if (optionalArticulo.isPresent()){
            ArticuloDTO articuloDTO = ArticuloMapper.getArticuloDTO(optionalArticulo.get(),precioArticulo);
            return new ResponseEntity<>(articuloDTO, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new ArticuloDTO(),HttpStatus.NOT_FOUND);
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
    public ResponseEntity<List<ArticuloDTO>> getArticulos(){
        try {
            List<Articulo> articulos = repository.findAll().stream()
                    .filter(Articulo::isActivo)
                    .collect(Collectors.toList());;

            if (articulos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            List<ArticuloDTO> articuloDTOS = new ArrayList<>();
            for (Articulo articulo: articulos) {
                articuloDTOS.add(ArticuloMapper.getArticuloDTO(articulo,getPrecioArticuloByNroArticulo(articulo.getNroArticulo())));
            }
            return new ResponseEntity<>(articuloDTOS, HttpStatus.OK);
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
                    articulo.setStock(newArticulo.getStock());
                    return new ResponseEntity<>(repository.save(articulo), HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(repository.save(newArticulo), HttpStatus.CREATED));
    }


    private PrecioArticulo getPrecioArticuloByNroArticulo(Long nroArticulo){
        List<PrecioArticulo> precioArticuloList = precioArticuloRepository.getPrecioArticuloByIdArticuloOrderByFechaDesc(nroArticulo);
        if (precioArticuloList.isEmpty()){
            return new PrecioArticulo();
        }else{
            return precioArticuloList.get(0);
        }
    }
}
