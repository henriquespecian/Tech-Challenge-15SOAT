package com.mecanica.oficina_api.application.cliente.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_api.domain.cliente.Cliente;
import com.mecanica.oficina_api.domain.cliente.Cpf;
import com.mecanica.oficina_api.domain.cliente.Email;
import com.mecanica.oficina_api.domain.cliente.Telefone;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AlterarClienteUseCaseTest {

    @Mock
    private ClienteGateway gateway;

    @InjectMocks
    private AlterarClienteUseCase useCase;

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
    void deveAlterarClienteComSucesso() {
        when(gateway.buscarPorDocumentoOuFalhar("37518712091")).thenReturn(clienteExistente);
        when(gateway.save(any())).thenReturn(clienteExistente);

        useCase.executar("37518712091", "Novo Nome", "novo@email.com", "5499999999");

        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(gateway).save(captor.capture());
        assertThat(captor.getValue().getNome()).isEqualTo("Novo Nome");
        assertThat(captor.getValue().getEmail().getValue()).isEqualTo("novo@email.com");
        assertThat(captor.getValue().getTelefone().getValue()).isEqualTo("5499999999");
    }

    @Test
    void deveLancarExcecao_quandoClienteNaoEncontrado() {
        when(gateway.buscarPorDocumentoOuFalhar("37518712091"))
            .thenThrow(new IllegalArgumentException("Cliente não encontrado"));

        assertThatThrownBy(() ->
            useCase.executar("37518712091", "Novo Nome", "novo@email.com", "5499999999")
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("não encontrado");

        verify(gateway, never()).save(any());
    }

    @Test
    void deveLancarExcecao_quandoDocumentoInvalido() {
        assertThatThrownBy(() ->
            useCase.executar("123", "Novo Nome", "novo@email.com", "5499999999")
        ).isInstanceOf(IllegalArgumentException.class);

        verify(gateway, never()).save(any());
    }
}
