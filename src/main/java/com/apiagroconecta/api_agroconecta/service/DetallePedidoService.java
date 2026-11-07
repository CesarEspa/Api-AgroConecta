package com.apiagroconecta.api_agroconecta.service;
import org.springframework.transaction.annotation.Transactional;
import com.apiagroconecta.api_agroconecta.dto.request.DetallePedidoRequestDTO;
import com.apiagroconecta.api_agroconecta.dto.response.DetallePedidoResponseDTO;
import com.apiagroconecta.api_agroconecta.model.*;
import com.apiagroconecta.api_agroconecta.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetallePedidoService {
    @Autowired
    private DetallePedidoRepository repository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<DetallePedidoResponseDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(DetallePedidoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public DetallePedidoResponseDTO findById(Long id) {
        return repository.findById(id)
                .map(DetallePedidoResponseDTO::fromEntity)
                .orElse(null);
    }

    @Transactional
    public DetallePedidoResponseDTO save(DetallePedidoRequestDTO dto) {

        Pedido pedido = pedidoRepository.findById(dto.getPedidoId()).orElse(null);
        Producto producto = productoRepository.findById(dto.getProductoId()).orElse(null);

        if (pedido == null || producto == null) {
            return null;
        }

        DetallePedido d = new DetallePedido();

        d.setPedido(pedido);
        d.setProducto(producto);
        d.setCantidad(dto.getCantidad());

        // 🔥 el precio se toma del producto
        d.setPrecioUni(producto.getPrecio());

        repository.save(d);

        return DetallePedidoResponseDTO.fromEntity(d);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}

