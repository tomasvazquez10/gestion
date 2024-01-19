package com.gestion.controller;

import com.gestion.dto.CompraDTO;
import com.gestion.model.Articulo;
import com.gestion.model.Compra;
import com.gestion.model.Proveedor;
import com.gestion.repository.ArticuloRepository;
import com.gestion.repository.CompraRepository;
import com.gestion.repository.ProveedorRepository;
import com.gestion.util.mappers.ArticuloMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/compra")
public class CompraController {

    private final CompraRepository repository;
    private final ArticuloRepository articuloRepository;
    private final ProveedorRepository proveedorRepository;

    public CompraController(CompraRepository repository, ArticuloRepository articuloRepository, ProveedorRepository proveedorRepository) {
        this.repository = repository;
        this.articuloRepository = articuloRepository;
        this.proveedorRepository = proveedorRepository;
    }

    @RequestMapping("/{id}")
    public ResponseEntity<Compra> getCompra(@PathVariable Long id) {

        Optional<Compra> optionalCompra = repository.findById(id);
        if (optionalCompra.isPresent()){
            return new ResponseEntity<>(optionalCompra.get(), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new Compra(),HttpStatus.NOT_FOUND);
        }

    }

    @RequestMapping("/all")
    public ResponseEntity<List<Compra>> getCompras(){
        try {
            List<Compra> compras = repository.findAll().stream()
                    .filter(Compra::isActivo)
                    .collect(Collectors.toList());;

            if (compras.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(compras, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/articulo/{idArticulo}")
    public ResponseEntity<List<Compra>> getComprasByArticuloId(@PathVariable Long idArticulo){
        try {
            Optional<Articulo> optionalArticulo = articuloRepository.findById(idArticulo);
            if (optionalArticulo.isPresent()){
                List<Compra> compras = repository.findAllByArticuloOrderByFechaDesc(optionalArticulo.get()).stream()
                        .filter(Compra::isActivo)
                        .collect(Collectors.toList());;

                if (compras.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }
                return new ResponseEntity<>(compras, HttpStatus.OK);
            }
            return new ResponseEntity<>(new ArrayList<>(),HttpStatus.NO_CONTENT);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/proveedor/{idProveedor}")
    public ResponseEntity<List<Compra>> getComprasByIdProveedor(@PathVariable Long idProveedor){
        try {
            Optional<Proveedor> optionalProveedor = proveedorRepository.findById(idProveedor);
            if (optionalProveedor.isPresent()){
                List<Compra> compras = repository.findAllByProveedorAndActivoTrueAndPagoFalse(optionalProveedor.get());

                if (compras.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }
                return new ResponseEntity<>(compras, HttpStatus.OK);
            }
            return new ResponseEntity<>(new ArrayList<>(),HttpStatus.NO_CONTENT);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("delete/{id}")
    public ResponseEntity deleteCompra(@PathVariable Long id){

        return repository.findById(id)
                .map(compra -> {
                    compra.setActivo(false);
                    repository.save(compra);
                    //chequeo restar la cantidad de stock al articulo
                    Articulo articulo = compra.getArticulo();
                    if (articulo.getStock() < compra.getCantidad()){
                        return new ResponseEntity(HttpStatus.NO_CONTENT);
                    }
                    articulo.setStock(articulo.getStock() - compra.getCantidad());
                    articuloRepository.save(articulo);
                    return new ResponseEntity(HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
    }

    @RequestMapping("pagar/{id}")
    public ResponseEntity pagarCompra(@PathVariable Long id){

        return repository.findById(id)
                .map(compra -> {
                    compra.setPago(true);
                    repository.save(compra);

                    //chequeo restar saldo al proveedor
                    Proveedor proveedor = compra.getProveedor();
                    if (proveedor == null){
                        return new ResponseEntity(HttpStatus.NO_CONTENT);
                    }
                    //proveedor.setSaldo(proveedor.getSaldo() - compra.getCantidad()* compra.getPrecioUnidad());
                    //proveedorRepository.save(proveedor);
                    return new ResponseEntity(compra,HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/edit")
    public ResponseEntity<Compra> editCompra(@RequestBody Compra newCompra) {
        return repository.findById(newCompra.getIdCompra())
                .map(compra -> {
                    compra.setArticulo(newCompra.getArticulo());
                    compra.setCantidad(newCompra.getCantidad());
                    compra.setFecha(newCompra.getFecha());
                    compra.setPrecioUnidad(newCompra.getPrecioUnidad());
                    compra.setActivo(true);
                    return new ResponseEntity<>(repository.save(compra), HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(repository.save(newCompra), HttpStatus.CREATED));
    }

    @PostMapping()
    public ResponseEntity<Compra> crearCompra(@RequestBody CompraDTO compra) {
        try {
            Articulo articulo = ArticuloMapper.getArticulo(compra.getArticulo());
            Proveedor proveedor = proveedorRepository.findProveedorByCuit(compra.getCuitProveedor()).get();
            Compra nuevaCompra = repository
                    .save(new Compra(articulo, proveedor, compra.getFecha(),compra.getPrecioUnidad(),compra.getCantidad(),false,true));

            //sumo la cantidad al stock de articulo
            articulo.setStock(articulo.getStock() + compra.getCantidad());
            articulo.setActivo(true);
            articuloRepository.save(articulo);
            nuevaCompra.setArticulo(articulo);

            //sumo total al saldo de proveedor
            //proveedor.setSaldo(proveedor.getSaldo() + compra.getCantidad()* compra.getPrecioUnidad());
            proveedorRepository.save(proveedor);

            return new ResponseEntity<>(nuevaCompra, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
