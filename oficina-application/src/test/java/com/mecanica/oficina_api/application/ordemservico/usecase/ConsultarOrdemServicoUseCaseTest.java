package com.mecanica.oficina_api.application.ordemservico.usecase;

import static com.mecanica.oficina_api.application.ordemservico.OrdemServicoFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServicoStatus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsultarOrdemServicoUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @InjectMocks
    private ConsultarOrdemServicoUseCase useCase;

    @Test
    void deveRetornarOrdemServico_quandoEncontrada() {
        OrdemServico os = osSemOrcamento(OrdemServicoStatus.RECEBIDA);
        when(ordemServicoGateway.encontrarOuLancar(OS_ID)).thenReturn(os);

        OrdemServico resultado = useCase.executar(OS_ID);

        assertThat(resultado.getId()).isEqualTo(OS_ID);
        assertThat(resultado.getStatus()).isEqualTo(OrdemServicoStatus.RECEBIDA);
    }

    @Test
    void deveLancarExcecao_quandoOrdemServicoNaoEncontrada() {
        when(ordemServicoGateway.encontrarOuLancar("inexistente"))
            .thenThrow(new IllegalArgumentException("Ordem de serviço não encontrada: inexistente"));

        assertThatThrownBy(() -> useCase.executar("inexistente"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Ordem de serviço não encontrada");
    }
}
