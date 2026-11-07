package com.apiagroconecta.api_agroconecta.model;


import com.apiagroconecta.api_agroconecta.auth.model.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario cliente; // Cambiado de Usuario a Cliente


    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estadoActual;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(updatable = false)
    private LocalDateTime creadoAt = LocalDateTime.now();

    // CascadeType.ALL porque si se borra el pedido, los detalles pierden sentido
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles = new ArrayList<>();

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialEstadoPedido> historial = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }

    public EstadoPedido getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(EstadoPedido estadoActual) {
        this.estadoActual = estadoActual;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public LocalDateTime getCreadoAt() {
        return creadoAt;
    }

    public void setCreadoAt(LocalDateTime creadoAt) {
        this.creadoAt = creadoAt;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
    }

    public List<HistorialEstadoPedido> getHistorial() {
        return historial;
    }

    public void setHistorial(List<HistorialEstadoPedido> historial) {
        this.historial = historial;
    }

    //metodo Helper
    public void addHistorial(HistorialEstadoPedido nuevoHistorial) {
        if (this.historial == null) {
            this.historial = new ArrayList<>();
        }
        this.historial.add(nuevoHistorial);
        nuevoHistorial.setPedido(this); // Esto asegura que guarde el ID del pedido
    }
}
