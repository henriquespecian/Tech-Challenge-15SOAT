package com.mecanica.oficina_api.application.servico;

import com.mecanica.oficina_api.application.servico.gateway.ServicoGateway;
import com.mecanica.oficina_api.application.servico.gateway.StatusServicoGateway;
import com.mecanica.oficina_api.domain.servico.Servico;
import com.mecanica.oficina_api.domain.servico.TempoMedioServico;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

public class ServicoService {

    private final ServicoGateway servicoGateway;
    private final StatusServicoGateway statusServicoGateway;

    public ServicoService(ServicoGateway servicoGateway, StatusServicoGateway statusServicoGateway) {
        this.servicoGateway = servicoGateway;
        this.statusServicoGateway = statusServicoGateway;
    }

    public Servico cadastrar(String nome, String descricao, BigDecimal preco, int tempoEstimadoHoras) {
        Servico servico = Servico.criar(nome, descricao, preco, Duration.ofHours(tempoEstimadoHoras));
        return servicoGateway.cadastrar(servico);
    }

    public Servico buscar(String id) {
        return servicoGateway.buscar(id).orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));
    }

    public TempoMedioServico buscarTempoMedio(String id) {
        Servico servico = servicoGateway.buscar(id)
            .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));
        double media = statusServicoGateway.calcularTempoMedioMinutos(id);
        return new TempoMedioServico(servico.getId(), servico.getNome(), media);
    }

    public List<Servico> listar() {
        return servicoGateway.listar();
    }

    public Servico alterar(String id, String nome, String descricao, BigDecimal preco, int tempoEstimadoHoras) {
        servicoGateway.buscar(id).orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));
        return servicoGateway.alterar(id, Servico.reconstituir(id, nome, descricao, preco, Duration.ofHours(tempoEstimadoHoras), true));
    }

    public Servico ativar(String id) {
        servicoGateway.buscar(id).orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));
        return servicoGateway.ativar(id);
    }

    public void inativar(String id) {
        servicoGateway.buscar(id).orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));
        servicoGateway.inativar(id);
    }

}
