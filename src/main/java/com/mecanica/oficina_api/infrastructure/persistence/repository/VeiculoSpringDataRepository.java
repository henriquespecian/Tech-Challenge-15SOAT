package com.mecanica.oficina_api.infrastructure.persistence.repository;

import com.mecanica.oficina_api.infrastructure.persistence.VeiculoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VeiculoSpringDataRepository extends JpaRepository<VeiculoJpaEntity, String> {
    boolean existsByPlacaAndAtivoTrue(String placa);
    List<VeiculoJpaEntity> findByCliente_Id(String clienteId);
    List<VeiculoJpaEntity> findByCliente_IdAndAtivoTrue(String clienteId);
    boolean existsByIdAndAtivoTrue(String id);
    Optional<VeiculoJpaEntity> findByIdAndAtivoTrue(String id);
}
