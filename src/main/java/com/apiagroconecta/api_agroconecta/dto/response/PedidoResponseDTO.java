package com.apiagroconecta.api_agroconecta.dto.response;

import com.apiagroconecta.api_agroconecta.model.Pedido;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PedidoResponseDTO {

    private Long id;
    private Long clienteId;         // Evitamos el bucle infinito exponiendo solo el ID
    private String clienteNombre;   // Dato útil para el frontend
    private String estadoActual;    // Extraemos solo el nombre o descripción del EstadoPedido
    private BigDecimal total;
    private LocalDateTime creadoAt;

    // Listas convertidas a sus respectivos DTOs para no exponer entidades crudas
    private List<DetallePedidoResponseDTO> detalles;
    private List<HistorialEstadoResponseDTO> historial;

    // Constructor vacío requerido para que sea un DTO estándar
    public PedidoResponseDTO() {
    }

    // El metodo estático convertir adaptado a tu flujo
    public static PedidoResponseDTO convertir(Pedido pedido) {
        if (pedido == null) {
            return null;
        }

        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(pedido.getId());
        dto.setTotal(pedido.getTotal());
        dto.setCreadoAt(pedido.getCreadoAt());

        // 1. Mapeo seguro de la relación con Usuario (Cliente)
        if (pedido.getCliente() != null) {
            dto.setClienteId(pedido.getCliente().getId());
            dto.setClienteNombre(pedido.getCliente().getNombre()); // Usa el getter real de tu entidad
        }

        // 2. Mapeo seguro del Estado (Asumiendo que EstadoPedido tiene un método getNombre())
        if (pedido.getEstadoActual() != null) {
            dto.setEstadoActual(pedido.getEstadoActual().name());
        }

        // 3. Mapeo polimórfico con Streams para la lista de Detalles
        if (pedido.getDetalles() != null) {
            dto.setDetalles(pedido.getDetalles().stream()
                    .map(DetallePedidoResponseDTO::fromEntity) // Requiere que crees este DTO
                    .collect(Collectors.toList()));
        }

        // 4. Mapeo con Streams para la lista de Historial
        if (pedido.getHistorial() != null) {
            dto.setHistorial(pedido.getHistorial().stream()
                    .map(HistorialEstadoResponseDTO::convertir) // Requiere que crees este DTO
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    // Getters y Setters estándar
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public String getEstadoActual() { return estadoActual; }
    public void setEstadoActual(String estadoActual) { this.estadoActual = estadoActual; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public LocalDateTime getCreadoAt() { return creadoAt; }
    public void setCreadoAt(LocalDateTime creadoAt) { this.creadoAt = creadoAt; }
    public List<DetallePedidoResponseDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedidoResponseDTO> detalles) { this.detalles = detalles; }
    public List<HistorialEstadoResponseDTO> getHistorial() { return historial; }
    public void setHistorial(List<HistorialEstadoResponseDTO> historial) { this.historial = historial; }
}
