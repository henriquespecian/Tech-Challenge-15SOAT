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
class CadastrarServicoUseCaseTest {

    @Mock
    private ServicoGateway servicoGateway;

    @InjectMocks
    private CadastrarServicoUseCase useCase;

    @Test
    void deveCadastrarServicoComSucesso() {
        Servico salvo = Servico.reconstituir("serv-1", "Troca de óleo", "Troca completa", BigDecimal.valueOf(150), Duration.ofHours(2), true);
        when(servicoGateway.cadastrar(any())).thenReturn(salvo);

        Servico resultado = useCase.executar("Troca de óleo", "Troca completa", BigDecimal.valueOf(150), 2);

        assertThat(resultado.getId()).isEqualTo("serv-1");

        ArgumentCaptor<Servico> captor = ArgumentCaptor.forClass(Servico.class);
        verify(servicoGateway).cadastrar(captor.capture());
        assertThat(captor.getValue().getNome()).isEqualTo("Troca de óleo");
        assertThat(captor.getValue().getTempoEstimadoHoras()).isEqualTo(Duration.ofHours(2));
        assertThat(captor.getValue().isAtivo()).isTrue();
    }

    @Test
    void deveLancarExcecao_quandoPrecoNegativo() {
        assertThatThrownBy(() -> useCase.executar("Troca de óleo", "Troca completa", BigDecimal.valueOf(-10), 2))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Preço");

        verify(servicoGateway, never()).cadastrar(any());
    }

    @Test
    void deveLancarExcecao_quandoNomeEmBranco() {
        assertThatThrownBy(() -> useCase.executar("  ", "Troca completa", BigDecimal.valueOf(150), 2))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Nome");

        verify(servicoGateway, never()).cadastrar(any());
    }
}
