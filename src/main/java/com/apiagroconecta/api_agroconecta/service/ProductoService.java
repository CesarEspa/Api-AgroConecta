package com.apiagroconecta.api_agroconecta.service;

import com.apiagroconecta.api_agroconecta.dto.request.ProductoRequestDTO;
import com.apiagroconecta.api_agroconecta.dto.request.ProductoUpdateDTO;
import com.apiagroconecta.api_agroconecta.dto.response.ProductoResponseDTO;
import com.apiagroconecta.api_agroconecta.model.Categoria;
import com.apiagroconecta.api_agroconecta.model.Producto;
import com.apiagroconecta.api_agroconecta.repository.CategoriaRepository;
import com.apiagroconecta.api_agroconecta.repository.ProductoRepository;
import com.apiagroconecta.api_agroconecta.service.storage.UploadImageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UploadImageService uploadImageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    public ProductoService(ProductoRepository productoRepository,
                           CategoriaRepository categoriaRepository,
                           UploadImageService uploadImageService) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.uploadImageService = uploadImageService;
    }

    /**
     * Catálogo público: retorna ÚNICAMENTE productos activos.
     * Regla de negocio: CLIENTE y anónimos nunca ven productos inactivos/borradores.
     */
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> findAll() {
        return productoRepository.findByActivoTrue().stream()
                .map(ProductoResponseDTO::desde)
                .collect(Collectors.toList());
    }

    /**
     * Productos en PROMOCIÓN — público para visitantes sin cuenta.
     *
     * IMPORTANTE: No se usa native query para este filtro porque PostgreSQL
     * retorna el campo jsonb como String crudo, lo que impide que Hibernate
     * deserialice el JsonNode correctamente y el campo detalles llega null al DTO.
     *
     * Solución: cargamos todos los activos via JPA (Hibernate maneja la
     * deserialización de jsonb → JsonNode) y filtramos en memoria verificando
     * el campo detalles.enPromocion o detalles.enDescuento del JSON.
     *
     * El front envía y espera el formato:
     *   "detalles": { "enDescuento": false, "porcentajeDescuento": null }
     */
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> findEnPromocion() {
        return productoRepository.findByActivoTrue().stream()
                .filter(p -> {
                    if (p.getDetalles() == null) return false;
                    // Soporte para ambas claves: "enPromocion" (backend) y "enDescuento" (frontend)
                    boolean enPromocion = p.getDetalles().path("enPromocion").asBoolean(false);
                    boolean enDescuento = p.getDetalles().path("enDescuento").asBoolean(false);
                    return enPromocion || enDescuento;
                })
                .map(ProductoResponseDTO::desde)
                .collect(Collectors.toList());
    }

    /**
     * Catálogo completo (activos + inactivos) — exclusivo para ADMIN.
     * Verificamos el rol desde el SecurityContext para mayor seguridad.
     */
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> findAllAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean esAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!esAdmin) {
            // Salvaguarda: si por algún bypass alguien sin rol ADMIN llama este método,
            // solo ve productos activos.
            return findAll();
        }

        return productoRepository.findAll().stream()
                .map(ProductoResponseDTO::desde)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoResponseDTO findById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        return ProductoResponseDTO.desde(producto);
    }

    @Transactional
    public ProductoResponseDTO save(ProductoRequestDTO dto) {
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("La categoría asignada no existe"));

        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());
        producto.setDescripcion(dto.getDescripcion());
        producto.setCantidad(dto.getCantidad() != null ? dto.getCantidad() : 0);
        producto.setStockMinimo(dto.getStockMinimo() != null ? dto.getStockMinimo() : 1);
        producto.setDescripcionLong(dto.getDescripcionLong());

        if (dto.getDetalles() != null) {
            producto.setDetalles(dto.getDetalles());
        } else {
            producto.setDetalles(objectMapper.createObjectNode());
        }
        producto.setCategoria(categoria);
        producto.setActivo(true);

        // Procesar y subir múltiples imágenes a GCP
        if (dto.getImagenes() != null && !dto.getImagenes().isEmpty()) {
            List<String> urls = new ArrayList<>();
            for (String img : dto.getImagenes()) {
                if (img == null || img.isBlank()) {
                    continue;
                }
                if (img.startsWith("http://") || img.startsWith("https://")) {
                    urls.add(img);
                } else {
                    try {
                        String url = uploadImageService.uploadBase64(img);
                        urls.add(url);
                    } catch (IOException e) {
                        throw new RuntimeException("Error al subir la imagen a GCP: " + e.getMessage(), e);
                    }
                }
            }
            producto.setImagenesFromList(urls);
        } else {
            producto.setImagen("");
        }

        Producto guardado = productoRepository.save(producto);
        return ProductoResponseDTO.desde(guardado);
    }

    /**
     * Actualización restringida: solo modifica precio, cantidad, stockMinimo, activo
     * y el objeto 'detalles' (JsonNode) del producto.
     * Campos sensibles (nombre, imágenes, categoría, descripción, etc.) no se alteran.
     *
     * El campo 'detalles' se fusiona (merge) con el JSON existente en la BD:
     * si el payload incluye { "enDescuento": true, "porcentajeDescuento": 10 },
     * se actualizan esos campos sin borrar otros sub-campos presentes en la BD.
     * Si 'detalles' viene null en el DTO, el campo en la BD no se toca.
     */
    @Transactional
    public ProductoResponseDTO update(Long id, ProductoUpdateDTO dto) {
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado para actualizar"));

        // Solo modificamos los campos permitidos por el DTO restringido
        productoExistente.setPrecio(dto.getPrecio());
        productoExistente.setCantidad(dto.getCantidad());
        productoExistente.setStockMinimo(dto.getStockMinimo());
        productoExistente.setActivo(dto.getActivo());

        // Merge del JsonNode 'detalles': toma los valores actuales de la BD
        // y sobreescribe/agrega los campos que llegan en el payload del DTO.
        // Formato esperado: { "enDescuento": false, "porcentajeDescuento": null }
        if (dto.getDetalles() != null && dto.getDetalles().isObject()) {
            ObjectNode detallesNode;
            if (productoExistente.getDetalles() != null && productoExistente.getDetalles().isObject()) {
                detallesNode = (ObjectNode) productoExistente.getDetalles();
            } else {
                detallesNode = objectMapper.createObjectNode();
            }
            dto.getDetalles().fields().forEachRemaining(entry ->
                    detallesNode.set(entry.getKey(), entry.getValue())
            );
            productoExistente.setDetalles(detallesNode);
        }

        Producto actualizado = productoRepository.save(productoExistente);
        return ProductoResponseDTO.desde(actualizado);
    }
}
