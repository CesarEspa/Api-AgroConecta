package com.apiagroconecta.api_agroconecta.dto.request;

import com.apiagroconecta.api_agroconecta.model.EstadoPedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class PedidoRequestDTO {

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    private EstadoPedido estado; // Opcional al crear (por defecto PENDIENTE)

    @NotEmpty(message = "El pedido debe contener al menos un detalle de producto")
    @Valid
    private List<DetalleItemRequestDTO> detalles;

    public PedidoRequestDTO() {
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public List<DetalleItemRequestDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleItemRequestDTO> detalles) {
        this.detalles = detalles;
    }

    public static class DetalleItemRequestDTO {
        
        @NotNull(message = "El ID del producto es obligatorio")
        private Long productoId;

        @NotNull(message = "La cantidad es obligatoria")
        private Integer cantidad;

        public DetalleItemRequestDTO() {
        }

        public Long getProductoId() {
            return productoId;
        }

        public void setProductoId(Long productoId) {
            this.productoId = productoId;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }
    }
}
