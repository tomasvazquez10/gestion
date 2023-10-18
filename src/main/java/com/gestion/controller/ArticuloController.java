package com.gestion.controller;

import com.gestion.dto.ArticuloDTO;
import com.gestion.model.Articulo;
import com.gestion.model.PrecioArticulo;
import com.gestion.repository.ArticuloRepository;
import com.gestion.repository.PrecioArticuloRepository;
import com.gestion.util.GeneratePDFReport;
import com.gestion.util.mappers.ArticuloMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
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

        Optional<Articulo> optionalArticulo = repository.findById(id);
        PrecioArticulo precioArticulo = getPrecioArticuloByNroArticulo(id);
        if (optionalArticulo.isPresent()){
            ArticuloDTO articuloDTO = ArticuloMapper.getArticuloDTO(optionalArticulo.get(),precioArticulo);
            return new ResponseEntity<>(articuloDTO, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new ArticuloDTO(),HttpStatus.NOT_FOUND);
        }

    }

    @RequestMapping("/numero/{nroArticulo}")
    public ResponseEntity<Articulo> getArticuloByNroArticulo(@PathVariable Long nroArticulo) {

        Optional<Articulo> optionalArticulo = repository.findArticuloByNroArticulo(nroArticulo);
        if (optionalArticulo.isPresent()){
            return new ResponseEntity<>(optionalArticulo.get(), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
                articuloDTOS.add(ArticuloMapper.getArticuloDTO(articulo,getPrecioArticuloByNroArticulo(articulo.getId())));
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
                    articulo.setNroArticulo(newArticulo.getNroArticulo());
                    articulo.setActivo(true);
                    return new ResponseEntity<>(repository.save(articulo), HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(repository.save(newArticulo), HttpStatus.CREATED));
    }

    @RequestMapping("/buscar/{campo}/{value}")
    public ResponseEntity<List<Articulo>> findArticulosBy(@PathVariable String campo, @PathVariable String value) {
        try{
            List<Articulo> articulos = new ArrayList<>();
            switch (campo){
                case "nombre":
                    articulos = repository.findAllByNombreStartingWithIgnoreCaseAndActivoTrue(value);
                    break;
                case "cuit":
                    articulos = repository.findAllByCuitProveedorStartingWithAndActivoTrue(value);
                    break;
                default:
                    articulos = new ArrayList<>();
            }
            return new ResponseEntity<>(articulos,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }
    }
    private PrecioArticulo getPrecioArticuloByNroArticulo(Long nroArticulo){
        List<PrecioArticulo> precioArticuloList = precioArticuloRepository.getPrecioArticuloByIdArticuloOrderByFechaDesc(nroArticulo);
        if (precioArticuloList.isEmpty()){
            return new PrecioArticulo();
        }else{
            return precioArticuloList.get(0);
        }
    }

    @RequestMapping("/pdf")
    public ResponseEntity<InputStreamResource> getListadoPDF(){
        try {
            List<Articulo> articulos = repository.findAll().stream()
                    .filter(Articulo::isActivo)
                    .collect(Collectors.toList());;

            if (articulos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            List<ArticuloDTO> articuloDTOS = new ArrayList<>();
            for (Articulo articulo: articulos) {
                articuloDTOS.add(ArticuloMapper.getArticuloDTO(articulo,getPrecioArticuloByNroArticulo(articulo.getId())));
            }

            ByteArrayInputStream bis = GeneratePDFReport.getArticuloPDF(articulos);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=listado-articulos.pdf");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(bis));

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
