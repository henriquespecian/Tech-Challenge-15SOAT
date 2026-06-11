package com.mecanica.oficina_api.application.veiculo.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.veiculo.gateway.VeiculoGateway;
import com.mecanica.oficina_api.domain.veiculo.Veiculo;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AlterarVeiculoUseCaseTest {

    @Mock
    private VeiculoGateway veiculoGateway;

    @InjectMocks
    private AlterarVeiculoUseCase useCase;

    private Veiculo existente;

    @BeforeEach
    void setUp() {
        existente = Veiculo.reconstituir("veic-1", "cli-1", "ABC1D23", "Fiat", "Uno", 2020, "Prata", true);
    }

    @Test
    void deveAlterarVeiculoMantendoMesmaPlaca() {
        when(veiculoGateway.buscar("veic-1")).thenReturn(Optional.of(existente));
        when(veiculoGateway.alterar(eq("veic-1"), any())).thenAnswer(inv -> inv.getArgument(1));

        Veiculo resultado = useCase.executar("veic-1", "ABC1D23", "Fiat", "Argo", 2022, "Preto");

        assertThat(resultado.getModelo()).isEqualTo("Argo");
        verify(veiculoGateway, never()).buscarPorPlaca(anyString());
    }

    @Test
    void deveAlterarVeiculoComPlacaNovaDisponivel() {
        when(veiculoGateway.buscar("veic-1")).thenReturn(Optional.of(existente));
        when(veiculoGateway.buscarPorPlaca("XYZ9A88")).thenReturn(false);
        when(veiculoGateway.alterar(eq("veic-1"), any())).thenAnswer(inv -> inv.getArgument(1));

        Veiculo resultado = useCase.executar("veic-1", "XYZ9A88", "Fiat", "Uno", 2020, "Prata");

        assertThat(resultado.getPlaca()).isEqualTo("XYZ9A88");

        ArgumentCaptor<Veiculo> captor = ArgumentCaptor.forClass(Veiculo.class);
        verify(veiculoGateway).alterar(eq("veic-1"), captor.capture());
        assertThat(captor.getValue().getClienteId()).isEqualTo("cli-1");
    }

    @Test
    void deveLancarExcecao_quandoVeiculoNaoEncontrado() {
        when(veiculoGateway.buscar("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
            useCase.executar("inexistente", "ABC1D23", "Fiat", "Uno", 2020, "Prata")
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("Veículo não encontrado");

        verify(veiculoGateway, never()).alterar(anyString(), any());
    }

    @Test
    void deveLancarExcecao_quandoPlacaNovaJaExiste() {
        when(veiculoGateway.buscar("veic-1")).thenReturn(Optional.of(existente));
        when(veiculoGateway.buscarPorPlaca("XYZ9A88")).thenReturn(true);

        assertThatThrownBy(() ->
            useCase.executar("veic-1", "XYZ9A88", "Fiat", "Uno", 2020, "Prata")
        ).isInstanceOf(IllegalStateException.class)
         .hasMessageContaining("Já existe um veículo com a placa");

        verify(veiculoGateway, never()).alterar(anyString(), any());
    }
}
