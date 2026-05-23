package com.mecanica.oficina_api.application.cliente;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_api.domain.cliente.Cliente;
import com.mecanica.oficina_api.domain.cliente.Cpf;
import com.mecanica.oficina_api.domain.cliente.Email;
import com.mecanica.oficina_api.domain.cliente.Telefone;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteGateway gateway;

    @InjectMocks
    private ClienteService clienteService;

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

    // --- cadastrar ---

    @Test
    void deveCadastrarClienteComSucesso() {
        when(gateway.existsByDocumento("37518712091")).thenReturn(false);
        when(gateway.save(any())).thenReturn(clienteExistente);

        Cliente resultado = clienteService.cadastrar("João Silva", "37518712091", "cliente@teste.com", "5437891237");

        assertThat(resultado.getNome()).isEqualTo("João Silva");
        assertThat(resultado.getDocumento().getValue()).isEqualTo("37518712091");
        verify(gateway).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoAoCadastrarDocumentoDuplicado() {
        when(gateway.existsByDocumento("37518712091")).thenReturn(true);

        assertThatThrownBy(() ->
            clienteService.cadastrar("João Silva", "37518712091", "cliente@teste.com", "5437891237")
        ).isInstanceOf(IllegalStateException.class)
         .hasMessageContaining("Documento já cadastrado");

        verify(gateway, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoCadastrarCpfInvalido() {
        assertThatThrownBy(() ->
            clienteService.cadastrar("João Silva", "11111111111", "cliente@teste.com", "5437891237")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    // --- consultar ---

    @Test
    void deveRetornarClienteAoConsultar() {
        when(gateway.findByDocumentoAtivo("37518712091")).thenReturn(Optional.of(clienteExistente));

        Optional<Cliente> resultado = clienteService.consultar("37518712091");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("João Silva");
    }

    @Test
    void deveRetornarVazioQuandoClienteNaoEncontrado() {
        when(gateway.findByDocumentoAtivo("37518712091")).thenReturn(Optional.empty());

        Optional<Cliente> resultado = clienteService.consultar("37518712091");

        assertThat(resultado).isEmpty();
    }

    // --- listar ---

    @Test
    void deveRetornarListaDeClientes() {
        when(gateway.findAllAtivos()).thenReturn(List.of(clienteExistente));

        List<Cliente> resultado = clienteService.listar();

        assertThat(resultado).hasSize(1);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaClientes() {
        when(gateway.findAllAtivos()).thenReturn(List.of());

        List<Cliente> resultado = clienteService.listar();

        assertThat(resultado).isEmpty();
    }

    // --- alterar ---

    @Test
    void deveAlterarClienteComSucesso() {
        when(gateway.findByDocumentoAtivo("37518712091")).thenReturn(Optional.of(clienteExistente));
        when(gateway.save(any())).thenReturn(clienteExistente);

        assertThatNoException().isThrownBy(() ->
            clienteService.alterar("37518712091", "Novo Nome", "novo@email.com", "5499999999")
        );

        verify(gateway).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoAoAlterarClienteNaoEncontrado() {
        when(gateway.findByDocumentoAtivo("37518712091")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
            clienteService.alterar("37518712091", "Novo Nome", "novo@email.com", "5499999999")
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("não encontrado");
    }

    // --- deletar ---

    @Test
    void deveDeletarClienteComSucesso() {
        assertThatNoException().isThrownBy(() ->
            clienteService.deletar("37518712091")
        );

        verify(gateway).softDelete("37518712091");
    }
}
