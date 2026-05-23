package com.mecanica.oficina_api.adapters.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TempoMedioServicoResponse {
  private String servicoId;
  private String nome;
  private double tempoMedioEmMinutos;
}
