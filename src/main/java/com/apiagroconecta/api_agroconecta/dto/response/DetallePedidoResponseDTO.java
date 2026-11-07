package com.apiagroconecta.api_agroconecta.dto.response;

import com.apiagroconecta.api_agroconecta.model.DetallePedido;

import java.math.BigDecimal;

public class DetallePedidoResponseDTO {
    private Long id;
    private Long pedidoId;

    private Long productoId;
    private String productoNombre;

    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    public static DetallePedidoResponseDTO fromEntity(DetallePedido d) {

        DetallePedidoResponseDTO dto = new DetallePedidoResponseDTO();

        dto.id = d.getId();
        dto.pedidoId = d.getPedido().getId();

        dto.productoId = d.getProducto().getId();
        dto.productoNombre = d.getProducto().getNombre();

        dto.cantidad = d.getCantidad();
        dto.precioUnitario = d.getPrecioUni();

        dto.subtotal = d.getPrecioUni()
                .multiply(BigDecimal.valueOf(d.getCantidad()));

        return dto;
    }

    // getters y setters

    public Long getId() { return id; }
    public Long getPedidoId() { return pedidoId; }
    public Long getProductoId() { return productoId; }
    public String getProductoNombre() { return productoNombre; }
    public Integer getCantidad() { return cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public BigDecimal getSubtotal() { return subtotal; }

    public void setId(Long id) { this.id = id; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}

