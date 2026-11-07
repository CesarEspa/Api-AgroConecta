package com.apiagroconecta.api_agroconecta.dto.response;

import com.apiagroconecta.api_agroconecta.model.Categoria;

public class CategoriaResponseDTO{
    private Long id;
    private String nombre;

    public CategoriaResponseDTO() {}

    public static CategoriaResponseDTO desde(Categoria categoria) {
        CategoriaResponseDTO dto = new CategoriaResponseDTO();
        dto.id = categoria.getId();
        dto.nombre = categoria.getNombre();
        return dto;
    }

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
}
