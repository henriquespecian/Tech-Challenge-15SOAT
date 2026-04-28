package com.mecanica.oficina_api.application.veiculo;

import com.mecanica.oficina_api.infrastructure.persistence.ClienteJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.VeiculoJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.repository.ClienteSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.VeiculoSpringDataRepository;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarVeiculoRequest;
import com.mecanica.oficina_api.interfaces.dto.response.VeiculoResponse;

import org.junit.jupiter.api.BeforeEach;
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
    private VeiculoSpringDataRepository veiculoRepository;

    @Mock
    private ClienteSpringDataRepository clienteRepository;

    @InjectMocks
    private VeiculoService veiculoService;

    private ClienteJpaEntity clienteEntity;
    private CadastrarVeiculoRequest request;

    @BeforeEach
    void setUp() {
        clienteEntity = new ClienteJpaEntity();
        clienteEntity.setId("cliente-1");
        clienteEntity.setNome("João Silva");

        request = new CadastrarVeiculoRequest();
        request.setClienteId("cliente-1");
        request.setPlaca("ABC1234");
        request.setMarca("Toyota");
        request.setModelo("Corolla");
        request.setAno(2020);
        request.setCor("Branco");
    }

    @Test
    void deveCadastrarVeiculoComSucesso() {
        when(clienteRepository.findById("cliente-1")).thenReturn(Optional.of(clienteEntity));
        when(veiculoRepository.existsByPlaca("ABC1234")).thenReturn(false);

        veiculoService.cadastrar(request);

        verify(veiculoRepository).save(any(VeiculoJpaEntity.class));
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        when(clienteRepository.findById("cliente-inexistente")).thenReturn(Optional.empty());
        request.setClienteId("cliente-inexistente");

        assertThatThrownBy(() -> veiculoService.cadastrar(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cliente não encontrado");

        verify(veiculoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoPlacaJaExiste() {
        when(clienteRepository.findById("cliente-1")).thenReturn(Optional.of(clienteEntity));
        when(veiculoRepository.existsByPlaca("ABC1234")).thenReturn(true);

        assertThatThrownBy(() -> veiculoService.cadastrar(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Já existe um veículo com a placa");

        verify(veiculoRepository, never()).save(any());
    }

    @Test
    void deveBuscarVeiculoPorIdComSucesso() {
        when(veiculoRepository.findById("veiculo-1")).thenReturn(Optional.of(veiculoEntityComId("veiculo-1")));

        VeiculoResponse response = veiculoService.buscarPorId("veiculo-1");

        assertThat(response.getId()).isEqualTo("veiculo-1");
        assertThat(response.getPlaca()).isEqualTo("ABC1234");
        assertThat(response.getClienteId()).isEqualTo("cliente-1");
    }

    @Test
    void deveLancarExcecaoQuandoVeiculoNaoEncontrado() {
        when(veiculoRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> veiculoService.buscarPorId("inexistente"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Veículo não encontrado");
    }

    @Test
    void deveListarVeiculosPorCliente() {
        List<VeiculoJpaEntity> entities = List.of(
                veiculoEntityComId("veiculo-1"),
                veiculoEntityComId("veiculo-2")
        );
        when(veiculoRepository.findByCliente_Id("cliente-1")).thenReturn(entities);

        List<VeiculoResponse> responses = veiculoService.listarPorCliente("cliente-1");

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(VeiculoResponse::getClienteId).containsOnly("cliente-1");
    }

    @Test
    void deveRetornarListaVaziaQuandoClienteNaoTemVeiculos() {
        when(veiculoRepository.findByCliente_Id("cliente-1")).thenReturn(List.of());

        List<VeiculoResponse> responses = veiculoService.listarPorCliente("cliente-1");

        assertThat(responses).isEmpty();
    }

    private VeiculoJpaEntity veiculoEntityComId(String id) {
        VeiculoJpaEntity entity = new VeiculoJpaEntity();
        entity.setId(id);
        entity.setPlaca("ABC1234");
        entity.setMarca("Toyota");
        entity.setModelo("Corolla");
        entity.setAno(2020);
        entity.setCor("Branco");
        entity.setCliente(clienteEntity);
        return entity;
    }
}
