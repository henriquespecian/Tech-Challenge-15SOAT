package com.mecanica.oficina_api.application.insumo.usecase;

import com.mecanica.oficina_api.application.insumo.gateway.InsumosGateway;
import com.mecanica.oficina_api.domain.insumo.Insumos;

public class AtivarInsumoUseCase {
    private final InsumosGateway insumosGateway;
    public AtivarInsumoUseCase(InsumosGateway insumosGateway) {
        this.insumosGateway = insumosGateway;
    }

    public Insumos executar(String id) {
        insumosGateway.buscar(id).orElseThrow(() -> new IllegalArgumentException("Insumo não encontrado"));
        return insumosGateway.ativar(id);
    }
}
