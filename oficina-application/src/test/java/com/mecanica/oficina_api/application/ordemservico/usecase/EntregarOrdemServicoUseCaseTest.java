package com.mecanica.oficina_api.application.ordemservico.usecase;

import static com.mecanica.oficina_api.application.ordemservico.OrdemServicoFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.ordemservico.gateway.NotificadorClienteGateway;
import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServicoStatus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EntregarOrdemServicoUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;
    @Mock
    private NotificadorClienteGateway notificadorClienteGateway;

    @InjectMocks
    private EntregarOrdemServicoUseCase useCase;

    @Test
    void deveEntregarOs_quandoFinalizada() {
        OrdemServico os = osSemOrcamento(OrdemServicoStatus.FINALIZADA);
        when(ordemServicoGateway.encontrarOuLancar(OS_ID)).thenReturn(os);
        when(ordemServicoGateway.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        OrdemServico resultado = useCase.executar(OS_ID);

        assertThat(resultado.getStatus()).isEqualTo(OrdemServicoStatus.ENTREGUE);
        verify(ordemServicoGateway).atualizar(os);
    }

    @Test
    void deveLancarExcecao_quandoOsNaoEstaFinalizada() {
        OrdemServico os = osSemOrcamento(OrdemServicoStatus.EM_EXECUCAO);
        when(ordemServicoGateway.encontrarOuLancar(OS_ID)).thenReturn(os);

        assertThatThrownBy(() -> useCase.executar(OS_ID))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("OS deve estar FINALIZADA");

        verify(ordemServicoGateway, never()).atualizar(any());
    }
}
