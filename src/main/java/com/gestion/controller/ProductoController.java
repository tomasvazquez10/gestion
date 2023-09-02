package com.gestion.controller;

import com.gestion.model.Pedido;
import com.gestion.model.Producto;
import com.gestion.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoRepository repository;

    @Autowired
    public ProductoController(ProductoRepository repository) {
        this.repository = repository;
    }

    @RequestMapping("/{id}")
    public ResponseEntity<Producto> getProducto(@PathVariable Long id) {

        Optional<Producto> optionalProducto = repository.findById(id);
        if (optionalProducto.isPresent()){
            return new ResponseEntity<>(optionalProducto.get(), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new Producto(),HttpStatus.NOT_FOUND);
        }

    }
}
