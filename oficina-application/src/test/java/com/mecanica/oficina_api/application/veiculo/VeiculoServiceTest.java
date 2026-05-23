package com.mecanica.oficina_api.application.veiculo;

import com.mecanica.oficina_api.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_api.application.veiculo.gateway.VeiculoGateway;
import com.mecanica.oficina_api.domain.cliente.Cliente;
import com.mecanica.oficina_api.domain.veiculo.Veiculo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {

    @Mock
    private VeiculoGateway veiculoGateway;

    @Mock
    private ClienteGateway clienteGateway;

    @InjectMocks
    private VeiculoService veiculoService;

    private Veiculo veiculoExistente(String id) {
        return Veiculo.reconstituir(id, "cliente-1", "ABC1234", "Toyota", "Corolla", 2020, "Branco", true);
    }

    // --- cadastrar ---

    @Test
    void deveCadastrarVeiculoComSucesso() {
        when(clienteGateway.findByIdAtivo("cliente-1")).thenReturn(Optional.of(mock(Cliente.class)));
        when(veiculoGateway.placaExiste("ABC1234")).thenReturn(false);
        when(veiculoGateway.cadastrar(any())).thenReturn(veiculoExistente("veiculo-1"));

        Veiculo resultado = veiculoService.cadastrar("cliente-1", "ABC1234", "Toyota", "Corolla", 2020, "Branco");

        assertThat(resultado.getId()).isEqualTo("veiculo-1");
        assertThat(resultado.getPlaca()).isEqualTo("ABC1234");
        verify(veiculoGateway).cadastrar(any(Veiculo.class));
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        when(clienteGateway.findByIdAtivo("cliente-inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> veiculoService.cadastrar("cliente-inexistente", "ABC1234", "Toyota", "Corolla", 2020, "Branco"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cliente não encontrado");

        verify(veiculoGateway, never()).cadastrar(any());
    }

    @Test
    void deveLancarExcecaoQuandoPlacaJaExiste() {
        when(clienteGateway.findByIdAtivo("cliente-1")).thenReturn(Optional.of(mock(Cliente.class)));
        when(veiculoGateway.placaExiste("ABC1234")).thenReturn(true);

        assertThatThrownBy(() -> veiculoService.cadastrar("cliente-1", "ABC1234", "Toyota", "Corolla", 2020, "Branco"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Já existe um veículo com a placa");

        verify(veiculoGateway, never()).cadastrar(any());
    }

    // --- buscarPorId ---

    @Test
    void deveBuscarVeiculoPorIdComSucesso() {
        when(veiculoGateway.buscar("veiculo-1")).thenReturn(Optional.of(veiculoExistente("veiculo-1")));

        Veiculo resultado = veiculoService.buscarPorId("veiculo-1");

        assertThat(resultado.getId()).isEqualTo("veiculo-1");
        assertThat(resultado.getPlaca()).isEqualTo("ABC1234");
        assertThat(resultado.getClienteId()).isEqualTo("cliente-1");
    }

    @Test
    void deveLancarExcecaoQuandoVeiculoNaoEncontrado() {
        when(veiculoGateway.buscar(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> veiculoService.buscarPorId("inexistente"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Veículo não encontrado");
    }

    // --- listarPorCliente ---

    @Test
    void deveListarVeiculosPorCliente() {
        when(veiculoGateway.buscarVeiculoPorCliente("cliente-1")).thenReturn(
                List.of(veiculoExistente("veiculo-1"), veiculoExistente("veiculo-2")));

        List<Veiculo> resultado = veiculoService.listarPorCliente("cliente-1");

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Veiculo::getClienteId).containsOnly("cliente-1");
    }

    @Test
    void deveRetornarListaVaziaQuandoClienteNaoTemVeiculos() {
        when(veiculoGateway.buscarVeiculoPorCliente("cliente-1")).thenReturn(List.of());

        List<Veiculo> resultado = veiculoService.listarPorCliente("cliente-1");

        assertThat(resultado).isEmpty();
    }

    // --- alterar ---

    @Test
    void deveAlterarVeiculoComSucesso() {
        when(veiculoGateway.buscar("veiculo-1")).thenReturn(Optional.of(veiculoExistente("veiculo-1")));
        when(veiculoGateway.buscarPorPlaca("XYZ9999")).thenReturn(false);
        when(veiculoGateway.alterar(anyString(), any())).thenReturn(
                Veiculo.reconstituir("veiculo-1", "cliente-1", "XYZ9999", "Honda", "Civic", 2023, "Preto", true));

        Veiculo resultado = veiculoService.alterar("veiculo-1", "XYZ9999", "Honda", "Civic", 2023, "Preto");

        assertThat(resultado).isNotNull();
        verify(veiculoGateway).alterar(anyString(), any(Veiculo.class));
    }

    @Test
    void deveAlterarVeiculoMantendoAMesmaPlaca() {
        when(veiculoGateway.buscar("veiculo-1")).thenReturn(Optional.of(veiculoExistente("veiculo-1")));
        when(veiculoGateway.alterar(anyString(), any())).thenReturn(veiculoExistente("veiculo-1"));

        veiculoService.alterar("veiculo-1", "ABC1234", "Toyota", "Camry", 2024, "Prata");

        verify(veiculoGateway, never()).buscarPorPlaca(anyString());
        verify(veiculoGateway).alterar(anyString(), any(Veiculo.class));
    }

    @Test
    void deveLancarExcecaoAoAlterarVeiculoNaoEncontrado() {
        when(veiculoGateway.buscar("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> veiculoService.alterar("inexistente", "XYZ9999", "Honda", "Civic", 2023, "Preto"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Veículo não encontrado");

        verify(veiculoGateway, never()).alterar(anyString(), any());
    }

    @Test
    void deveLancarExcecaoAoAlterarComPlacaJaEmUso() {
        when(veiculoGateway.buscar("veiculo-1")).thenReturn(Optional.of(veiculoExistente("veiculo-1")));
        when(veiculoGateway.buscarPorPlaca("XYZ9999")).thenReturn(true);

        assertThatThrownBy(() -> veiculoService.alterar("veiculo-1", "XYZ9999", "Honda", "Civic", 2023, "Preto"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Já existe um veículo com a placa");

        verify(veiculoGateway, never()).alterar(anyString(), any());
    }

    // --- deletar ---

    @Test
    void deveDeletarVeiculoComSoftDelete() {
        when(veiculoGateway.buscar("veiculo-1")).thenReturn(Optional.of(veiculoExistente("veiculo-1")));

        veiculoService.deletar("veiculo-1");

        verify(veiculoGateway).inativar("veiculo-1");
    }

    @Test
    void deveLancarExcecaoAoDeletarVeiculoNaoEncontrado() {
        when(veiculoGateway.buscar("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> veiculoService.deletar("inexistente"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Veículo não encontrado");

        verify(veiculoGateway, never()).inativar(anyString());
    }
}
