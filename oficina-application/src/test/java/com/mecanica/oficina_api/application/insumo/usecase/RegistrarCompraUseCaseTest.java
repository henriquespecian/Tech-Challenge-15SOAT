package com.mecanica.oficina_api.application.insumo.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.insumo.gateway.InsumosGateway;
import com.mecanica.oficina_api.domain.insumo.Insumos;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegistrarCompraUseCaseTest {

    @Mock
    private InsumosGateway insumosGateway;

    @InjectMocks
    private RegistrarCompraUseCase useCase;

    @Test
    void deveRegistrarCompraAdicionandoEstoque() {
        Insumos insumo = Insumos.reconstituir("ins-1", "Óleo", BigDecimal.valueOf(50), 10, 2, "LITRO");
        when(insumosGateway.buscar("ins-1")).thenReturn(Optional.of(insumo));
        when(insumosGateway.alterar(eq("ins-1"), any())).thenAnswer(inv -> inv.getArgument(1));

        Insumos resultado = useCase.executar("ins-1", 5);

        assertThat(resultado.getEstoqueAtual()).isEqualTo(15);

        ArgumentCaptor<Insumos> captor = ArgumentCaptor.forClass(Insumos.class);
        verify(insumosGateway).alterar(eq("ins-1"), captor.capture());
        assertThat(captor.getValue().getEstoqueAtual()).isEqualTo(15);
    }

    @Test
    void deveLancarExcecao_quandoQuantidadeNula() {
        assertThatThrownBy(() -> useCase.executar("ins-1", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Quantidade");

        verify(insumosGateway, never()).buscar(anyString());
    }

    @Test
    void deveLancarExcecao_quandoQuantidadeZeroOuNegativa() {
        assertThatThrownBy(() -> useCase.executar("ins-1", 0))
            .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> useCase.executar("ins-1", -3))
            .isInstanceOf(IllegalArgumentException.class);

        verify(insumosGateway, never()).alterar(anyString(), any());
    }

    @Test
    void deveLancarExcecao_quandoInsumoNaoEncontrado() {
        when(insumosGateway.buscar("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executar("inexistente", 5))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Insumo não encontrado");

        verify(insumosGateway, never()).alterar(anyString(), any());
    }
}
