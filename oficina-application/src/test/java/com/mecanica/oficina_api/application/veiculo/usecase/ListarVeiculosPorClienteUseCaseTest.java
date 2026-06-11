package com.mecanica.oficina_api.application.veiculo.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.veiculo.gateway.VeiculoGateway;
import com.mecanica.oficina_api.domain.veiculo.Veiculo;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListarVeiculosPorClienteUseCaseTest {

    @Mock
    private VeiculoGateway veiculoGateway;

    @InjectMocks
    private ListarVeiculosPorClienteUseCase useCase;

    @Test
    void deveRetornarVeiculosDoCliente() {
        Veiculo veiculo = Veiculo.reconstituir("veic-1", "cli-1", "ABC1D23", "Fiat", "Uno", 2020, "Prata", true);
        when(veiculoGateway.buscarVeiculoPorCliente("cli-1")).thenReturn(List.of(veiculo));

        List<Veiculo> resultado = useCase.executar("cli-1");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getClienteId()).isEqualTo("cli-1");
    }

    @Test
    void deveRetornarListaVazia_quandoClienteSemVeiculos() {
        when(veiculoGateway.buscarVeiculoPorCliente("cli-1")).thenReturn(List.of());

        List<Veiculo> resultado = useCase.executar("cli-1");

        assertThat(resultado).isEmpty();
    }
}
