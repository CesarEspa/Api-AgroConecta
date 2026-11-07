package com.apiagroconecta.api_agroconecta.service;

import com.apiagroconecta.api_agroconecta.dto.request.CategoriaRequestDTO;
import com.apiagroconecta.api_agroconecta.dto.response.CategoriaResponseDTO;
import com.apiagroconecta.api_agroconecta.exception.ConflictException;
import com.apiagroconecta.api_agroconecta.exception.ResourceNotFoundException;
import com.apiagroconecta.api_agroconecta.model.Categoria;
import com.apiagroconecta.api_agroconecta.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> findAll() {
        return categoriaRepository.findAll().stream()
                .map(CategoriaResponseDTO::desde)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoriaResponseDTO findById(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Categoria no encontrada"));
        return CategoriaResponseDTO.desde(categoria);
    }

    @Transactional
    public CategoriaResponseDTO save(CategoriaRequestDTO dto) {
        if (categoriaRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new ConflictException(
                    "Ya existe una categoria con el nombre: " + dto.getNombre());
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());

        Categoria guardada = categoriaRepository.save(categoria);
        return CategoriaResponseDTO.desde(guardada);
    }

    @Transactional
    public CategoriaResponseDTO update(Long id, CategoriaRequestDTO dto) {
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada para actualizar"));

        categoriaExistente.setNombre(dto.getNombre());
        Categoria actualizada = categoriaRepository.save(categoriaExistente);
        return CategoriaResponseDTO.desde(actualizada);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. Categoría no encontrada");
        }
        categoriaRepository.deleteById(id);
    }
}
