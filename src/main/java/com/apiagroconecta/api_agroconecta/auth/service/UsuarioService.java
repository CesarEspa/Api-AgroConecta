package com.apiagroconecta.api_agroconecta.auth.service;

import com.apiagroconecta.api_agroconecta.auth.dto.UpdateUsuarioDTO;
import com.apiagroconecta.api_agroconecta.auth.dto.UsuarioResponseDTO;
import com.apiagroconecta.api_agroconecta.auth.model.EstadoUsuario;
import com.apiagroconecta.api_agroconecta.auth.model.Usuario;
import com.apiagroconecta.api_agroconecta.auth.repository.UsuarioRepository;
import com.apiagroconecta.api_agroconecta.exception.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponseDTO::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return UsuarioResponseDTO.desde(usuario);
    }

    @Transactional
    public UsuarioResponseDTO actualizarUsuario(Long id, UpdateUsuarioDTO request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        usuario.setNombre(request.getNombre());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        // Validar teléfono: obligatorio si el usuario es CLIENTE
        if (usuario.getRol() == com.apiagroconecta.api_agroconecta.auth.model.Rol.CLIENTE) {
            if (request.getTelefono() == null || request.getTelefono().isBlank()) {
                throw new IllegalArgumentException("El teléfono es obligatorio para los clientes");
            }
        }
        usuario.setTelefono(request.getTelefono());
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return UsuarioResponseDTO.desde(usuarioActualizado);
    }

    @Transactional
    public void cambiarEstado(Long id, EstadoUsuario estado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        usuario.setEstado(estado);
        usuarioRepository.save(usuario);
    }
}
