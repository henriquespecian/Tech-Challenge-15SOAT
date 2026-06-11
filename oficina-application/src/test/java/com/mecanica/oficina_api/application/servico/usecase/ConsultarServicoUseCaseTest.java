package com.mecanica.oficina_api.application.servico.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.servico.gateway.ServicoGateway;
import com.mecanica.oficina_api.domain.servico.Servico;

import java.math.BigDecimal;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsultarServicoUseCaseTest {

    @Mock
    private ServicoGateway servicoGateway;

    @InjectMocks
    private ConsultarServicoUseCase useCase;

    @Test
    void deveRetornarServico_quandoEncontrado() {
        Servico servico = Servico.reconstituir("serv-1", "Troca de óleo", "Troca completa", BigDecimal.valueOf(150), Duration.ofHours(2), true);
        when(servicoGateway.buscarOuFalhar("serv-1")).thenReturn(servico);

        Servico resultado = useCase.executar("serv-1");

        assertThat(resultado.getId()).isEqualTo("serv-1");
        assertThat(resultado.getNome()).isEqualTo("Troca de óleo");
    }

    @Test
    void deveLancarExcecao_quandoServicoNaoEncontrado() {
        when(servicoGateway.buscarOuFalhar("inexistente"))
            .thenThrow(new IllegalArgumentException("Serviço não encontrado"));

        assertThatThrownBy(() -> useCase.executar("inexistente"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Serviço não encontrado");
    }
}
