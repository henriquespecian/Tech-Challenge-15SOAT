package com.mecanica.oficina_api.application.ordemservico;

import java.math.BigDecimal;
import java.util.List;

import com.mecanica.oficina_api.domain.ordemservico.ItemOrcamento;
import com.mecanica.oficina_api.domain.ordemservico.Orcamento;
import com.mecanica.oficina_api.domain.ordemservico.OrcamentoStatus;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServicoStatus;

/** Fixtures de domínio compartilhados pelos testes de use case de ordem de serviço. */
public final class OrdemServicoFixture {

    public static final String OS_ID = "os-1";
    public static final String VEICULO_ID = "veic-1";
    public static final String CLIENTE_ID = "cli-1";

    private OrdemServicoFixture() {}

    public static ItemOrcamento itemInsumo(String insumoId, int quantidade) {
        return new ItemOrcamento(insumoId, null, "Insumo " + insumoId, quantidade, BigDecimal.TEN);
    }

    public static ItemOrcamento itemServico(String servicoId) {
        return new ItemOrcamento(null, servicoId, "Serviço " + servicoId, 1, BigDecimal.valueOf(100));
    }

    public static OrdemServico osSemOrcamento(OrdemServicoStatus status) {
        return OrdemServico.reconstituir(OS_ID, VEICULO_ID, CLIENTE_ID, status, null, null, null);
    }

    public static OrdemServico osComOrcamento(OrdemServicoStatus status, OrcamentoStatus orcamentoStatus, List<ItemOrcamento> itens) {
        Orcamento orcamento = Orcamento.reconstituir(itens, orcamentoStatus, null, null);
        return OrdemServico.reconstituir(OS_ID, VEICULO_ID, CLIENTE_ID, status, orcamento, null, null);
    }
}
