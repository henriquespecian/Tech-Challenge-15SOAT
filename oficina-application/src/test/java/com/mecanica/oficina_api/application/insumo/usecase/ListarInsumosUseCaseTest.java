package com.mecanica.oficina_api.application.insumo.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.insumo.gateway.InsumosGateway;
import com.mecanica.oficina_api.domain.insumo.Insumos;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListarInsumosUseCaseTest {

    @Mock
    private InsumosGateway insumosGateway;

    @InjectMocks
    private ListarInsumosUseCase useCase;

    @Test
    void deveRetornarListaDeInsumos() {
        Insumos insumo = Insumos.reconstituir("ins-1", "Óleo", BigDecimal.valueOf(50), 10, 2, "LITRO");
        when(insumosGateway.listar()).thenReturn(List.of(insumo));

        List<Insumos> resultado = useCase.executar();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Óleo");
    }

    @Test
    void deveRetornarListaVazia_quandoNaoHaInsumos() {
        when(insumosGateway.listar()).thenReturn(List.of());

        List<Insumos> resultado = useCase.executar();

        assertThat(resultado).isEmpty();
    }
}
