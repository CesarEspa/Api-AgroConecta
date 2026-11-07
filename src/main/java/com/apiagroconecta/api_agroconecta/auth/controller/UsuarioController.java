package com.apiagroconecta.api_agroconecta.auth.controller;

import com.apiagroconecta.api_agroconecta.auth.dto.UpdateUsuarioDTO;
import com.apiagroconecta.api_agroconecta.auth.dto.UsuarioResponseDTO;
import com.apiagroconecta.api_agroconecta.auth.model.EstadoUsuario;
import com.apiagroconecta.api_agroconecta.auth.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerUsuarioPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerUsuarioPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody UpdateUsuarioDTO request) {
        return ResponseEntity.ok(
                usuarioService.actualizarUsuario(id, request)
        );
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestParam EstadoUsuario estado) {
        usuarioService.cambiarEstado(id, estado);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Estado actualizado correctamente"
        ));
    }
}
