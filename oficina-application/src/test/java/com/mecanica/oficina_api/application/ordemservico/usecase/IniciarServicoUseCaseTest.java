package com.mecanica.oficina_api.application.ordemservico.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.servico.gateway.StatusServicoGateway;
import com.mecanica.oficina_api.domain.ordemservico.ServicoStatus;
import com.mecanica.oficina_api.domain.ordemservico.StatusServico;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IniciarServicoUseCaseTest {

    @Mock
    private StatusServicoGateway statusServicoGateway;

    @InjectMocks
    private IniciarServicoUseCase useCase;

    @Test
    void deveIniciarServicoComSucesso() {
        StatusServico statusServico = StatusServico.criar("os-1", "serv-1");
        when(statusServicoGateway.buscarPorIdEStatus("ss-1", ServicoStatus.AGUARDANDO))
            .thenReturn(Optional.of(statusServico));
        when(statusServicoGateway.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        StatusServico resultado = useCase.executar("ss-1");

        assertThat(resultado.getStatus()).isEqualTo(ServicoStatus.INICIADO);
        assertThat(resultado.getDataInicio()).isNotNull();
        verify(statusServicoGateway).atualizar(statusServico);
    }

    @Test
    void deveLancarExcecao_quandoServicoNaoEncontradoEmAguardando() {
        when(statusServicoGateway.buscarPorIdEStatus("ss-1", ServicoStatus.AGUARDANDO))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executar("ss-1"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Serviço não encontrado");

        verify(statusServicoGateway, never()).atualizar(any());
    }
}
