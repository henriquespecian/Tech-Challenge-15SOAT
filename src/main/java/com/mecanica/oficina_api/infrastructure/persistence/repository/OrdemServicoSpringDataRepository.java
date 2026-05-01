package com.mecanica.oficina_api.infrastructure.persistence.repository;

import com.mecanica.oficina_api.infrastructure.persistence.OrdemServicoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdemServicoSpringDataRepository extends JpaRepository<OrdemServicoJpaEntity, String> {

    List<OrdemServicoJpaEntity> findByVeiculoId(String veiculoId);

    List<OrdemServicoJpaEntity> findByClienteId(String clienteId);
}
