package com.gestion.controller;

import com.gestion.dto.CompraDTO;
import com.gestion.dto.CuentaDTO;
import com.gestion.dto.GastoDTO;
import com.gestion.model.*;
import com.gestion.repository.*;
import com.gestion.util.mappers.CompraMapper;
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

    private final ClienteRepository clienteRepository;
    private final ProveedorRepository proveedorRepository;
    private final CompraRepository compraRepository;
    private final PedidoRepository pedidoRepository;
    private final PagoRepository pagoRepository;

    @Autowired
    public CuentaController(ClienteRepository clienteRepository, ProveedorRepository proveedorRepository, CompraRepository compraRepository, PedidoRepository pedidoRepository, PagoRepository pagoRepository) {
        this.clienteRepository = clienteRepository;
        this.proveedorRepository = proveedorRepository;
        this.compraRepository = compraRepository;
        this.pedidoRepository = pedidoRepository;
        this.pagoRepository = pagoRepository;
    }

    @RequestMapping("/{id}")
    public ResponseEntity<CuentaDTO> getCuenta(@PathVariable Long id) {


        CuentaDTO cuentaDTO = new CuentaDTO();
        cuentaDTO.setId(id);
        cuentaDTO.setIdUsuario(id);
        Optional<Cliente> optionalCliente = clienteRepository.findById(id);
        if (optionalCliente.isPresent()){
            List<Pedido> pedidos = pedidoRepository.findAllByClienteOrderByFechaDesc(optionalCliente.get());
            //seteo gastos
            List<GastoDTO> gastos = GastoMapper.getGastoDTOList(pedidos);
            cuentaDTO.setGastos(gastos);
            //seteo pagos
            List<List<Pago>> listPagos = new ArrayList<>();
            for(Pedido pedido : pedidos){
                listPagos.add(pagoRepository.findAllByPedido(pedido));
            }
            List<Pago> pagos = listPagos.stream().flatMap(List :: stream)
                    .collect(Collectors.toList());
            cuentaDTO.setPagos(pagos);
            cuentaDTO.setDniCliente(optionalCliente.get().getDni());
            cuentaDTO.setSaldo(optionalCliente.get().getSaldo());
        }else {
            Optional<Proveedor> optionalProveedor = proveedorRepository.findById(id);
            cuentaDTO.setDniCliente(optionalProveedor.get().getCuit());
            List<CompraDTO> compras = new ArrayList<>();
            for (Articulo articulo : optionalProveedor.get().getArticulos()){
                List<Compra> comprasArt = compraRepository.findAllByArticuloOrderByFechaDesc(articulo);
                for (Compra compra : comprasArt){
                    compras.add(CompraMapper.getCompraDTO(compra));
                }
            }
            cuentaDTO.setCompras(compras);
            cuentaDTO.setSaldo(optionalProveedor.get().getSaldo());
        }
        return new ResponseEntity<>(cuentaDTO, HttpStatus.OK);
    }

    /*
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
    */
    @RequestMapping("/all")
    public ResponseEntity<List<CuentaDTO>> getCuentas(){
        try {
           List<CuentaDTO> cuentas = new ArrayList<>();
           List<Cliente> clientes = clienteRepository.findAll();
           for (Cliente cliente : clientes){
               cuentas.add(new CuentaDTO(cliente.getId(), cliente.getId(), cliente.getSaldo(), cliente.getDni()));
           }
           List<Proveedor> proveedores = proveedorRepository.findAll();
           for (Proveedor proveedor : proveedores){
               cuentas.add(new CuentaDTO(proveedor.getId(), proveedor.getId(), proveedor.getSaldo(), proveedor.getCuit()));
           }
            if (cuentas.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(cuentas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
    }

    /*
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
    */
}
