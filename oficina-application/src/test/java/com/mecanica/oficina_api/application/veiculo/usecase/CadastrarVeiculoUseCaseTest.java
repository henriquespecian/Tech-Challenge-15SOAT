package com.mecanica.oficina_api.application.veiculo.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_api.application.veiculo.gateway.VeiculoGateway;
import com.mecanica.oficina_api.domain.cliente.Cliente;
import com.mecanica.oficina_api.domain.cliente.Cpf;
import com.mecanica.oficina_api.domain.cliente.Email;
import com.mecanica.oficina_api.domain.cliente.Telefone;
import com.mecanica.oficina_api.domain.veiculo.Veiculo;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CadastrarVeiculoUseCaseTest {

    @Mock
    private VeiculoGateway veiculoGateway;

    @Mock
    private ClienteGateway clienteGateway;

    @InjectMocks
    private CadastrarVeiculoUseCase useCase;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = Cliente.reconstituir(
            "cli-1",
            "João Silva",
            new Cpf("37518712091"),
            new Email("cliente@teste.com"),
            new Telefone("5437891237"),
            LocalDateTime.now(),
            null
        );
    }

    @Test
    void deveCadastrarVeiculoComSucesso() {
        Veiculo salvo = Veiculo.reconstituir("veic-1", "cli-1", "ABC1D23", "Fiat", "Uno", 2020, "Prata", true);
        when(clienteGateway.findByIdAtivo("cli-1")).thenReturn(Optional.of(cliente));
        when(veiculoGateway.placaExiste("ABC1D23")).thenReturn(false);
        when(veiculoGateway.cadastrar(any())).thenReturn(salvo);

        Veiculo resultado = useCase.executar("cli-1", "ABC1D23", "Fiat", "Uno", 2020, "Prata");

        assertThat(resultado.getId()).isEqualTo("veic-1");

        ArgumentCaptor<Veiculo> captor = ArgumentCaptor.forClass(Veiculo.class);
        verify(veiculoGateway).cadastrar(captor.capture());
        assertThat(captor.getValue().getPlaca()).isEqualTo("ABC1D23");
        assertThat(captor.getValue().getClienteId()).isEqualTo("cli-1");
    }

    @Test
    void deveLancarExcecao_quandoClienteNaoEncontrado() {
        when(clienteGateway.findByIdAtivo("cli-x")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
            useCase.executar("cli-x", "ABC1D23", "Fiat", "Uno", 2020, "Prata")
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("Cliente não encontrado");

        verify(veiculoGateway, never()).cadastrar(any());
    }

    @Test
    void deveLancarExcecao_quandoPlacaJaExiste() {
        when(clienteGateway.findByIdAtivo("cli-1")).thenReturn(Optional.of(cliente));
        when(veiculoGateway.placaExiste("ABC1D23")).thenReturn(true);

        assertThatThrownBy(() ->
            useCase.executar("cli-1", "ABC1D23", "Fiat", "Uno", 2020, "Prata")
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("Já existe um veículo com a placa");

        verify(veiculoGateway, never()).cadastrar(any());
    }

    @Test
    void deveLancarExcecao_quandoPlacaInvalida() {
        when(clienteGateway.findByIdAtivo("cli-1")).thenReturn(Optional.of(cliente));
        when(veiculoGateway.placaExiste("PLACA-INVALIDA")).thenReturn(false);

        assertThatThrownBy(() ->
            useCase.executar("cli-1", "PLACA-INVALIDA", "Fiat", "Uno", 2020, "Prata")
        ).isInstanceOf(IllegalArgumentException.class);

        verify(veiculoGateway, never()).cadastrar(any());
    }
}
