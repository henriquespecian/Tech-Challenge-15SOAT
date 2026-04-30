package com.mecanica.oficina_api.infrastructure.persistence.repository;


import com.mecanica.oficina_api.infrastructure.persistence.InsumosJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsumosSpringDataRepository extends JpaRepository<InsumosJpaEntity, String> {
  Optional<InsumosJpaEntity> findByNome(String nome);

  Optional<InsumosJpaEntity> findByIdAndAtivoTrue(String id);

  List<InsumosJpaEntity> findAllByAtivoTrue();
}
