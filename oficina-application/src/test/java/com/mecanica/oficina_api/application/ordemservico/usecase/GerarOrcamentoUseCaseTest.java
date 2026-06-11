package com.mecanica.oficina_api.application.ordemservico.usecase;

import static com.mecanica.oficina_api.application.ordemservico.OrdemServicoFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.ordemservico.MontadorItensOrcamento;
import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
import com.mecanica.oficina_api.application.ordemservico.input.GerarOrcamentoInput;
import com.mecanica.oficina_api.domain.ordemservico.ItemOrcamento;
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
class GerarOrcamentoUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private MontadorItensOrcamento montadorItensOrcamento;

    @InjectMocks
    private GerarOrcamentoUseCase useCase;

    @Test
    void deveGerarOrcamentoComSucesso() {
        OrdemServico os = osSemOrcamento(OrdemServicoStatus.EM_DIAGNOSTICO);
        GerarOrcamentoInput input = new GerarOrcamentoInput(null, List.of(new GerarOrcamentoInput.ItemServicoInput("serv-1", 1)), "obs");
        List<ItemOrcamento> itens = List.of(itemServico("serv-1"));
        when(ordemServicoGateway.encontrarOuLancar(OS_ID)).thenReturn(os);
        when(montadorItensOrcamento.montar(input)).thenReturn(itens);
        when(ordemServicoGateway.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        OrdemServico resultado = useCase.executar(OS_ID, input);

        assertThat(resultado.getOrcamento()).isNotNull();
        assertThat(resultado.getOrcamento().getStatus()).isEqualTo(OrcamentoStatus.PENDENTE);
        assertThat(resultado.getOrcamento().getItens()).hasSize(1);
        assertThat(resultado.getOrcamento().getObservacoes()).isEqualTo("obs");
        verify(ordemServicoGateway).atualizar(os);
    }

    @Test
    void deveLancarExcecao_quandoOsFinalizada() {
        OrdemServico os = osSemOrcamento(OrdemServicoStatus.FINALIZADA);
        GerarOrcamentoInput input = new GerarOrcamentoInput(null, List.of(new GerarOrcamentoInput.ItemServicoInput("serv-1", 1)), null);
        when(ordemServicoGateway.encontrarOuLancar(OS_ID)).thenReturn(os);
        when(montadorItensOrcamento.montar(input)).thenReturn(List.of(itemServico("serv-1")));

        assertThatThrownBy(() -> useCase.executar(OS_ID, input))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Não é possível gerar orçamento");

        verify(ordemServicoGateway, never()).atualizar(any());
    }
}
