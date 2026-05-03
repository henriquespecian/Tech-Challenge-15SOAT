package com.mecanica.oficina_api.application.servico;

import com.mecanica.oficina_api.infrastructure.persistence.ServicoJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.repository.ServicoSpringDataRepository;
import com.mecanica.oficina_api.interfaces.dto.request.AlterarServicoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarServicoRequest;
import com.mecanica.oficina_api.interfaces.dto.response.ServicoResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoServiceTest {

    @Mock
    private ServicoSpringDataRepository repository;

    @InjectMocks
    private ServicoService servicoService;

    private ServicoJpaEntity entityPadrao;
    private CadastrarServicoRequest cadastrarRequest;

    @BeforeEach
    void setUp() {
        entityPadrao = servicoEntity("srv-1", "Troca de óleo", true);

        cadastrarRequest = new CadastrarServicoRequest();
        cadastrarRequest.setNome("Troca de óleo");
        cadastrarRequest.setDescricao("Troca completa do óleo do motor");
        cadastrarRequest.setPreco(BigDecimal.valueOf(150));
        cadastrarRequest.setTempoEstimadoHoras(1);
    }

    // --- cadastrar ---

    @Test
    void deveCadastrarServicoComSucesso() {
        when(repository.save(any(ServicoJpaEntity.class))).thenReturn(entityPadrao);

        ServicoResponse resp = servicoService.cadastrar(cadastrarRequest);

        assertThat(resp.getId()).isEqualTo("srv-1");
        assertThat(resp.getNome()).isEqualTo("Troca de óleo");
        assertThat(resp.isAtivo()).isTrue();
        verify(repository).save(any(ServicoJpaEntity.class));
    }

    @Test
    void deveLancarExcecaoAoCadastrarComNomeNulo() {
        cadastrarRequest.setNome(null);

        assertThatThrownBy(() -> servicoService.cadastrar(cadastrarRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nome é obrigatório");

        verify(repository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoCadastrarComPrecoNegativo() {
        cadastrarRequest.setPreco(BigDecimal.valueOf(-1));

        assertThatThrownBy(() -> servicoService.cadastrar(cadastrarRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Preço deve ser um número positivo");

        verify(repository, never()).save(any());
    }

    // --- buscar ---

    @Test
    void deveBuscarServicoComSucesso() {
        when(repository.findById("srv-1")).thenReturn(Optional.of(entityPadrao));

        ServicoResponse resp = servicoService.buscar("srv-1");

        assertThat(resp.getId()).isEqualTo("srv-1");
        assertThat(resp.getNome()).isEqualTo("Troca de óleo");
    }

    @Test
    void deveLancarExcecaoQuandoServicoNaoEncontradoAoBuscar() {
        when(repository.findById("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicoService.buscar("inexistente"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode())
                        .isEqualTo(HttpStatus.NOT_FOUND));
    }

    // --- listar ---

    @Test
    void deveListarServicosAtivos() {
        when(repository.findAllByAtivoTrue()).thenReturn(List.of(
                servicoEntity("srv-1", "Troca de óleo", true),
                servicoEntity("srv-2", "Alinhamento", true)));

        List<ServicoResponse> resp = servicoService.listar();

        assertThat(resp).hasSize(2);
        assertThat(resp).extracting(ServicoResponse::getNome)
                .containsExactly("Troca de óleo", "Alinhamento");
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaServicosAtivos() {
        when(repository.findAllByAtivoTrue()).thenReturn(List.of());

        List<ServicoResponse> resp = servicoService.listar();

        assertThat(resp).isEmpty();
    }

    // --- alterar ---

    @Test
    void deveAlterarServicoComSucesso() {
        ServicoJpaEntity entityAtualizado = servicoEntity("srv-1", "Alinhamento e balanceamento", true);
        when(repository.findById("srv-1")).thenReturn(Optional.of(entityPadrao));
        when(repository.save(any(ServicoJpaEntity.class))).thenReturn(entityAtualizado);

        AlterarServicoRequest req = new AlterarServicoRequest();
        req.setNome("Alinhamento e balanceamento");
        req.setDescricao("Alinhamento e balanceamento completo");
        req.setPreco(BigDecimal.valueOf(200));
        req.setTempoEstimadoHoras(2);

        ServicoResponse resp = servicoService.alterar("srv-1", req);

        assertThat(resp.getNome()).isEqualTo("Alinhamento e balanceamento");
        verify(repository).save(any(ServicoJpaEntity.class));
    }

    @Test
    void deveLancarExcecaoQuandoServicoNaoEncontradoAoAlterar() {
        when(repository.findById("inexistente")).thenReturn(Optional.empty());

        AlterarServicoRequest req = new AlterarServicoRequest();
        req.setNome("Qualquer");
        req.setDescricao("Desc");
        req.setPreco(BigDecimal.valueOf(100));
        req.setTempoEstimadoHoras(1);

        assertThatThrownBy(() -> servicoService.alterar("inexistente", req))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode())
                        .isEqualTo(HttpStatus.NOT_FOUND));

        verify(repository, never()).save(any());
    }

    // --- ativar ---

    @Test
    void deveAtivarServicoComSucesso() {
        ServicoJpaEntity inativo = servicoEntity("srv-1", "Troca de óleo", false);
        when(repository.findById("srv-1")).thenReturn(Optional.of(inativo));
        when(repository.save(any(ServicoJpaEntity.class))).thenReturn(entityPadrao);

        ServicoResponse resp = servicoService.ativar("srv-1");

        assertThat(resp.isAtivo()).isTrue();
        verify(repository).save(argThat(ServicoJpaEntity::isAtivo));
    }

    @Test
    void deveLancarExcecaoQuandoServicoNaoEncontradoAoAtivar() {
        when(repository.findById("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicoService.ativar("inexistente"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode())
                        .isEqualTo(HttpStatus.NOT_FOUND));

        verify(repository, never()).save(any());
    }

    // --- inativar ---

    @Test
    void deveInativarServicoComSucesso() {
        when(repository.findById("srv-1")).thenReturn(Optional.of(entityPadrao));
        when(repository.save(any(ServicoJpaEntity.class))).thenReturn(entityPadrao);

        servicoService.inativar("srv-1");

        verify(repository).save(argThat(e -> !e.isAtivo()));
    }

    @Test
    void deveLancarExcecaoQuandoServicoNaoEncontradoAoInativar() {
        when(repository.findById("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicoService.inativar("inexistente"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode())
                        .isEqualTo(HttpStatus.NOT_FOUND));

        verify(repository, never()).save(any());
    }

    // --- helpers ---

    private ServicoJpaEntity servicoEntity(String id, String nome, boolean ativo) {
        ServicoJpaEntity e = new ServicoJpaEntity();
        e.setId(id);
        e.setNome(nome);
        e.setDescricao("Descrição padrão");
        e.setPreco(BigDecimal.valueOf(150));
        e.setTempoEstimadoHoras(Duration.ofHours(1));
        e.setAtivo(ativo);
        return e;
    }
}
