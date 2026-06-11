package com.mecanica.oficina_api.application.cliente.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_api.domain.cliente.Cliente;
import com.mecanica.oficina_api.domain.cliente.Cpf;
import com.mecanica.oficina_api.domain.cliente.Email;
import com.mecanica.oficina_api.domain.cliente.Telefone;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsultarClienteUseCaseTest {

    @Mock
    private ClienteGateway gateway;

    @InjectMocks
    private ConsultarClienteUseCase useCase;

    private Cliente clienteExistente;

    @BeforeEach
    void setUp() {
        clienteExistente = Cliente.reconstituir(
            "id-1",
            "João Silva",
            new Cpf("37518712091"),
            new Email("cliente@teste.com"),
            new Telefone("5437891237"),
            LocalDateTime.now(),
            null
        );
    }

    @Test
    void deveRetornarCliente_quandoEncontrado() {
        when(gateway.findByDocumentoAtivo("37518712091")).thenReturn(Optional.of(clienteExistente));

        Optional<Cliente> resultado = useCase.executar("37518712091");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("João Silva");
    }

    @Test
    void deveRetornarVazio_quandoClienteNaoEncontrado() {
        when(gateway.findByDocumentoAtivo("37518712091")).thenReturn(Optional.empty());

        Optional<Cliente> resultado = useCase.executar("37518712091");

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveLancarExcecao_quandoDocumentoInvalido() {
        assertThatThrownBy(() -> useCase.executar("123"))
            .isInstanceOf(IllegalArgumentException.class);

        verify(gateway, never()).findByDocumentoAtivo(anyString());
    }
}
