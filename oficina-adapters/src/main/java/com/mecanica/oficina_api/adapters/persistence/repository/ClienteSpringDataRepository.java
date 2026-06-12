package com.mecanica.oficina_api.adapters.persistence.repository;

import com.mecanica.oficina_api.adapters.persistence.ClienteJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteSpringDataRepository extends JpaRepository<ClienteJpaEntity, String> {
    boolean existsByDocumento(String documento);

    Optional<ClienteJpaEntity> findByDocumento(String documento);

    Optional<ClienteJpaEntity> findByDocumentoAndAtivoTrue(String documento);

    List<ClienteJpaEntity> findAllByAtivoTrue();

    Optional<ClienteJpaEntity> findByIdAndAtivoTrue(String id);
}
