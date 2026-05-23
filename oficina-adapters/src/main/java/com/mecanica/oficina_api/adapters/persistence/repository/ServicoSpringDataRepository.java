package com.mecanica.oficina_api.adapters.persistence.repository;

import com.mecanica.oficina_api.adapters.persistence.ServicoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServicoSpringDataRepository extends JpaRepository<ServicoJpaEntity, String> {
    Optional<ServicoJpaEntity> buscar(String id);
    List<ServicoJpaEntity> findAllByAtivoTrue();
}
