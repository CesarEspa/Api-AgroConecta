package com.apiagroconecta.api_agroconecta.auth.dto;

import com.apiagroconecta.api_agroconecta.auth.model.EstadoUsuario;
import com.apiagroconecta.api_agroconecta.auth.model.Rol;
import com.apiagroconecta.api_agroconecta.auth.model.Usuario;

import java.time.LocalDateTime;

public class UsuarioResponseDTO {
    private Long id;
    private String nombre;
    private String email;
    private Rol rol;
    private EstadoUsuario estado;
    private LocalDateTime fechaCreacion;
    private String telefono;

    public UsuarioResponseDTO() {
    }

    public static UsuarioResponseDTO desde(Usuario usuario) {

        UsuarioResponseDTO dto = new UsuarioResponseDTO();

        dto.id = usuario.getId();
        dto.nombre = usuario.getNombre();
        dto.email = usuario.getEmail();
        dto.rol = usuario.getRol();
        dto.estado = usuario.getEstado();
        dto.fechaCreacion = usuario.getFechaCreacion();
        dto.telefono = usuario.getTelefono();

        return dto;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public Rol getRol() {
        return rol;
    }

    public EstadoUsuario getEstado() {
        return estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public String getTelefono() {
        return telefono;
    }
}
