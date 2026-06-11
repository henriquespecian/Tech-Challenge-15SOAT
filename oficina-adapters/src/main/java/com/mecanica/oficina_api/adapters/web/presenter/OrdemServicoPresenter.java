package com.mecanica.oficina_api.adapters.web.presenter;

import com.mecanica.oficina_api.adapters.web.dto.response.ItemOrcamentoResponse;
import com.mecanica.oficina_api.adapters.web.dto.response.OrcamentoResponse;
import com.mecanica.oficina_api.adapters.web.dto.response.OrdemServicoResponse;
import com.mecanica.oficina_api.adapters.web.dto.response.ServicoStatusResponse;
import com.mecanica.oficina_api.domain.ordemservico.ItemOrcamento;
import com.mecanica.oficina_api.domain.ordemservico.Orcamento;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;
import com.mecanica.oficina_api.domain.ordemservico.StatusServico;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrdemServicoPresenter {

    public OrdemServicoResponse apresentar(OrdemServico os) {
        OrcamentoResponse orcamento = os.getOrcamento() != null ? toOrcamentoResponse(os.getOrcamento()) : null;
        return new OrdemServicoResponse(os.getId(), os.getVeiculoId(), os.getClienteId(),
                os.getStatus().name(), os.getValorFinal(), os.getDataFinal(), orcamento);
    }

    public List<OrdemServicoResponse> apresentar(List<OrdemServico> oss) {
        return oss.stream().map(this::apresentar).toList();
    }

    public ServicoStatusResponse apresentarServico(StatusServico s) {
        return new ServicoStatusResponse(s.getId(), s.getStatus().name(), s.getOrdemServicoId(),
                s.getServicoId(), s.getDataInicio(), s.getDataFim());
    }

    public List<ServicoStatusResponse> apresentarServicos(List<StatusServico> servicos) {
        return servicos.stream().map(this::apresentarServico).toList();
    }

    private OrcamentoResponse toOrcamentoResponse(Orcamento orc) {
        List<ItemOrcamentoResponse> itens = orc.getItens().stream().map(this::toItemResponse).toList();
        return new OrcamentoResponse(orc.getStatus().name(), itens, orc.getValorTotal(),
                orc.getObservacoes(), orc.getRespondidoEm());
    }

    private ItemOrcamentoResponse toItemResponse(ItemOrcamento i) {
        return new ItemOrcamentoResponse(i.getInsumoId(), i.getServicoId(), i.getDescricao(),
                i.getQuantidade(), i.getPrecoUnitario(), i.getValorTotal());
    }
}
