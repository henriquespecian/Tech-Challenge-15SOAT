package com.mecanica.oficina_api.application.ordemservico.usecase;

import static com.mecanica.oficina_api.application.ordemservico.OrdemServicoFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.ordemservico.gateway.NotificadorClienteGateway;
import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
import com.mecanica.oficina_api.application.ordemservico.output.NotificacaoCliente;
import com.mecanica.oficina_api.application.ordemservico.output.TipoNotificacaoCliente;
import com.mecanica.oficina_api.domain.ordemservico.OrcamentoStatus;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServicoStatus;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EnviarOrcamentoUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private NotificadorClienteGateway notificadorClienteGateway;

    @InjectMocks
    private EnviarOrcamentoUseCase useCase;

    @Test
    void deveEnviarOrcamentoENotificarCliente() {
        OrdemServico os = osComOrcamento(OrdemServicoStatus.EM_DIAGNOSTICO, OrcamentoStatus.PENDENTE,
            List.of(itemServico("serv-1")));
        when(ordemServicoGateway.encontrarOuLancar(OS_ID)).thenReturn(os);
        when(ordemServicoGateway.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        OrdemServico resultado = useCase.executar(OS_ID);

        assertThat(resultado.getStatus()).isEqualTo(OrdemServicoStatus.AGUARDANDO_APROVACAO);
        assertThat(resultado.getOrcamento().getStatus()).isEqualTo(OrcamentoStatus.ENVIADO);

        ArgumentCaptor<NotificacaoCliente> captor = ArgumentCaptor.forClass(NotificacaoCliente.class);
        verify(notificadorClienteGateway).notificar(captor.capture());
        assertThat(captor.getValue().tipo()).isEqualTo(TipoNotificacaoCliente.ENVIO_ORCAMENTO);
        assertThat(captor.getValue().ordemServicoId()).isEqualTo(OS_ID);
        assertThat(captor.getValue().clienteId()).isEqualTo(CLIENTE_ID);
    }

    @Test
    void deveLancarExcecao_quandoOsNaoPossuiOrcamento() {
        OrdemServico os = osSemOrcamento(OrdemServicoStatus.EM_DIAGNOSTICO);
        when(ordemServicoGateway.encontrarOuLancar(OS_ID)).thenReturn(os);

        assertThatThrownBy(() -> useCase.executar(OS_ID))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("OS não possui orçamento");

        verify(ordemServicoGateway, never()).atualizar(any());
        verify(notificadorClienteGateway, never()).notificar(any());
    }

    @Test
    void deveLancarExcecao_quandoOrcamentoNaoEstaPendente() {
        OrdemServico os = osComOrcamento(OrdemServicoStatus.AGUARDANDO_APROVACAO, OrcamentoStatus.APROVADO,
            List.of(itemServico("serv-1")));
        when(ordemServicoGateway.encontrarOuLancar(OS_ID)).thenReturn(os);

        assertThatThrownBy(() -> useCase.executar(OS_ID))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Orçamento deve estar PENDENTE");

        verify(notificadorClienteGateway, never()).notificar(any());
    }
}
