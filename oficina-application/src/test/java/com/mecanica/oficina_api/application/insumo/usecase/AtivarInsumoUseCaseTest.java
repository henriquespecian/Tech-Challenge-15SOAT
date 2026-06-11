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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AtivarInsumoUseCaseTest {

    @Mock
    private InsumosGateway insumosGateway;

    @InjectMocks
    private AtivarInsumoUseCase useCase;

    @Test
    void deveAtivarInsumoComSucesso() {
        Insumos insumo = Insumos.reconstituir("ins-1", "Óleo", BigDecimal.valueOf(50), 10, 2, "LITRO");
        when(insumosGateway.buscar("ins-1")).thenReturn(Optional.of(insumo));
        when(insumosGateway.ativar("ins-1")).thenReturn(insumo);

        Insumos resultado = useCase.executar("ins-1");

        assertThat(resultado.getId()).isEqualTo("ins-1");
        verify(insumosGateway).ativar("ins-1");
    }

    @Test
    void deveLancarExcecao_quandoInsumoNaoEncontrado() {
        when(insumosGateway.buscar("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executar("inexistente"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Insumo não encontrado");

        verify(insumosGateway, never()).ativar(anyString());
    }
}
