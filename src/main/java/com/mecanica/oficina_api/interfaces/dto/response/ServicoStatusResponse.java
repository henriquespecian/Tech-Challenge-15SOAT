package com.mecanica.oficina_api.interfaces.dto.response;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ServicoStatusResponse {
  private String id;
  private String status;
  private String ordemServicoId;
  private String servicoId;
  private LocalDateTime dataInicio;
  private LocalDateTime dataFim;

  public ServicoStatusResponse() {}

  public ServicoStatusResponse(String id, String status, String ordemServicoId, String servicoId, LocalDateTime dataInicio, LocalDateTime dataFim) {
    this.id = id;
    this.status = status;
    this.ordemServicoId = ordemServicoId;
    this.servicoId = servicoId;
    this.dataInicio = dataInicio;
    this.dataFim = dataFim;
  }
}
