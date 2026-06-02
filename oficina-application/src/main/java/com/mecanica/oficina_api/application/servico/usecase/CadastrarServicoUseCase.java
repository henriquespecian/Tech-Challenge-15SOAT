package com.mecanica.oficina_api.application.servico.usecase;

import java.math.BigDecimal;
import java.time.Duration;

import com.mecanica.oficina_api.application.servico.gateway.ServicoGateway;
import com.mecanica.oficina_api.domain.servico.Servico;

public class CadastrarServicoUseCase {
    
    private final ServicoGateway servicoGateway;

    public CadastrarServicoUseCase(ServicoGateway servicoGateway) {
        this.servicoGateway = servicoGateway;
    }

    public Servico executar(String nome, String descricao, BigDecimal preco, int tempoEstimadoHoras) {
        Servico servico = Servico.criar(nome, descricao, preco, Duration.ofHours(tempoEstimadoHoras));
        return servicoGateway.cadastrar(servico);
    }
}
