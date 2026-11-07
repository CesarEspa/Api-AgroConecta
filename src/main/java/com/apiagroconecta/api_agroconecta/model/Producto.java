package com.apiagroconecta.api_agroconecta.model;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    @Column(columnDefinition = "TEXT")
    private String imagen;
    @JdbcTypeCode(SqlTypes.JSON)//Como debe serializar y deserializar el objeto Hibernate
    @Column(columnDefinition = "jsonb")
    private JsonNode detalles;
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidad;
    @CreationTimestamp //Hibernate se encarga de poner la fecha actual del sistema justo antes de hacer el INSERT
    @Column(name = "fecha_ingreso", updatable = false)
    private LocalDateTime fechaDeIngreso;
    private Boolean activo = true;
    @Min(value = 0, message = "El stock minimo no puede ser negativo")
    @Column(name = "stock_minimo")
    private Integer stockMinimo = 1;
    @Column(name = "descripcion_long", columnDefinition = "TEXT")
    private String descripcionLong;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", referencedColumnName = "id")
    @NotNull(message = "La categoria es obligatoria")
    private Categoria categoria;

    public Producto() {}

//    public Producto(Long id, String nombre, BigDecimal precio, String descripcion, String imagen, String detalles, Integer cantidad, LocalDateTime fechaDeIngreso, Boolean activo, Integer stockMinimo, String descripcionLong, Categoria categoria) {
//        this.id = id;
//        this.nombre = nombre;
//        this.precio = precio;
//        this.descripcion = descripcion;
//        this.imagen = imagen;
//        this.detalles = detalles;
//        this.cantidad = cantidad;
//        this.fechaDeIngreso = fechaDeIngreso;
//        this.activo = activo;
//        this.stockMinimo = stockMinimo;
//        this.descripcionLong = descripcionLong;
//        this.categoria = categoria;
//    }

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

    public JsonNode getDetalles() {
        return detalles;
    }

    public void setDetalles(JsonNode detalles) {
        this.detalles = detalles;
    }

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

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public java.util.List<String> getImagenesAsList() {
        if (this.imagen == null || this.imagen.isBlank()) {
            return java.util.Collections.emptyList();
        }
        return java.util.Arrays.stream(this.imagen.split("\\|"))
                .filter(s -> !s.isBlank())
                .collect(java.util.stream.Collectors.toList());
    }

    public void setImagenesFromList(java.util.List<String> list) {
        if (list == null || list.isEmpty()) {
            this.imagen = "";
        } else {
            this.imagen = String.join("|", list);
        }
    }
}
