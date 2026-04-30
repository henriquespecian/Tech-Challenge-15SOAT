package com.mecanica.oficina_api.application.insumo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mecanica.oficina_api.infrastructure.persistence.InsumosJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.repository.InsumosSpringDataRepository;
import com.mecanica.oficina_api.interfaces.dto.request.AlterarInsumosRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarInsumosRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class InsumoServiceTest {

  @Mock
  private InsumosSpringDataRepository repository;

  @InjectMocks
  private InsumosService service;

  private CadastrarInsumosRequest cadastrarRequest;

  @BeforeEach
  void setup() {
    cadastrarRequest = new CadastrarInsumosRequest(
        "Óleo",
        BigDecimal.valueOf(50),
        10,
        2,
        "LITRO",
        true
    );
  }

  // ===============================
  // CADASTRAR
  // ===============================

  @Test
  void deveCadastrarInsumoComSucesso() {

    when(repository.findByNome("Óleo")).thenReturn(Optional.empty());

    service.cadastrar(cadastrarRequest);

    verify(repository, times(1)).save(any(InsumosJpaEntity.class));
  }

  @Test
  void deveLancarErroQuandoInsumoJaExiste() {
    when(repository.findByNome("Óleo"))
        .thenReturn(Optional.of(new InsumosJpaEntity()));

    assertThrows(ResponseStatusException.class, () ->
        service.cadastrar(cadastrarRequest)
    );

    verify(repository, never()).save(any());
  }

  // ===============================
  // LISTAR
  // ===============================

  @Test
  void deveListarInsumosAtivos() {
    InsumosJpaEntity entity = new InsumosJpaEntity();
    entity.setNome("Óleo");
    entity.setPrecoUnitario(BigDecimal.valueOf(50));
    entity.setEstoqueAtual(10);
    entity.setEstoqueMinimo(2);
    entity.setUnidade("LITRO");
    entity.setAtivo(true);

    when(repository.findAllByAtivoTrue()).thenReturn(List.of(entity));

    var resultado = service.listar();

    assertEquals(1, resultado.size());
    assertEquals("Óleo", resultado.get(0).getNome());
  }

  // ===============================
  // ATUALIZAR
  // ===============================

  @Test
  void deveAtualizarInsumoComSucesso() {
    InsumosJpaEntity entity = new InsumosJpaEntity();
    entity.setAtivo(true);

    AlterarInsumosRequest request = new AlterarInsumosRequest(
        "Filtro",
        BigDecimal.valueOf(30),
        5,
        1,
        "UN",
        true
    );

    when(repository.findByIdAndAtivoTrue("1"))
        .thenReturn(Optional.of(entity));

    service.atualizar("1", request);

    verify(repository).save(entity);
    assertEquals("Filtro", entity.getNome());
  }

  @Test
  void deveLancarErroQuandoInsumoNaoEncontradoParaAtualizar() {
    AlterarInsumosRequest request = new AlterarInsumosRequest(
        "Filtro",
        BigDecimal.valueOf(30),
        5,
        1,
        "UN",
        true
    );

    when(repository.findByIdAndAtivoTrue("1"))
        .thenReturn(Optional.empty());

    assertThrows(ResponseStatusException.class, () ->
        service.atualizar("1", request)
    );

    verify(repository, never()).save(any());
  }

  // ===============================
  // DELETAR
  // ===============================

  @Test
  void deveDesativarInsumoQuandoExistir() {
    InsumosJpaEntity entity = new InsumosJpaEntity();
    entity.setAtivo(true);

    when(repository.findById("1"))
        .thenReturn(Optional.of(entity));

    service.deletar("1");

    assertFalse(entity.getAtivo());
    verify(repository).save(entity);
  }

  @Test
  void naoDeveFazerNadaQuandoInsumoNaoExistir() {
    when(repository.findById("1"))
        .thenReturn(Optional.empty());

    service.deletar("1");

    verify(repository, never()).save(any());
  }
}
