package com.mecanica.oficina_api.application.ordemservico.input;

import java.util.List;

public record GerarOrcamentoInput(List<ItemInsumoInput> insumos,
                                  List<ItemServicoInput> servicos,
                                  String observacoes) {

    public record ItemInsumoInput(String insumoId, int quantidade) {}

    public record ItemServicoInput(String servicoId, int quantidade) {}
}
