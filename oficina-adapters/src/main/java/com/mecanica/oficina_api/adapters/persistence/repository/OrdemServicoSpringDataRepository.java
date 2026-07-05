package com.mecanica.oficina_api.adapters.persistence.repository;

import com.mecanica.oficina_api.adapters.persistence.OrdemServicoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrdemServicoSpringDataRepository extends JpaRepository<OrdemServicoJpaEntity, String> {

    List<OrdemServicoJpaEntity> findByVeiculoId(String veiculoId);

    List<OrdemServicoJpaEntity> findByClienteId(String clienteId);

    List<OrdemServicoJpaEntity> findByStatus(String status);

    @Query("SELECT o FROM OrdemServicoJpaEntity o " +
           "WHERE o.status NOT IN ('FINALIZADA', 'ENTREGUE') " +
           "ORDER BY CASE o.status " +
           "  WHEN 'EM_EXECUCAO' THEN 1 " +
           "  WHEN 'AGUARDANDO_APROVACAO' THEN 2 " +
           "  WHEN 'EM_DIAGNOSTICO' THEN 3 " +
           "  WHEN 'RECEBIDA' THEN 4 " +
           "  ELSE 5 END ASC, o.dataCriacao ASC")
    List<OrdemServicoJpaEntity> findAtivasOrdenadas();
}
