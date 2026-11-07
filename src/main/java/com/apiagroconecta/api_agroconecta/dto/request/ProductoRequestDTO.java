package com.apiagroconecta.api_agroconecta.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public class ProductoRequestDTO {

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotNull(message = "El precio es obligatorio")
    private BigDecimal precio;

    private String descripcion;

    @NotEmpty(message = "El producto debe tener al menos una imagen")
    @Size(min = 1, max = 7, message = "El número de imágenes debe estar entre 1 y 7")
    @JsonAlias({"imagen", "imagenes"})
    private List<String> imagenes;

    private JsonNode detalles;

    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidad;

    @Min(value = 0, message = "El stock minimo no puede ser negativo")
    private Integer stockMinimo;

    private String descripcionLong;

    @NotNull(message = "La categoria es obligatoria")
    private Long categoriaId;

    public ProductoRequestDTO() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public List<String> getImagenes() { return imagenes; }
    public void setImagenes(List<String> imagenes) { this.imagenes = imagenes; }

    public JsonNode getDetalles() { return detalles; }
    public void setDetalles(JsonNode detalles) { this.detalles = detalles; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }

    public String getDescripcionLong() { return descripcionLong; }
    public void setDescripcionLong(String descripcionLong) { this.descripcionLong = descripcionLong; }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
}
