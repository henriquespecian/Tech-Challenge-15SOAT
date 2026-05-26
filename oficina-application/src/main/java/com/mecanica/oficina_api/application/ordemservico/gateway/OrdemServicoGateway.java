package com.mecanica.oficina_api.application.ordemservico.gateway;

import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;

public interface OrdemServicoGateway {
    OrdemServico cadastrar(String veiculoId, String clienteId);
}
