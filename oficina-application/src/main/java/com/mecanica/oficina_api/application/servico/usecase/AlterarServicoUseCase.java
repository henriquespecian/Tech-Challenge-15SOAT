package com.mecanica.oficina_api.application.servico.usecase;

import java.math.BigDecimal;
import java.time.Duration;

import com.mecanica.oficina_api.application.servico.gateway.ServicoGateway;
import com.mecanica.oficina_api.domain.servico.Servico;

public class AlterarServicoUseCase {
    private final ServicoGateway servicoGateway;

    public AlterarServicoUseCase(ServicoGateway servicoGateway) {
        this.servicoGateway = servicoGateway;
    }

    public Servico executar(String id, String nome, String descricao, BigDecimal preco, int tempoEstimadoHoras) {
        servicoGateway.buscarOuFalhar(id);
        return servicoGateway.alterar(id, Servico.reconstituir(id, nome, descricao, preco, Duration.ofHours(tempoEstimadoHoras), true));
    }
}
