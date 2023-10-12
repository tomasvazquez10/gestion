package com.gestion.util.mappers;

import com.gestion.dto.CuentaDTO;
import com.gestion.model.Cuenta;

public class CuentaMapper {

    public static CuentaDTO getCuentaDTO(Cuenta cuenta){
        CuentaDTO cuentaDTO = new CuentaDTO();
        cuentaDTO.setId(cuenta.getId());
        cuentaDTO.setIdUsuario(cuenta.getIdUsuario());
        cuentaDTO.setSaldo(cuenta.getSaldo());
        return cuentaDTO;
    }
}
