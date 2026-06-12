package com.mecanica.oficina_api.adapters.persistence.repository;

import com.mecanica.oficina_api.adapters.persistence.VeiculoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VeiculoSpringDataRepository extends JpaRepository<VeiculoJpaEntity, String> {
    boolean existsByPlacaAndAtivoTrue(String placa);
    List<VeiculoJpaEntity> findByCliente_Id(String clienteId);
    List<VeiculoJpaEntity> findByCliente_IdAndAtivoTrue(String clienteId);
    boolean existsByIdAndAtivoTrue(String id);
    Optional<VeiculoJpaEntity> findByIdAndAtivoTrue(String id);
    Optional<VeiculoJpaEntity> findByPlacaIgnoreCaseAndAtivoTrue(String placa);
}
