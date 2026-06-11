package com.mecanica.oficina_api.application.ordemservico.usecase;

import static com.mecanica.oficina_api.application.ordemservico.OrdemServicoFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.ordemservico.MontadorItensOrcamento;
import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
import com.mecanica.oficina_api.application.ordemservico.input.GerarOrcamentoInput;
import com.mecanica.oficina_api.domain.ordemservico.OrcamentoStatus;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServicoStatus;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AtualizarOrcamentoUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private MontadorItensOrcamento montadorItensOrcamento;

    @InjectMocks
    private AtualizarOrcamentoUseCase useCase;

    @Test
    void deveAtualizarOrcamento_quandoOrcamentoAguardando() {
        OrdemServico os = osComOrcamento(OrdemServicoStatus.AGUARDANDO_APROVACAO, OrcamentoStatus.AGUARDANDO,
            List.of(itemServico("serv-1")));
        GerarOrcamentoInput input = new GerarOrcamentoInput(null, List.of(new GerarOrcamentoInput.ItemServicoInput("serv-2", 1)), "ajuste");
        when(ordemServicoGateway.encontrarOuLancar(OS_ID)).thenReturn(os);
        when(montadorItensOrcamento.montar(input)).thenReturn(List.of(itemServico("serv-2")));
        when(ordemServicoGateway.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        OrdemServico resultado = useCase.executar(OS_ID, input);

        assertThat(resultado.getOrcamento().getStatus()).isEqualTo(OrcamentoStatus.PENDENTE);
        assertThat(resultado.getOrcamento().getObservacoes()).isEqualTo("ajuste");
        assertThat(resultado.getOrcamento().getItens()).extracting("servicoId").containsExactly("serv-2");
    }

    @Test
    void deveLancarExcecao_quandoOrcamentoNaoEstaAguardando() {
        OrdemServico os = osComOrcamento(OrdemServicoStatus.AGUARDANDO_APROVACAO, OrcamentoStatus.ENVIADO,
            List.of(itemServico("serv-1")));
        GerarOrcamentoInput input = new GerarOrcamentoInput(null, List.of(new GerarOrcamentoInput.ItemServicoInput("serv-2", 1)), null);
        when(ordemServicoGateway.encontrarOuLancar(OS_ID)).thenReturn(os);
        when(montadorItensOrcamento.montar(input)).thenReturn(List.of(itemServico("serv-2")));

        assertThatThrownBy(() -> useCase.executar(OS_ID, input))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Orçamento deve estar AGUARDANDO");

        verify(ordemServicoGateway, never()).atualizar(any());
    }
}
