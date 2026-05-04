package com.mecanica.oficina_api.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@Entity
@With
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "servico_status")
public class StatusServicoJpaEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false)
  private String ordemServicoId;

  @Column(nullable = false)
  private String servicoId;

  @Column(nullable = false)
  private String status;

  private LocalDateTime dataInicio;
  private LocalDateTime dataFim;
}
