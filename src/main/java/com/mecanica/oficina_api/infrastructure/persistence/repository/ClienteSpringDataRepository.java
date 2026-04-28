package com.mecanica.oficina_api.infrastructure.persistence.repository;

import com.mecanica.oficina_api.infrastructure.persistence.ClienteJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteSpringDataRepository extends JpaRepository<ClienteJpaEntity, String> {
    boolean existsByCpf(String cpf);

    Optional<ClienteJpaEntity> findByCpf(String cpf);

    Optional<ClienteJpaEntity> findByCpfAndAtivoTrue(String cpf);

    ClienteJpaEntity findByAtivo(boolean ativo);
}