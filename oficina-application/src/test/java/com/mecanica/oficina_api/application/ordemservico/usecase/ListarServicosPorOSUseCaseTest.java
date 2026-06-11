package com.mecanica.oficina_api.application.ordemservico.usecase;

import static com.mecanica.oficina_api.application.ordemservico.OrdemServicoFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.servico.gateway.StatusServicoGateway;
import com.mecanica.oficina_api.domain.ordemservico.StatusServico;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListarServicosPorOSUseCaseTest {

    @Mock
    private StatusServicoGateway statusServicoGateway;

    @InjectMocks
    private ListarServicosPorOSUseCase useCase;

    @Test
    void deveListarServicosDaOrdemServico() {
        StatusServico statusServico = StatusServico.criar(OS_ID, "serv-1");
        when(statusServicoGateway.listarServicosPorOS(OS_ID)).thenReturn(List.of(statusServico));

        List<StatusServico> resultado = useCase.executar(OS_ID);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getServicoId()).isEqualTo("serv-1");
    }

    @Test
    void deveRetornarListaVazia_quandoOrdemSemServicos() {
        when(statusServicoGateway.listarServicosPorOS(OS_ID)).thenReturn(List.of());

        List<StatusServico> resultado = useCase.executar(OS_ID);

        assertThat(resultado).isEmpty();
    }
}
