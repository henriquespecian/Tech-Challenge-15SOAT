package com.mecanica.oficina_api.application.insumo.gateway;

import java.util.List;
import java.util.Optional;

import com.mecanica.oficina_api.domain.insumo.Insumos;

public interface InsumosGateway {
    Optional<Insumos> buscar(String id);
    Boolean existePorNome(String nome);
    Boolean existePorId(String id);
    Insumos criar(Insumos insumo);
    List<Insumos> listar();
    Insumos alterar(String id, Insumos insumo);
    Insumos ativar(String id);
    void inativar(String id);
    Integer obterEstoqueAtual(String id);
}
