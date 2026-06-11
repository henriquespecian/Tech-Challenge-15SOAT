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
class CadastrarClienteUseCaseTest {

    @Mock
    private ClienteGateway gateway;

    @InjectMocks
    private CadastrarClienteUseCase useCase;

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
    void deveCadastrarClienteComSucesso() {
        when(gateway.existsByDocumento("37518712091")).thenReturn(false);
        when(gateway.save(any())).thenReturn(clienteExistente);

        Cliente resultado = useCase.executar("João Silva", "37518712091", "cliente@teste.com", "5437891237");

        assertThat(resultado.getNome()).isEqualTo("João Silva");
        assertThat(resultado.getDocumento().getValue()).isEqualTo("37518712091");

        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(gateway).save(captor.capture());
        assertThat(captor.getValue().getDocumento().getValue()).isEqualTo("37518712091");
        assertThat(captor.getValue().getEmail().getValue()).isEqualTo("cliente@teste.com");
    }

    @Test
    void deveLancarExcecao_quandoDocumentoDuplicado() {
        when(gateway.existsByDocumento("37518712091")).thenReturn(true);

        assertThatThrownBy(() ->
            useCase.executar("João Silva", "37518712091", "cliente@teste.com", "5437891237")
        ).isInstanceOf(IllegalStateException.class)
         .hasMessageContaining("Documento já cadastrado");

        verify(gateway, never()).save(any());
    }

    @Test
    void deveLancarExcecao_quandoCpfInvalido() {
        assertThatThrownBy(() ->
            useCase.executar("João Silva", "11111111111", "cliente@teste.com", "5437891237")
        ).isInstanceOf(IllegalArgumentException.class);

        verify(gateway, never()).save(any());
    }

    @Test
    void deveLancarExcecao_quandoDocumentoComTamanhoInvalido() {
        assertThatThrownBy(() ->
            useCase.executar("João Silva", "123", "cliente@teste.com", "5437891237")
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("Documento inválido");

        verify(gateway, never()).save(any());
    }
}
