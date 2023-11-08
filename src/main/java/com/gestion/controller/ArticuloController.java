package com.gestion.controller;

import com.gestion.dto.ArticuloDTO;
import com.gestion.model.Articulo;
import com.gestion.model.PrecioArticulo;
import com.gestion.model.Proveedor;
import com.gestion.repository.ArticuloRepository;
import com.gestion.repository.PrecioArticuloRepository;
import com.gestion.repository.ProveedorRepository;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/articulo")
public class ArticuloController {

    private final ArticuloRepository repository;
    private final PrecioArticuloRepository precioArticuloRepository;
    private final ProveedorRepository proveedorRepository;

    @Autowired
    public ArticuloController(ArticuloRepository repository, PrecioArticuloRepository precioArticuloRepository, ProveedorRepository proveedorRepository) {
        this.repository = repository;
        this.precioArticuloRepository = precioArticuloRepository;
        this.proveedorRepository = proveedorRepository;
    }

    @RequestMapping("/{id}")
    public ResponseEntity<ArticuloDTO> getArticulo(@PathVariable Long id) {

        Optional<Articulo> optionalArticulo = repository.findById(id);
        if (optionalArticulo.isPresent()){
            ArticuloDTO articuloDTO = ArticuloMapper.getArticuloDTO(optionalArticulo.get());
            return new ResponseEntity<>(articuloDTO, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new ArticuloDTO(),HttpStatus.NOT_FOUND);
        }

    }

    @RequestMapping("/numero/{nroArticulo}")
    public ResponseEntity<ArticuloDTO> getArticuloByNroArticulo(@PathVariable Long nroArticulo) {

        Optional<Articulo> optionalArticulo = repository.findArticuloByNroArticulo(nroArticulo);
        if (optionalArticulo.isPresent()){
            return new ResponseEntity<>(ArticuloMapper.getArticuloDTO(optionalArticulo.get()), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

    }

    @PostMapping()
    public ResponseEntity<ArticuloDTO> crearArticulo(@RequestBody ArticuloDTO articuloDTO) {

        try {
            Optional<Proveedor> optionalProveedor = proveedorRepository.findProveedorByCuit(articuloDTO.getCuitProveedor());
            optionalProveedor.ifPresent(articuloDTO::setProveedor);
            Articulo nuevoArticulo = repository
                    .save(new Articulo(articuloDTO.getNombre(),
                            articuloDTO.getDescripcion(),
                            articuloDTO.getNroArticulo(),
                            articuloDTO.getProveedor(),
                            articuloDTO.getPrecio(),
                            articuloDTO.getStock()));
            precioArticuloRepository.save(new PrecioArticulo(nuevoArticulo, new Date(), articuloDTO.getPrecio()));
            return new ResponseEntity<>(ArticuloMapper.getArticuloDTO(nuevoArticulo), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/all")
    public ResponseEntity<List<ArticuloDTO>> getArticulos(){
        try {
            List<Articulo> articulos = repository.findAll().stream()
                    .filter(Articulo::isActivo)
                    .collect(Collectors.toList());

            if (articulos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(getArticulosDTO(articulos), HttpStatus.OK);
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
    public ResponseEntity<ArticuloDTO> editArticulo(@RequestBody ArticuloDTO newArticulo) {
        Optional<Proveedor> optionalProveedor = proveedorRepository.findProveedorByCuit(newArticulo.getCuitProveedor());
        return repository.findById(newArticulo.getId())
                .map(articulo -> {
                    articulo.setNombre(newArticulo.getNombre());
                    articulo.setDescripcion(newArticulo.getDescripcion());
                    articulo.setProveedor(optionalProveedor.get());
                    articulo.setStock(newArticulo.getStock());
                    articulo.setNroArticulo(newArticulo.getNroArticulo());
                    articulo.setActivo(true);
                    return new ResponseEntity<>(ArticuloMapper.getArticuloDTO(repository.save(articulo)), HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(ArticuloMapper.getArticuloDTO(repository.save(ArticuloMapper.getArticulo(newArticulo))), HttpStatus.CREATED));
    }

    @RequestMapping("/buscar/{campo}/{value}")
    public ResponseEntity<List<ArticuloDTO>> findArticulosBy(@PathVariable String campo, @PathVariable String value) {
        try{
            List<Articulo> articulos = new ArrayList<>();
            switch (campo){
                case "nombre":
                    articulos = repository.findAllByNombreStartingWithIgnoreCaseAndActivoTrue(value);
                    break;
                case "cuit":
                    Optional<Proveedor> optionalProveedor = proveedorRepository.findProveedorByCuit(value);
                    if (optionalProveedor.isPresent()){
                        articulos = repository.findAllByProveedorAndActivoTrue(optionalProveedor.get());
                    }else {
                        articulos = new ArrayList<>();
                    }

                    break;
                default:
                    articulos = new ArrayList<>();
            }
            return new ResponseEntity<>(getArticulosDTO(articulos),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }
    }

    private PrecioArticulo getPrecioArticuloByNroArticulo(Long nroArticulo){
        Optional<Articulo> optionalArticulo = repository.findArticuloByNroArticulo(nroArticulo);
        List<PrecioArticulo> precioArticuloList = new ArrayList<>();
        if (optionalArticulo.isPresent()){
            precioArticuloList = precioArticuloRepository.getPrecioArticuloByArticuloOrderByFechaDesc(optionalArticulo.get());
        }
        if (precioArticuloList.isEmpty()){
            return new PrecioArticulo();
        }else{
            return precioArticuloList.get(0);
        }
    }

    @PostMapping("/pdf")
    public ResponseEntity<InputStreamResource> getListadoPDF(@RequestBody List<Articulo> articulos){
        ByteArrayInputStream bis = GeneratePDFReport.getArticuloPDF(articulos);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=listado-articulos.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    private List<ArticuloDTO> getArticulosDTO(List<Articulo> articulos){
        return articulos.stream()
                .map(ArticuloMapper::getArticuloDTO)
                .collect(Collectors.toList());
    }
}
