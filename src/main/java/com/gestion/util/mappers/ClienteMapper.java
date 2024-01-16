package com.gestion.util.mappers;

import com.gestion.dto.ClienteDTO;
import com.gestion.model.Cliente;

public class ClienteMapper {

    public static ClienteDTO getClienteDTO(Cliente cliente){
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setId(cliente.getIdCliente());
        clienteDTO.setNombre(cliente.getNombre());
        clienteDTO.setNombreFantasia(cliente.getNombreFantasia());
        clienteDTO.setEmail(cliente.getEmail());
        clienteDTO.setDni(cliente.getDni());
        clienteDTO.setTelefono(cliente.getTelefono());
        clienteDTO.setDireccion(cliente.getDireccion());
        clienteDTO.setNroReparto(cliente.getReparto().getNroReparto());
        clienteDTO.setSaldo(cliente.getSaldo());
        return clienteDTO;
    }
}
