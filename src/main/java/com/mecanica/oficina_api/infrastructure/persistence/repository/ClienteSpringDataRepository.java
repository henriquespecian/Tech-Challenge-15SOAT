package com.mecanica.oficina_api.infrastructure.persistence.repository;

import com.mecanica.oficina_api.infrastructure.persistence.ClienteJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteSpringDataRepository extends JpaRepository<ClienteJpaEntity, String> {
    boolean existsByDocumento(String documento);

    Optional<ClienteJpaEntity> findByDocumento(String documento);

    Optional<ClienteJpaEntity> findByDocumentoAndAtivoTrue(String documento);

    List<ClienteJpaEntity> findAllByAtivoTrue();

    ClienteJpaEntity findByAtivo(boolean ativo);
}
