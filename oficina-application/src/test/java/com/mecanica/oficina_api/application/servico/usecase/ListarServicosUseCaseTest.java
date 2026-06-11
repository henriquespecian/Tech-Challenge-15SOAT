package com.mecanica.oficina_api.application.servico.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.servico.gateway.ServicoGateway;
import com.mecanica.oficina_api.domain.servico.Servico;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListarServicosUseCaseTest {

    @Mock
    private ServicoGateway servicoGateway;

    @InjectMocks
    private ListarServicosUseCase useCase;

    @Test
    void deveRetornarListaDeServicos() {
        Servico servico = Servico.reconstituir("serv-1", "Troca de óleo", "Troca completa", BigDecimal.valueOf(150), Duration.ofHours(2), true);
        when(servicoGateway.listar()).thenReturn(List.of(servico));

        List<Servico> resultado = useCase.executar();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Troca de óleo");
    }

    @Test
    void deveRetornarListaVazia_quandoNaoHaServicos() {
        when(servicoGateway.listar()).thenReturn(List.of());

        List<Servico> resultado = useCase.executar();

        assertThat(resultado).isEmpty();
    }
}
