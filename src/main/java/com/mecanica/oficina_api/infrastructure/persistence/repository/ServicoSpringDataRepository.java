package com.mecanica.oficina_api.infrastructure.persistence.repository;

import com.mecanica.oficina_api.infrastructure.persistence.ServicoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServicoSpringDataRepository extends JpaRepository<ServicoJpaEntity, String> {
    Optional<ServicoJpaEntity> findByIdAndAtivoTrue(String id);
    List<ServicoJpaEntity> findAllByAtivoTrue();
}
