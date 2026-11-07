package com.apiagroconecta.api_agroconecta.controller;

import com.apiagroconecta.api_agroconecta.dto.request.ProductoRequestDTO;
import com.apiagroconecta.api_agroconecta.dto.request.ProductoUpdateDTO;
import com.apiagroconecta.api_agroconecta.dto.response.ProductoResponseDTO;
import com.apiagroconecta.api_agroconecta.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * Catálogo público: retorna SOLO productos con activo=true.
     * Accesible por cualquier visitante (sin cuenta, CLIENTE y ADMIN).
     */
    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(productoService.findAll());
    }

    /**
     * Productos en PROMOCIÓN — completamente público, sin autenticación.
     * Pensado para la vitrina del home y el banner de ofertas del catálogo.
     */
    @GetMapping("/promociones")
    public ResponseEntity<List<ProductoResponseDTO>> obtenerEnPromocion() {
        return ResponseEntity.ok(productoService.findEnPromocion());
    }

    /**
     * Catálogo completo (activos + inactivos) SOLO para ADMIN.
     * Doble protección: SecurityConfig + @PreAuthorize.
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductoResponseDTO>> obtenerTodosAdmin() {
        return ResponseEntity.ok(productoService.findAllAdmin());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crear(@Valid @RequestBody ProductoRequestDTO dto) {
        return new ResponseEntity<>(productoService.save(dto), HttpStatus.CREATED);
    }

    /**
     * Actualización restringida: solo permite modificar precio, cantidad,
     * stockMinimo, activo y el objeto 'detalles' (JsonNode).
     * Campos sensibles (nombre, imágenes, categoría, etc.) quedan intactos.
     *
     * Formato esperado para 'detalles':
     *   { "enDescuento": false, "porcentajeDescuento": null }
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoUpdateDTO dto) {
        return ResponseEntity.ok(productoService.update(id, dto));
    }
}

