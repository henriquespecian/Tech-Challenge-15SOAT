package com.mecanica.oficina_api.application.servico.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.servico.gateway.ServicoGateway;
import com.mecanica.oficina_api.application.servico.gateway.StatusServicoGateway;
import com.mecanica.oficina_api.domain.servico.Servico;
import com.mecanica.oficina_api.domain.servico.TempoMedioServico;

import java.math.BigDecimal;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsultarTempoMedioUseCaseTest {

    @Mock
    private ServicoGateway servicoGateway;

    @Mock
    private StatusServicoGateway statusServicoGateway;

    @InjectMocks
    private ConsultarTempoMedioUseCase useCase;

    @Test
    void deveRetornarTempoMedioDoServico() {
        Servico servico = Servico.reconstituir("serv-1", "Troca de óleo", "Troca completa", BigDecimal.valueOf(150), Duration.ofHours(2), true);
        when(servicoGateway.buscarOuFalhar("serv-1")).thenReturn(servico);
        when(statusServicoGateway.calcularTempoMedioMinutos("serv-1")).thenReturn(95.5);

        TempoMedioServico resultado = useCase.executar("serv-1");

        assertThat(resultado.servicoId()).isEqualTo("serv-1");
        assertThat(resultado.nome()).isEqualTo("Troca de óleo");
        assertThat(resultado.tempoMedioEmMinutos()).isEqualTo(95.5);
    }

    @Test
    void deveLancarExcecao_quandoServicoNaoEncontrado() {
        when(servicoGateway.buscarOuFalhar("inexistente"))
            .thenThrow(new IllegalArgumentException("Serviço não encontrado"));

        assertThatThrownBy(() -> useCase.executar("inexistente"))
            .isInstanceOf(IllegalArgumentException.class);

        verify(statusServicoGateway, never()).calcularTempoMedioMinutos(anyString());
    }
}
