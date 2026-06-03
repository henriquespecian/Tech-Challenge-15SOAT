package com.mecanica.oficina_api.application.servico.gateway;

import java.util.List;
import java.util.Optional;

import com.mecanica.oficina_api.domain.servico.Servico;

public interface ServicoGateway {
    Servico cadastrar(Servico request);
    Optional<Servico> buscar(String id);

    default Servico buscarOuFalhar(String id) {
        return buscar(id)
            .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));
    }

    List<Servico> listar();
    Servico alterar(String id, Servico request);
    Servico ativar(String id);
    void inativar(String id);
}
