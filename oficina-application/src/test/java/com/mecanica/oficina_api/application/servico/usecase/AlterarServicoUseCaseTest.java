package com.mecanica.oficina_api.application.servico.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.servico.gateway.ServicoGateway;
import com.mecanica.oficina_api.domain.servico.Servico;

import java.math.BigDecimal;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AlterarServicoUseCaseTest {

    @Mock
    private ServicoGateway servicoGateway;

    @InjectMocks
    private AlterarServicoUseCase useCase;

    @Test
    void deveAlterarServicoComSucesso() {
        Servico existente = Servico.reconstituir("serv-1", "Troca de óleo", "Troca completa", BigDecimal.valueOf(150), Duration.ofHours(2), true);
        Servico alterado = Servico.reconstituir("serv-1", "Troca de óleo premium", "Troca completa", BigDecimal.valueOf(200), Duration.ofHours(3), true);
        when(servicoGateway.buscarOuFalhar("serv-1")).thenReturn(existente);
        when(servicoGateway.alterar(eq("serv-1"), any())).thenReturn(alterado);

        Servico resultado = useCase.executar("serv-1", "Troca de óleo premium", "Troca completa", BigDecimal.valueOf(200), 3);

        assertThat(resultado.getNome()).isEqualTo("Troca de óleo premium");

        ArgumentCaptor<Servico> captor = ArgumentCaptor.forClass(Servico.class);
        verify(servicoGateway).alterar(eq("serv-1"), captor.capture());
        assertThat(captor.getValue().getPreco()).isEqualTo(BigDecimal.valueOf(200));
        assertThat(captor.getValue().getTempoEstimadoHoras()).isEqualTo(Duration.ofHours(3));
    }

    @Test
    void deveLancarExcecao_quandoServicoNaoEncontrado() {
        when(servicoGateway.buscarOuFalhar("inexistente"))
            .thenThrow(new IllegalArgumentException("Serviço não encontrado"));

        assertThatThrownBy(() ->
            useCase.executar("inexistente", "Nome", "Descricao", BigDecimal.TEN, 1)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("Serviço não encontrado");

        verify(servicoGateway, never()).alterar(anyString(), any());
    }
}
