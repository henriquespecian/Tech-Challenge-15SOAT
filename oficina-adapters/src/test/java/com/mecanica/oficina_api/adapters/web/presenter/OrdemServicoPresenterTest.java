package com.mecanica.oficina_api.adapters.web.presenter;

import com.mecanica.oficina_api.adapters.web.dto.response.OrdemServicoResponse;
import com.mecanica.oficina_api.adapters.web.dto.response.ServicoStatusResponse;
import com.mecanica.oficina_api.domain.ordemservico.ItemOrcamento;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServicoStatus;
import com.mecanica.oficina_api.domain.ordemservico.ServicoStatus;
import com.mecanica.oficina_api.domain.ordemservico.StatusServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrdemServicoPresenterTest {

    private final OrdemServicoPresenter presenter = new OrdemServicoPresenter();

    @Test
    void deveApresentarOrdemSemOrcamento_comOrcamentoNulo() {
        OrdemServico os = OrdemServico.reconstituir("os-1", "veiculo-1", "cliente-1",
                OrdemServicoStatus.RECEBIDA, null, null, null);

        OrdemServicoResponse response = presenter.apresentar(os);

        assertThat(response.getId()).isEqualTo("os-1");
        assertThat(response.getVeiculoId()).isEqualTo("veiculo-1");
        assertThat(response.getClienteId()).isEqualTo("cliente-1");
        assertThat(response.getStatus()).isEqualTo("RECEBIDA");
        assertThat(response.getValorFinal()).isNull();
        assertThat(response.getDataFinal()).isNull();
        assertThat(response.getOrcamento()).isNull();
    }

    @Test
    void deveApresentarOrdemComOrcamento_mapeandoItensValorTotalEStatus() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");
        os.iniciarDiagnostico();
        os.gerarOrcamento(List.of(
                new ItemOrcamento("insumo-1", null, "Óleo", 2, new BigDecimal("50.00")),
                new ItemOrcamento(null, "servico-1", "Troca de óleo", 1, new BigDecimal("30.00"))),
                "observação");

        OrdemServicoResponse response = presenter.apresentar(os);

        assertThat(response.getOrcamento()).isNotNull();
        assertThat(response.getOrcamento().getStatus()).isEqualTo("PENDENTE");
        assertThat(response.getOrcamento().getObservacoes()).isEqualTo("observação");
        assertThat(response.getOrcamento().getValorTotal()).isEqualByComparingTo("130.00");
        assertThat(response.getOrcamento().getItens()).hasSize(2);
        assertThat(response.getOrcamento().getItens().get(0).getInsumo_id()).isEqualTo("insumo-1");
        assertThat(response.getOrcamento().getItens().get(0).getDescricao()).isEqualTo("Óleo");
        assertThat(response.getOrcamento().getItens().get(0).getQuantidade()).isEqualTo(2);
        assertThat(response.getOrcamento().getItens().get(0).getValorTotal()).isEqualByComparingTo("100.00");
        assertThat(response.getOrcamento().getItens().get(1).getServico_id()).isEqualTo("servico-1");
    }

    @Test
    void deveApresentarListaDeOrdens() {
        OrdemServico os = OrdemServico.reconstituir("os-1", "veiculo-1", "cliente-1",
                OrdemServicoStatus.RECEBIDA, null, null, null);

        List<OrdemServicoResponse> responses = presenter.apresentar(List.of(os));

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo("os-1");
    }

    @Test
    void deveApresentarServico_mapeandoStatusServicoParaResponse() {
        LocalDateTime inicio = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 1, 1, 11, 0);
        StatusServico status = StatusServico.recriar("status-1", ServicoStatus.FINALIZADO,
                "os-1", "servico-1", inicio, fim);

        ServicoStatusResponse response = presenter.apresentarServico(status);

        assertThat(response.getId()).isEqualTo("status-1");
        assertThat(response.getStatus()).isEqualTo("FINALIZADO");
        assertThat(response.getOrdemServicoId()).isEqualTo("os-1");
        assertThat(response.getServicoId()).isEqualTo("servico-1");
        assertThat(response.getDataInicio()).isEqualTo(inicio);
        assertThat(response.getDataFim()).isEqualTo(fim);
    }

    @Test
    void deveApresentarListaDeServicos() {
        StatusServico status = StatusServico.recriar("status-1", ServicoStatus.AGUARDANDO,
                "os-1", "servico-1", null, null);

        List<ServicoStatusResponse> responses = presenter.apresentarServicos(List.of(status));

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getStatus()).isEqualTo("AGUARDANDO");
    }
}
