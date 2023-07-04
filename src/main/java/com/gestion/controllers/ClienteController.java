package com.gestion.controllers;

import com.gestion.model.Cliente;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClienteController {

    @RequestMapping("/cliente")
    public Cliente getCliente() {
        return new Cliente("12345","nombre","apellido");
    }
}
