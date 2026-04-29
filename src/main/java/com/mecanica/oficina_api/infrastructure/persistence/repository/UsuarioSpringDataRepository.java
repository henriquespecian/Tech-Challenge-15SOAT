package com.mecanica.oficina_api.infrastructure.persistence.repository;

import com.mecanica.oficina_api.infrastructure.persistence.UsuarioJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioSpringDataRepository extends JpaRepository<UsuarioJpaEntity, String> {
    boolean existsByEmail(String email);
    Optional<UsuarioJpaEntity> findByEmail(String email);
    Optional<UsuarioJpaEntity> findByIdAndAtivoTrue(String id);
}
