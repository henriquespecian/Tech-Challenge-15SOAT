package com.mecanica.oficina_api.adapters.persistence.repository;

import com.mecanica.oficina_api.adapters.persistence.StatusServicoJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusServicoSpringDataRepository extends JpaRepository<StatusServicoJpaEntity, String> {

  Optional<StatusServicoJpaEntity> findByIdAndStatus(String id, String status);

  List<StatusServicoJpaEntity> findByOrdemServicoId(String ordemServicoId);

  List<StatusServicoJpaEntity> findByServicoIdAndStatus(String servicoId, String status);

  Optional<StatusServicoJpaEntity> findFirstByOrdemServicoIdAndStatusEqualsIgnoreCase(String ordemServicoId, String status);

}
