package com.mecanica.oficina_api.application.insumo.usecase;

import java.util.List;

import com.mecanica.oficina_api.application.insumo.gateway.InsumosGateway;
import com.mecanica.oficina_api.domain.insumo.Insumos;

public class ListarInsumosUseCase {
    private final InsumosGateway insumosGateway;
    
    public ListarInsumosUseCase(InsumosGateway insumosGateway) {
        this.insumosGateway = insumosGateway;
    }

    public List<Insumos> executar() {    
        return insumosGateway.listar();
    }
}
