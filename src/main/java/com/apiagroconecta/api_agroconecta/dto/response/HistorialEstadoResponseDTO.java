package com.apiagroconecta.api_agroconecta.dto.response;

import com.apiagroconecta.api_agroconecta.model.EstadoPedido;
import com.apiagroconecta.api_agroconecta.model.HistorialEstadoPedido;
import java.time.LocalDateTime;

public class HistorialEstadoResponseDTO {

    private Integer id;
    private Integer pedidoId;
    private EstadoPedido estadoId;
    private LocalDateTime fecha;
    private String nota;

    public HistorialEstadoResponseDTO() {
    }

    public static HistorialEstadoResponseDTO convertir(HistorialEstadoPedido historial) {
        if (historial == null) {
            return null;
        }
        HistorialEstadoResponseDTO dto = new HistorialEstadoResponseDTO();
        dto.setId(historial.getId());

        // MODIFICADO: Navegamos de forma segura hacia el objeto Pedido para extraer su ID
        if (historial.getPedido() != null) {
            // Si el ID de tu entidad Pedido es Long, usamos .intValue() para mantener el tipo Integer del DTO
            dto.setPedidoId(historial.getPedido().getId().intValue());
        }

        dto.setEstadoId(historial.getEstadoId());
        dto.setFecha(historial.getFecha());
        dto.setNota(historial.getNota());
        return dto;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Integer pedidoId) {
        this.pedidoId = pedidoId;
    }

    public EstadoPedido getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(EstadoPedido estadoId) {
        this.estadoId = estadoId;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }
}
