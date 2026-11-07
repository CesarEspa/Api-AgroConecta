package com.apiagroconecta.api_agroconecta.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO restringido para la actualización de un producto existente.
 * Solo expone los campos que el administrador puede modificar,
 * protegiendo campos sensibles como ID, nombre, imágenes y categoría.
 *
 * El campo 'detalles' acepta un objeto JSON con el formato:
 *   { "enDescuento": false, "porcentajeDescuento": null }
 * Su contenido se fusiona (merge) con el JSON existente en la BD,
 * sin eliminar sub-campos que no vengan en el payload.
 */
public class ProductoUpdateDTO {

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que cero")
    private BigDecimal precio;

    @NotNull(message = "La cantidad en stock es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidad;

    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    @NotNull(message = "El campo 'activo' es obligatorio")
    private Boolean activo;

    /**
     * Objeto JSON con los detalles de descuento/promoción del producto.
     * Formato esperado: { "enDescuento": false, "porcentajeDescuento": null }
     * Si se omite, el campo 'detalles' en la BD no se modifica.
     */
    private JsonNode detalles;

    public ProductoUpdateDTO() {}

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public JsonNode getDetalles() { return detalles; }
    public void setDetalles(JsonNode detalles) { this.detalles = detalles; }
}
