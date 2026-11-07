package com.apiagroconecta.api_agroconecta.dto.response;

import com.apiagroconecta.api_agroconecta.model.Producto;
import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductoResponseDTO {
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private String descripcion;
    private String imagen;
    private JsonNode detalles;
    private Integer cantidad;
    private LocalDateTime fechaDeIngreso;
    private Boolean activo;
    private Integer stockMinimo;
    private String descripcionLong;
    private Long categoriaId;
    private String categoriaNombre;

    public ProductoResponseDTO() {}

    public static ProductoResponseDTO desde(Producto producto) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.id = producto.getId();
        dto.nombre = producto.getNombre();
        dto.precio = producto.getPrecio();
        dto.descripcion = producto.getDescripcion();
        dto.imagen = producto.getImagen();
        dto.detalles = producto.getDetalles();
        dto.cantidad = producto.getCantidad();
        dto.fechaDeIngreso = producto.getFechaDeIngreso();
        dto.activo = producto.getActivo();
        dto.stockMinimo = producto.getStockMinimo();
        dto.descripcionLong = producto.getDescripcionLong();
        // Mapeo e inyeccion segura de la instancia de Categoria asignada
        if (producto.getCategoria() != null) {
            dto.categoriaId = producto.getCategoria().getId();
            dto.categoriaNombre = producto.getCategoria().getNombre();
        }
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    // 4. Getter y Setter actualizados con JsonNode
    public JsonNode getDetalles() { return detalles; }
    public void setDetalles(JsonNode detalles) { this.detalles = detalles; }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFechaDeIngreso() {
        return fechaDeIngreso;
    }

    public void setFechaDeIngreso(LocalDateTime fechaDeIngreso) {
        this.fechaDeIngreso = fechaDeIngreso;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public String getDescripcionLong() {
        return descripcionLong;
    }

    public void setDescripcionLong(String descripcionLong) {
        this.descripcionLong = descripcionLong;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }
}
