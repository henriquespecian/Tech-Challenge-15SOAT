package com.mecanica.oficina_api.infrastructure.persistence.repository;

import com.mecanica.oficina_api.infrastructure.persistence.VeiculoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VeiculoSpringDataRepository extends JpaRepository<VeiculoJpaEntity, String> {
    boolean existsByPlaca(String placa);
    List<VeiculoJpaEntity> findByCliente_Id(String clienteId);
}
