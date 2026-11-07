package com.apiagroconecta.api_agroconecta.controller;

import com.apiagroconecta.api_agroconecta.dto.request.PedidoRequestDTO;
import com.apiagroconecta.api_agroconecta.dto.response.PedidoResponseDTO;
import com.apiagroconecta.api_agroconecta.model.EstadoPedido;
import com.apiagroconecta.api_agroconecta.service.PedidoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    @Autowired
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // Obtener todos los pedidos
    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> findAll() {
        return ResponseEntity.ok(pedidoService.findAll());
    }

    // Obtener un pedido por su ID
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> findById(@PathVariable Long id) {
        PedidoResponseDTO pedido = pedidoService.findById(id);
        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pedido);
    }

    // Obtener todos los pedidos de un cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResponseDTO>> findByClienteId(@PathVariable Long clienteId) {
        try {
            return ResponseEntity.ok(pedidoService.findByClienteId(clienteId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Historial de compras del CLIENTE autenticado.
     * No necesita parámetros: la identidad viene del JWT (SecurityContext).
     * Solo accesible con rol CLIENTE.
     */
    @GetMapping("/mis-pedidos")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<PedidoResponseDTO>> misPedidos() {
        return ResponseEntity.ok(pedidoService.findMisPedidos());
    }

    // Crear un nuevo pedido — exclusivo para CLIENTE
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<?> crearPedido(@Valid @RequestBody PedidoRequestDTO dto) {
        try {
            PedidoResponseDTO nuevoPedido = pedidoService.save(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPedido);
        } catch (IllegalArgumentException e) {
            // Retorna el mensaje real del error, no un 400 vacío
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "status", 400,
                            "error", "Bad Request",
                            "message", e.getMessage()
                    ));
        }
    }

    // Actualizar los detalles (artículos) de un pedido
    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> updatePedido(
            @PathVariable Long id,
            @Valid @RequestBody PedidoRequestDTO dto) {
        try {
            PedidoResponseDTO pedidoActualizado = pedidoService.update(id, dto);
            if (pedidoActualizado == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(pedidoActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Actualizar parcialmente el estado del pedido y añadir historial
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoResponseDTO> updateEstado(
            @PathVariable Long id,
            @Valid @RequestBody EstadoUpdateRequest request) {
        PedidoResponseDTO pedidoActualizado = pedidoService.updateEstado(id, request.getEstado(), request.getNota());
        if (pedidoActualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pedidoActualizado);
    }

    // Cancelar lógicamente un pedido
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> cancelarPedido(@PathVariable Long id) {
//        boolean cancelado = pedidoService.delete(id);
//        if (!cancelado) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.noContent().build();
//    }

    // DTO de petición interna para actualizar estado
    public static class EstadoUpdateRequest {
        @NotNull(message = "El estado es obligatorio")
        private EstadoPedido estado;
        private String nota;

        public EstadoUpdateRequest() {
        }

        public EstadoPedido getEstado() {
            return estado;
        }

        public void setEstado(EstadoPedido estado) {
            this.estado = estado;
        }

        public String getNota() {
            return nota;
        }

        public void setNota(String nota) {
            this.nota = nota;
        }
    }
}
