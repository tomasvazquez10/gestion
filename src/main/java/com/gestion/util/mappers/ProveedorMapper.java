package com.gestion.util.mappers;


import com.gestion.dto.ProveedorDTO;
import com.gestion.model.Proveedor;

public class ProveedorMapper {

    public static ProveedorDTO getProveedorDTO(Proveedor proveedor, double saldo){
        ProveedorDTO proveedorDTO = new ProveedorDTO();
        proveedorDTO.setId(proveedor.getId());
        proveedorDTO.setNombre(proveedor.getNombre());
        proveedorDTO.setNombreFantasia(proveedor.getNombreFantasia());
        proveedorDTO.setEmail(proveedor.getEmail());
        proveedorDTO.setCuit(proveedor.getCuit());
        proveedorDTO.setTelefono(proveedor.getTelefono());
        proveedorDTO.setDireccion(proveedor.getDireccion());
        proveedorDTO.setSaldo(saldo);
        return proveedorDTO;
    }
}
