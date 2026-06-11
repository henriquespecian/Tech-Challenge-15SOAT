package com.mecanica.oficina_api.application.cliente.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.cliente.gateway.ClienteGateway;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeletarClienteUseCaseTest {

    @Mock
    private ClienteGateway gateway;

    @InjectMocks
    private DeletarClienteUseCase useCase;

    @Test
    void deveDeletarClienteComSucesso() {
        assertThatNoException().isThrownBy(() -> useCase.executar("37518712091"));

        verify(gateway).softDelete("37518712091");
    }

    @Test
    void deveDeletarClienteNormalizandoDocumentoFormatado() {
        useCase.executar("375.187.120-91");

        verify(gateway).softDelete("37518712091");
    }

    @Test
    void deveLancarExcecao_quandoDocumentoInvalido() {
        assertThatThrownBy(() -> useCase.executar("123"))
            .isInstanceOf(IllegalArgumentException.class);

        verify(gateway, never()).softDelete(anyString());
    }
}
