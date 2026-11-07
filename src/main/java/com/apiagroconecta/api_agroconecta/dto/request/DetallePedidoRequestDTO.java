package com.apiagroconecta.api_agroconecta.dto.request;

public class DetallePedidoRequestDTO {
    private Long pedidoId;
    private Long productoId;
    private Integer cantidad;

    public Long getPedidoId() {
        return pedidoId;
    }

    public Long getProductoId() {
        return productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
