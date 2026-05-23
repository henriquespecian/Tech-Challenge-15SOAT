package com.mecanica.oficina_api.adapters.web.dto.response;

public record MinhaOrdemServicoResponse(
        String id,
        String status,
        String orcamentoStatus,
        VeiculoResumo veiculo
) {
    public record VeiculoResumo(String id, String placa, String marca, String modelo, int ano, String cor) {}
}
