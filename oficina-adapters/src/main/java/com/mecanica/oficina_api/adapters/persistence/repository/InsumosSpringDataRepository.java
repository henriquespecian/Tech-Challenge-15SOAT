package com.mecanica.oficina_api.adapters.persistence.repository;


import com.mecanica.oficina_api.adapters.persistence.InsumosJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsumosSpringDataRepository extends JpaRepository<InsumosJpaEntity, String> {
  Optional<InsumosJpaEntity> findByNome(String nome);

  Optional<InsumosJpaEntity> findByIdAndAtivoTrue(String id);

  List<InsumosJpaEntity> findAllByAtivoTrue();

  Boolean existsByAtivoTrueAndNome(String nome);

  Boolean existsByIdAndAtivoTrue(String id);
}
