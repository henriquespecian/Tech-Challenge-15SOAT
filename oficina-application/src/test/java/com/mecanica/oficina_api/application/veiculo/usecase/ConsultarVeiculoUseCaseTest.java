package com.mecanica.oficina_api.application.veiculo.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.veiculo.gateway.VeiculoGateway;
import com.mecanica.oficina_api.domain.veiculo.Veiculo;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsultarVeiculoUseCaseTest {

    @Mock
    private VeiculoGateway veiculoGateway;

    @InjectMocks
    private ConsultarVeiculoUseCase useCase;

    @Test
    void deveRetornarVeiculo_quandoEncontrado() {
        Veiculo veiculo = Veiculo.reconstituir("veic-1", "cli-1", "ABC1D23", "Fiat", "Uno", 2020, "Prata", true);
        when(veiculoGateway.buscar("veic-1")).thenReturn(Optional.of(veiculo));

        Veiculo resultado = useCase.executar("veic-1");

        assertThat(resultado.getId()).isEqualTo("veic-1");
        assertThat(resultado.getPlaca()).isEqualTo("ABC1D23");
    }

    @Test
    void deveLancarExcecao_quandoVeiculoNaoEncontrado() {
        when(veiculoGateway.buscar("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executar("inexistente"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Veículo não encontrado");
    }
}
