package com.apiagroconecta.api_agroconecta.repository;

import com.apiagroconecta.api_agroconecta.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

}