package com.gestion.controller;

import com.gestion.dto.CuentaDTO;
import com.gestion.dto.GastoDTO;
import com.gestion.model.*;
import com.gestion.repository.*;
import com.gestion.util.mappers.CuentaMapper;
import com.gestion.util.mappers.GastoMapper;
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
@RequestMapping("/cuenta")
public class CuentaController {

    private final CuentaRepository repository;
    private final ClienteRepository clienteRepository;
    private final ProveedorRepository proveedorRepository;
    private final CompraRepository compraRepository;
    private final PedidoRepository pedidoRepository;
    private final PagoRepository pagoRepository;

    @Autowired
    public CuentaController(CuentaRepository repository, ClienteRepository clienteRepository, ProveedorRepository proveedorRepository, CompraRepository compraRepository, PedidoRepository pedidoRepository, PagoRepository pagoRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
        this.proveedorRepository = proveedorRepository;
        this.compraRepository = compraRepository;
        this.pedidoRepository = pedidoRepository;
        this.pagoRepository = pagoRepository;
    }

    @RequestMapping("/{id}")
    public ResponseEntity<CuentaDTO> getCuenta(@PathVariable Long id) {

        Optional<Cuenta> optionalCuenta = repository.findById(id);
        CuentaDTO cuentaDTO = CuentaMapper.getCuentaDTO(optionalCuenta.get());
        Optional<Cliente> optionalCliente = clienteRepository.findById(optionalCuenta.get().getIdUsuario());
        if (optionalCliente.isPresent()){
            List<Pedido> pedidos = pedidoRepository.findAllByDniClienteOrderByFechaDesc(optionalCliente.get().getDni());
            //seteo gastos
            List<GastoDTO> gastos = GastoMapper.getGastoDTOList(pedidos);
            cuentaDTO.setGastos(gastos);
            //seteo pagos
            List<List<Pago>> listPagos = new ArrayList<>();
            for(Pedido pedido : pedidos){
                listPagos.add(pagoRepository.findAllByVenta(pedido.getVenta()));
            }
            List<Pago> pagos = listPagos.stream().flatMap(List :: stream)
                    .collect(Collectors.toList());
            cuentaDTO.setPagos(pagos);
            cuentaDTO.setDniCliente(optionalCliente.get().getDni());

        }else {
            Optional<Proveedor> optionalProveedor = proveedorRepository.findById(optionalCuenta.get().getIdUsuario());
            cuentaDTO.setDniCliente(optionalProveedor.get().getCuit());

        }
        return new ResponseEntity<>(cuentaDTO, HttpStatus.OK);
    }

    @RequestMapping("/cliente/dni/{dni}")
    public ResponseEntity<Cuenta> getCuentaByDniCliente(@PathVariable String dni) {

        Optional<Cliente> optionalCliente = clienteRepository.findClienteByDni(dni);
        if(optionalCliente.isPresent()){
            Optional<Cuenta> optionalCuenta = repository.findCuentaByIdUsuario(optionalCliente.get().getId());
            Cuenta cuenta = (optionalCuenta.isPresent()?optionalCuenta.get():new Cuenta());
            return new ResponseEntity<>(cuenta, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new Cuenta(), HttpStatus.OK);
        }
    }

    @RequestMapping("/usuario/{idUsuario}")
    public ResponseEntity<Cuenta> getCuentaByUsuario(@PathVariable Long idUsuario) {

        Optional<Cuenta> optionalCuenta = repository.findCuentaByIdUsuario(idUsuario);
        Cuenta cuenta = (optionalCuenta.isPresent()?optionalCuenta.get():new Cuenta());
        return new ResponseEntity<>(cuenta, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Cuenta> crearCuenta(@RequestBody Cuenta cuenta) {
        try {
            Cuenta nuevaCuenta = repository.save(new Cuenta(cuenta.getIdUsuario(),0));

            return new ResponseEntity<>(nuevaCuenta, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/all")
    public ResponseEntity<List<CuentaDTO>> getCuentas(){
        try {
            List<CuentaDTO> cuentas = repository.findAll().stream()
                    .map( cuenta -> {
                        CuentaDTO cuentaDTO = CuentaMapper.getCuentaDTO(cuenta);
                        Optional<Cliente> optionalCliente =clienteRepository.findById(cuenta.getIdUsuario());
                        if (optionalCliente.isPresent()){
                            cuentaDTO.setDniCliente(optionalCliente.get().getDni());
                        }else{
                            cuentaDTO.setDniCliente(proveedorRepository.findById(cuenta.getIdUsuario()).get().getCuit());
                        }
                        return cuentaDTO;
                    }).collect(Collectors.toList());

            if (cuentas.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(cuentas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cuenta> updateCuenta(@RequestBody Cuenta newCuenta, @PathVariable Long id){

        return repository.findById(id)
                .map(cuenta -> {
                    cuenta.setIdUsuario(newCuenta.getIdUsuario());
                    cuenta.setSaldo(newCuenta.getSaldo());

                    return new ResponseEntity<>(repository.save(cuenta), HttpStatus.CREATED);
                })
                .orElseGet(() -> new ResponseEntity<>(repository.save(newCuenta), HttpStatus.CREATED));
    }

    @PostMapping("/pedido")
    public ResponseEntity<Cuenta> restarCuenta(@RequestBody Cuenta newCuenta) {
        return repository.findById(newCuenta.getId())
                .map(cuenta -> {
                    cuenta.setSaldo(cuenta.getSaldo() - newCuenta.getSaldo());

                    return new ResponseEntity<>(repository.save(cuenta), HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(repository.save(newCuenta), HttpStatus.CREATED));
    }

}
