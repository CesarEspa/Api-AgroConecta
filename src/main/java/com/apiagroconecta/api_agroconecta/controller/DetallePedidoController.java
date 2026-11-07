package com.apiagroconecta.api_agroconecta.controller;

import com.apiagroconecta.api_agroconecta.dto.response.DetallePedidoResponseDTO;
import com.apiagroconecta.api_agroconecta.service.DetallePedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de SOLO LECTURA para detalles de pedido.
 *
 * Los DetallePedido NO se crean ni eliminan manualmente:
 * se generan automáticamente al registrar un pedido (PedidoService.save).
 * Por lo tanto, este controlador solo expone endpoints GET de consulta.
 *
 * Acceso restringido a ADMIN para auditoría y soporte.
 */
@RestController
@RequestMapping("/api/v1/detalles")
public class DetallePedidoController {

    @Autowired
    private DetallePedidoService service;

    /**
     * Lista todos los detalles de pedido. Solo ADMIN.
     * Útil para auditoría y soporte operativo.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DetallePedidoResponseDTO>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    /**
     * Consulta un detalle de pedido por su ID. Solo ADMIN.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DetallePedidoResponseDTO> getById(@PathVariable Long id) {
        DetallePedidoResponseDTO dto = service.findById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    // ❌ POST /api/v1/detalles — ELIMINADO
    //    Los detalles se crean automáticamente en PedidoService.save()
    //    al procesar los ítems del PedidoRequestDTO. Crear detalles
    //    manualmente rompería la integridad del inventario (sin descuento de stock).

    // ❌ DELETE /api/v1/detalles/{id} — ELIMINADO
    //    Eliminar un detalle de forma aislada dejaría el pedido en estado
    //    inconsistente. Para anular un pedido, usar PATCH /api/v1/pedidos/{id}/estado.
}