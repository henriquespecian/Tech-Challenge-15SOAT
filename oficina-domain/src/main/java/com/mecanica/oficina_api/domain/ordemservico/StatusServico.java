package com.mecanica.oficina_api.domain.ordemservico;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusServico {
  private String id;
  private ServicoStatus status;
  private String ordemServicoId;
  private String servicoId;
  private LocalDateTime dataInicio;
  private LocalDateTime dataFim;


  protected StatusServico(String ordemServicoId, String servicoId){
    this.status = ServicoStatus.AGUARDANDO;
    this.ordemServicoId = Objects.requireNonNull(ordemServicoId, "O ID da Ordem de Serviço é obrigatório");
    this.servicoId = Objects.requireNonNull(servicoId, "O ID do Serviço é obrigatório");
    this.dataInicio = null;
    this.dataFim = null;
  }

  public static StatusServico criar(String ordemServicoId, String servicoId) {
    return new StatusServico(ordemServicoId, servicoId);
  }

  public static StatusServico recriar(String id, ServicoStatus status, String ordemServicoId, String servicoId, LocalDateTime dataInicio, LocalDateTime dataFim) {
    StatusServico statusServico = new StatusServico(ordemServicoId, servicoId);
    statusServico.id = id;
    statusServico.status = status;
    statusServico.dataInicio = dataInicio;
    statusServico.dataFim = dataFim;

    return statusServico;
  }

  public void iniciarServico(){
    if(this.status != ServicoStatus.AGUARDANDO){
      throw new IllegalArgumentException("Status do serviço deve ser: AGUARDANDO");
    }

    this.status = ServicoStatus.INICIADO;
    this.dataInicio = LocalDateTime.now();
  }

  public void finalizarServico(){
    if(this.status != ServicoStatus.INICIADO){
      throw new IllegalArgumentException("Status do serviço deve ser: INICIADO");
    }

    this.status = ServicoStatus.FINALIZADO;
    this.dataFim = LocalDateTime.now();
  }
}
