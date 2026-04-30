package com.mecanica.oficina_api.application.veiculo;

import com.mecanica.oficina_api.infrastructure.persistence.ClienteJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.VeiculoJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.repository.ClienteSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.VeiculoSpringDataRepository;
import com.mecanica.oficina_api.interfaces.dto.request.AlterarVeiculoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarVeiculoRequest;
import com.mecanica.oficina_api.interfaces.dto.response.VeiculoResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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

    // --- cadastrar ---

    @Test
    void deveCadastrarVeiculoComSucesso() {
        when(clienteRepository.findById("cliente-1")).thenReturn(Optional.of(clienteEntity));
        when(veiculoRepository.existsByPlacaAndAtivoTrue("ABC1234")).thenReturn(false);

        veiculoService.cadastrar(request);

        verify(veiculoRepository).save(argThat(e -> e.getAtivo()));
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        when(clienteRepository.findById("cliente-inexistente")).thenReturn(Optional.empty());
        request.setClienteId("cliente-inexistente");

        assertThatThrownBy(() -> veiculoService.cadastrar(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Cliente não encontrado");

        verify(veiculoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoPlacaJaExiste() {
        when(clienteRepository.findById("cliente-1")).thenReturn(Optional.of(clienteEntity));
        when(veiculoRepository.existsByPlacaAndAtivoTrue("ABC1234")).thenReturn(true);

        assertThatThrownBy(() -> veiculoService.cadastrar(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Já existe um veículo com a placa");

        verify(veiculoRepository, never()).save(any());
    }

    // --- buscarPorId ---

    @Test
    void deveBuscarVeiculoPorIdComSucesso() {
        when(veiculoRepository.findByIdAndAtivoTrue("veiculo-1")).thenReturn(Optional.of(veiculoEntityComId("veiculo-1")));

        VeiculoResponse response = veiculoService.buscarPorId("veiculo-1");

        assertThat(response.getId()).isEqualTo("veiculo-1");
        assertThat(response.getPlaca()).isEqualTo("ABC1234");
        assertThat(response.getClienteId()).isEqualTo("cliente-1");
    }

    @Test
    void deveLancarExcecaoQuandoVeiculoNaoEncontrado() {
        when(veiculoRepository.findByIdAndAtivoTrue(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> veiculoService.buscarPorId("inexistente"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode())
                        .isEqualTo(HttpStatus.NOT_FOUND));
    }

    // --- listarPorCliente ---

    @Test
    void deveListarVeiculosPorCliente() {
        List<VeiculoJpaEntity> entities = List.of(
                veiculoEntityComId("veiculo-1"),
                veiculoEntityComId("veiculo-2")
        );
        when(veiculoRepository.findByCliente_IdAndAtivoTrue("cliente-1")).thenReturn(entities);

        List<VeiculoResponse> responses = veiculoService.listarPorCliente("cliente-1");

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(VeiculoResponse::getClienteId).containsOnly("cliente-1");
    }

    @Test
    void deveRetornarListaVaziaQuandoClienteNaoTemVeiculos() {
        when(veiculoRepository.findByCliente_IdAndAtivoTrue("cliente-1")).thenReturn(List.of());

        List<VeiculoResponse> responses = veiculoService.listarPorCliente("cliente-1");

        assertThat(responses).isEmpty();
    }

    // --- alterar ---

    @Test
    void deveAlterarVeiculoComSucesso() {
        VeiculoJpaEntity entity = veiculoEntityComId("veiculo-1");
        when(veiculoRepository.findByIdAndAtivoTrue("veiculo-1")).thenReturn(Optional.of(entity));
        when(veiculoRepository.existsByPlacaAndAtivoTrue("XYZ9999")).thenReturn(false);
        when(veiculoRepository.save(any(VeiculoJpaEntity.class))).thenReturn(entity);

        AlterarVeiculoRequest request = new AlterarVeiculoRequest();
        request.setPlaca("XYZ9999");
        request.setMarca("Honda");
        request.setModelo("Civic");
        request.setAno(2023);
        request.setCor("Preto");

        VeiculoResponse response = veiculoService.alterar("veiculo-1", request);

        assertThat(response).isNotNull();
        verify(veiculoRepository).save(any(VeiculoJpaEntity.class));
    }

    @Test
    void deveAlterarVeiculoMantendoAMesmaPlaca() {
        VeiculoJpaEntity entity = veiculoEntityComId("veiculo-1");
        when(veiculoRepository.findByIdAndAtivoTrue("veiculo-1")).thenReturn(Optional.of(entity));
        when(veiculoRepository.save(any(VeiculoJpaEntity.class))).thenReturn(entity);

        AlterarVeiculoRequest request = new AlterarVeiculoRequest();
        request.setPlaca("ABC1234"); // mesma placa do entity
        request.setMarca("Toyota");
        request.setModelo("Camry");
        request.setAno(2024);
        request.setCor("Prata");

        veiculoService.alterar("veiculo-1", request);

        verify(veiculoRepository, never()).existsByPlacaAndAtivoTrue(anyString());
        verify(veiculoRepository).save(any(VeiculoJpaEntity.class));
    }

    @Test
    void deveLancarExcecaoAoAlterarVeiculoNaoEncontrado() {
        when(veiculoRepository.findByIdAndAtivoTrue("inexistente")).thenReturn(Optional.empty());

        AlterarVeiculoRequest request = new AlterarVeiculoRequest();
        request.setPlaca("XYZ9999");

        assertThatThrownBy(() -> veiculoService.alterar("inexistente", request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode())
                        .isEqualTo(HttpStatus.NOT_FOUND));

        verify(veiculoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoAlterarComPlacaJaEmUso() {
        VeiculoJpaEntity entity = veiculoEntityComId("veiculo-1");
        when(veiculoRepository.findByIdAndAtivoTrue("veiculo-1")).thenReturn(Optional.of(entity));
        when(veiculoRepository.existsByPlacaAndAtivoTrue("XYZ9999")).thenReturn(true);

        AlterarVeiculoRequest request = new AlterarVeiculoRequest();
        request.setPlaca("XYZ9999");

        assertThatThrownBy(() -> veiculoService.alterar("veiculo-1", request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Já existe um veículo com a placa");

        verify(veiculoRepository, never()).save(any());
    }

    // --- deletar ---

    @Test
    void deveDeletarVeiculoComSoftDelete() {
        VeiculoJpaEntity entity = veiculoEntityComId("veiculo-1");
        entity.setAtivo(true);
        when(veiculoRepository.findByIdAndAtivoTrue("veiculo-1")).thenReturn(Optional.of(entity));

        veiculoService.deletar("veiculo-1");

        verify(veiculoRepository).save(argThat(e -> !e.getAtivo()));
    }

    @Test
    void deveNaoSalvarQuandoVeiculoNaoEncontradoAoDeletar() {
        when(veiculoRepository.findByIdAndAtivoTrue("inexistente")).thenReturn(Optional.empty());

        veiculoService.deletar("inexistente");

        verify(veiculoRepository, never()).save(any());
    }

    // --- helpers ---

    private VeiculoJpaEntity veiculoEntityComId(String id) {
        VeiculoJpaEntity entity = new VeiculoJpaEntity();
        entity.setId(id);
        entity.setPlaca("ABC1234");
        entity.setMarca("Toyota");
        entity.setModelo("Corolla");
        entity.setAno(2020);
        entity.setCor("Branco");
        entity.setAtivo(true);
        entity.setCliente(clienteEntity);
        return entity;
    }
}
