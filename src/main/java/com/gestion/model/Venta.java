package com.gestion.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "VENTA")
public class Venta {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @OneToOne(mappedBy = "venta")
    private Pedido pedido;

    @OneToMany(mappedBy = "venta")
    private Set<Pago> pagos;

    public Venta(){}

    public Venta(Pedido pedido, Set<Pago> pagos) {
        this.pedido = pedido;
        this.pagos = pagos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Set<Pago> getPagos() {
        return pagos;
    }

    public void setPagos(Set<Pago> pagos) {
        this.pagos = pagos;
    }
}
