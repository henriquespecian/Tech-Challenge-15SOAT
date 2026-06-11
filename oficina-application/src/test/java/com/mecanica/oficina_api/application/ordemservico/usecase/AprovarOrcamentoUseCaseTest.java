package com.mecanica.oficina_api.application.ordemservico.usecase;

import static com.mecanica.oficina_api.application.ordemservico.OrdemServicoFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.insumo.gateway.InsumosGateway;
import com.mecanica.oficina_api.application.insumo.gateway.NotificarEstoqueBaixoGateway;
import com.mecanica.oficina_api.application.insumo.output.AlertaEstoqueBaixo;
import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
import com.mecanica.oficina_api.domain.insumo.Insumos;
import com.mecanica.oficina_api.domain.insumo.OrigemNotificacaoEstoque;
import com.mecanica.oficina_api.domain.ordemservico.OrcamentoStatus;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServicoStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AprovarOrcamentoUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private InsumosGateway insumosGateway;

    @Mock
    private NotificarEstoqueBaixoGateway notificadorEstoqueBaixo;

    @InjectMocks
    private AprovarOrcamentoUseCase useCase;

    @Test
    void deveAprovarOrcamentoEDarBaixaNosInsumos_semAlertaQuandoEstoqueSegueAcimaDoMinimo() {
        OrdemServico os = osComOrcamento(OrdemServicoStatus.AGUARDANDO_APROVACAO, OrcamentoStatus.ENVIADO,
            List.of(itemInsumo("ins-1", 2)));
        Insumos insumo = Insumos.reconstituir("ins-1", "Óleo", BigDecimal.valueOf(50), 10, 2, "LITRO");
        when(ordemServicoGateway.encontrarOuLancar(OS_ID)).thenReturn(os);
        when(insumosGateway.buscar("ins-1")).thenReturn(Optional.of(insumo));
        when(ordemServicoGateway.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        OrdemServico resultado = useCase.executar(OS_ID);

        assertThat(resultado.getOrcamento().getStatus()).isEqualTo(OrcamentoStatus.APROVADO);

        ArgumentCaptor<Insumos> captor = ArgumentCaptor.forClass(Insumos.class);
        verify(insumosGateway).alterar(eq("ins-1"), captor.capture());
        assertThat(captor.getValue().getEstoqueAtual()).isEqualTo(8);
        verify(notificadorEstoqueBaixo, never()).notificar(any());
    }

    @Test
    void deveNotificarEstoqueBaixo_quandoBaixaCruzaOMinimo() {
        OrdemServico os = osComOrcamento(OrdemServicoStatus.AGUARDANDO_APROVACAO, OrcamentoStatus.ENVIADO,
            List.of(itemInsumo("ins-1", 3)));
        Insumos insumo = Insumos.reconstituir("ins-1", "Óleo", BigDecimal.valueOf(50), 10, 8, "LITRO");
        when(ordemServicoGateway.encontrarOuLancar(OS_ID)).thenReturn(os);
        when(insumosGateway.buscar("ins-1")).thenReturn(Optional.of(insumo));
        when(ordemServicoGateway.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.executar(OS_ID);

        ArgumentCaptor<AlertaEstoqueBaixo> captor = ArgumentCaptor.forClass(AlertaEstoqueBaixo.class);
        verify(notificadorEstoqueBaixo).notificar(captor.capture());
        AlertaEstoqueBaixo alerta = captor.getValue();
        assertThat(alerta.insumoId()).isEqualTo("ins-1");
        assertThat(alerta.estoqueAnterior()).isEqualTo(10);
        assertThat(alerta.estoqueAtual()).isEqualTo(7);
        assertThat(alerta.origem()).isEqualTo(OrigemNotificacaoEstoque.BAIXA_ORDEM_SERVICO);
    }

    @Test
    void deveIgnorarItensDeServico_semConsultarInsumos() {
        OrdemServico os = osComOrcamento(OrdemServicoStatus.AGUARDANDO_APROVACAO, OrcamentoStatus.ENVIADO,
            List.of(itemServico("serv-1")));
        when(ordemServicoGateway.encontrarOuLancar(OS_ID)).thenReturn(os);
        when(ordemServicoGateway.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.executar(OS_ID);

        verify(insumosGateway, never()).buscar(anyString());
        verify(insumosGateway, never()).alterar(anyString(), any());
    }

    @Test
    void deveIgnorarInsumoNaoEncontrado_semDarBaixa() {
        OrdemServico os = osComOrcamento(OrdemServicoStatus.AGUARDANDO_APROVACAO, OrcamentoStatus.ENVIADO,
            List.of(itemInsumo("ins-removido", 2)));
        when(ordemServicoGateway.encontrarOuLancar(OS_ID)).thenReturn(os);
        when(insumosGateway.buscar("ins-removido")).thenReturn(Optional.empty());
        when(ordemServicoGateway.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.executar(OS_ID);

        verify(insumosGateway, never()).alterar(anyString(), any());
        verify(notificadorEstoqueBaixo, never()).notificar(any());
    }

    @Test
    void deveLancarExcecao_quandoOrcamentoNaoPodeSerAprovado() {
        OrdemServico os = osComOrcamento(OrdemServicoStatus.EM_DIAGNOSTICO, OrcamentoStatus.PENDENTE,
            List.of(itemInsumo("ins-1", 2)));
        when(ordemServicoGateway.encontrarOuLancar(OS_ID)).thenReturn(os);

        assertThatThrownBy(() -> useCase.executar(OS_ID))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Orçamento deve estar ENVIADO ou AGUARDANDO");

        verify(insumosGateway, never()).alterar(anyString(), any());
        verify(ordemServicoGateway, never()).atualizar(any());
    }
}
