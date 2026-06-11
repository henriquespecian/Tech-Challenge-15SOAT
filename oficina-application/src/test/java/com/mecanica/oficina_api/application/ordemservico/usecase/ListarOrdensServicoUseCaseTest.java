package com.mecanica.oficina_api.application.ordemservico.usecase;

import static com.mecanica.oficina_api.application.ordemservico.OrdemServicoFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServicoStatus;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListarOrdensServicoUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @InjectMocks
    private ListarOrdensServicoUseCase useCase;

    @Test
    void deveListarOrdensPorStatus() {
        OrdemServico os = osSemOrcamento(OrdemServicoStatus.RECEBIDA);
        when(ordemServicoGateway.listar(OrdemServicoStatus.RECEBIDA)).thenReturn(List.of(os));

        List<OrdemServico> resultado = useCase.executar(OrdemServicoStatus.RECEBIDA);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getStatus()).isEqualTo(OrdemServicoStatus.RECEBIDA);
    }

    @Test
    void deveRetornarListaVazia_quandoNaoHaOrdens() {
        when(ordemServicoGateway.listar(null)).thenReturn(List.of());

        List<OrdemServico> resultado = useCase.executar(null);

        assertThat(resultado).isEmpty();
    }
}
