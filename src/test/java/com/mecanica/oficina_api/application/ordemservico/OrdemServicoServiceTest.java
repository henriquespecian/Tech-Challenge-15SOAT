package com.mecanica.oficina_api.application.ordemservico;

import com.mecanica.oficina_api.domain.ordemservico.OrdemServicoStatus;
import com.mecanica.oficina_api.infrastructure.persistence.ClienteJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.InsumosJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.ItemOrcamentoJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.OrdemServicoJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.ServicoJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.VeiculoJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.repository.ClienteSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.InsumosSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.OrdemServicoSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.ServicoSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.VeiculoSpringDataRepository;
import com.mecanica.oficina_api.application.insumo.NotificadorEstoqueBaixo;
import com.mecanica.oficina_api.infrastructure.security.UsuarioPrincipal;
import com.mecanica.oficina_api.interfaces.dto.request.CriarOrdemServicoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.GerarOrcamentoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.ItemOrcamentoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.ItemServicoRequest;
import com.mecanica.oficina_api.interfaces.dto.response.MinhaOrdemServicoResponse;
import com.mecanica.oficina_api.interfaces.dto.response.OrdemServicoResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdemServicoServiceTest {

    @Mock private OrdemServicoSpringDataRepository ordemServicoRepository;
    @Mock private VeiculoSpringDataRepository veiculoRepository;
    @Mock private ClienteSpringDataRepository clienteRepository;
    @Mock private InsumosSpringDataRepository insumosRepository;
    @Mock private ServicoSpringDataRepository servicoRepository;
    @Mock private NotificadorEstoqueBaixo notificadorEstoqueBaixo;
    @InjectMocks private OrdemServicoService service;

    private VeiculoJpaEntity veiculo;
    private ClienteJpaEntity cliente;
    private OrdemServicoJpaEntity osEntity;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @BeforeEach
    void setUp() {
        veiculo = new VeiculoJpaEntity();
        veiculo.setId("veiculo-1");
        veiculo.setPlaca("ABC1234");
        veiculo.setMarca("Toyota");
        veiculo.setModelo("Corolla");
        veiculo.setAno(2020);
        veiculo.setCor("Branco");
        veiculo.setAtivo(true);

        cliente = new ClienteJpaEntity();
        cliente.setId("cliente-1");
        cliente.setAtivo(true);

        osEntity = new OrdemServicoJpaEntity();
        osEntity.setId("os-1");
        osEntity.setVeiculoId("veiculo-1");
        osEntity.setClienteId("cliente-1");
        osEntity.setStatus("RECEBIDA");
    }

    // --- criar ---

    @Test
    void deveCriarOrdemServicoComSucesso() {
        OrdemServicoJpaEntity salva = new OrdemServicoJpaEntity();
        salva.setId("os-1");
        salva.setVeiculoId("veiculo-1");
        salva.setClienteId("cliente-1");
        salva.setStatus("RECEBIDA");

        when(veiculoRepository.findByIdAndAtivoTrue("veiculo-1")).thenReturn(Optional.of(veiculo));
        when(clienteRepository.findById("cliente-1")).thenReturn(Optional.of(cliente));
        when(ordemServicoRepository.save(any())).thenReturn(salva);

        CriarOrdemServicoRequest req = new CriarOrdemServicoRequest();
        req.setVeiculoId("veiculo-1");
        req.setClienteId("cliente-1");

        OrdemServicoResponse resp = service.criar(req);

        assertThat(resp.getId()).isEqualTo("os-1");
        assertThat(resp.getStatus()).isEqualTo("RECEBIDA");
        verify(ordemServicoRepository).save(argThat(e -> "RECEBIDA".equals(e.getStatus())));
    }

    @Test
    void deveLancarExcecaoQuandoVeiculoNaoEncontradoAoCriar() {
        when(veiculoRepository.findByIdAndAtivoTrue("veiculo-x")).thenReturn(Optional.empty());

        CriarOrdemServicoRequest req = new CriarOrdemServicoRequest();
        req.setVeiculoId("veiculo-x");
        req.setClienteId("cliente-1");

        assertThatThrownBy(() -> service.criar(req))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));

        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoEncontradoAoCriar() {
        when(veiculoRepository.findByIdAndAtivoTrue("veiculo-1")).thenReturn(Optional.of(veiculo));
        when(clienteRepository.findById("cliente-x")).thenReturn(Optional.empty());

        CriarOrdemServicoRequest req = new CriarOrdemServicoRequest();
        req.setVeiculoId("veiculo-1");
        req.setClienteId("cliente-x");

        assertThatThrownBy(() -> service.criar(req))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));

        verify(ordemServicoRepository, never()).save(any());
    }

    // --- buscarPorId ---

    @Test
    void deveBuscarPorIdComSucesso() {
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));

        OrdemServicoResponse resp = service.buscarPorId("os-1");

        assertThat(resp.getId()).isEqualTo("os-1");
        assertThat(resp.getStatus()).isEqualTo("RECEBIDA");
        assertThat(resp.getOrcamento()).isNull();
    }

    @Test
    void deveLancarExcecaoQuandoOsNaoEncontrada() {
        when(ordemServicoRepository.findById("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId("inexistente"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    // --- listar ---

    @Test
    void deveListarTodasAsOsSemFiltro() {
        when(ordemServicoRepository.findAll()).thenReturn(List.of(osEntity));

        List<OrdemServicoResponse> lista = service.listar(null);

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).getId()).isEqualTo("os-1");
        verify(ordemServicoRepository).findAll();
        verify(ordemServicoRepository, never()).findByStatus(any());
    }

    @Test
    void deveListarOsComFiltroDeStatus() {
        when(ordemServicoRepository.findByStatus("RECEBIDA")).thenReturn(List.of(osEntity));

        List<OrdemServicoResponse> lista = service.listar(OrdemServicoStatus.RECEBIDA);

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).getStatus()).isEqualTo("RECEBIDA");
        verify(ordemServicoRepository).findByStatus("RECEBIDA");
        verify(ordemServicoRepository, never()).findAll();
    }

    // --- listarPorVeiculo ---

    @Test
    void deveListarPorVeiculoComSucesso() {
        when(ordemServicoRepository.findByVeiculoId("veiculo-1")).thenReturn(List.of(osEntity));

        List<OrdemServicoResponse> lista = service.listarPorVeiculo("veiculo-1");

        assertThat(lista).hasSize(1);
    }

    // --- gerarOrcamento ---

    @Test
    void deveGerarOrcamentoComSucesso() {
        osEntity.setStatus("EM_DIAGNOSTICO");
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(ordemServicoRepository.save(any())).thenReturn(osEntityComOrcamento("PENDENTE", "EM_DIAGNOSTICO"));
        when(insumosRepository.findByIdAndAtivoTrue("insumo-1")).thenReturn(Optional.of(insumoEntity()));

        OrdemServicoResponse resp = service.gerarOrcamento("os-1", gerarOrcamentoRequest());

        assertThat(resp.getOrcamento()).isNotNull();
        verify(ordemServicoRepository).save(argThat(e -> "PENDENTE".equals(e.getOrcamentoStatus())));
    }

    @Test
    void deveLancarConflictAoGerarOrcamentoEmOsFinalizada() {
        osEntity.setStatus("FINALIZADA");
        osEntity.setOrcamentoStatus("APROVADO");
        osEntity.setItensOrcamento(itensEntity());
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(insumosRepository.findByIdAndAtivoTrue("insumo-1")).thenReturn(Optional.of(insumoEntity()));

        assertThatThrownBy(() -> service.gerarOrcamento("os-1", gerarOrcamentoRequest()))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    // --- enviarOrcamento ---

    @Test
    void deveEnviarOrcamentoComSucesso() {
        osEntity.setStatus("EM_DIAGNOSTICO");
        osEntity.setOrcamentoStatus("PENDENTE");
        osEntity.setItensOrcamento(itensEntity());
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(ordemServicoRepository.save(any())).thenReturn(osEntityComOrcamento("ENVIADO", "AGUARDANDO_APROVACAO"));

        OrdemServicoResponse resp = service.enviarOrcamento("os-1");

        assertThat(resp.getOrcamento().getStatus()).isEqualTo("ENVIADO");
    }

    @Test
    void deveLancarConflictAoEnviarOrcamentoJaEnviado() {
        osEntity.setStatus("AGUARDANDO_APROVACAO");
        osEntity.setOrcamentoStatus("ENVIADO");
        osEntity.setItensOrcamento(itensEntity());
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));

        assertThatThrownBy(() -> service.enviarOrcamento("os-1"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    // --- aprovarOrcamento ---

    @Test
    void deveAprovarOrcamentoEManterOsEmAguardandoAprovacao() {
        osEntity.setStatus("AGUARDANDO_APROVACAO");
        osEntity.setOrcamentoStatus("ENVIADO");
        osEntity.setItensOrcamento(itensEntity());
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(ordemServicoRepository.save(any())).thenReturn(osEntityComOrcamento("APROVADO", "AGUARDANDO_APROVACAO"));

        OrdemServicoResponse resp = service.aprovarOrcamento("os-1");

        assertThat(resp.getStatus()).isEqualTo("AGUARDANDO_APROVACAO");
        assertThat(resp.getOrcamento().getStatus()).isEqualTo("APROVADO");
    }

    @Test
    void deveIniciarExecucaoComSucesso() {
        osEntity.setStatus("AGUARDANDO_APROVACAO");
        osEntity.setOrcamentoStatus("APROVADO");
        osEntity.setItensOrcamento(itensEntity());
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(ordemServicoRepository.save(any())).thenReturn(osEntityComOrcamento("APROVADO", "EM_EXECUCAO"));

        OrdemServicoResponse resp = service.iniciarExecucao("os-1");

        assertThat(resp.getStatus()).isEqualTo("EM_EXECUCAO");
    }

    @Test
    void devePreservarInsumoIdAoEnviarOrcamentoParaBaixaNaFinalizacao() {
        osEntity.setStatus("EM_DIAGNOSTICO");
        osEntity.setOrcamentoStatus("PENDENTE");
        ItemOrcamentoJpaEntity item = new ItemOrcamentoJpaEntity();
        item.setDescricao("Óleo");
        item.setQuantidade(1);
        item.setPrecoUnitario(BigDecimal.valueOf(100.0));
        item.setInsumoId("insumo-1");
        osEntity.setItensOrcamento(new java.util.ArrayList<>(List.of(item)));
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(ordemServicoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.enviarOrcamento("os-1");

        verify(ordemServicoRepository).save(argThat(e ->
                e.getItensOrcamento().size() == 1
                        && "insumo-1".equals(e.getItensOrcamento().get(0).getInsumoId())));
    }

    @Test
    void deveDarBaixaNoEstoqueDoInsumoAoFinalizar() {
        osEntity.setStatus("EM_EXECUCAO");
        osEntity.setOrcamentoStatus("APROVADO");
        ItemOrcamentoJpaEntity item = new ItemOrcamentoJpaEntity();
        item.setDescricao("Óleo de motor");
        item.setQuantidade(3);
        item.setPrecoUnitario(BigDecimal.valueOf(45.90));
        item.setInsumoId("insumo-1");
        osEntity.setItensOrcamento(new java.util.ArrayList<>(List.of(item)));

        InsumosJpaEntity insumo = insumoEntity();
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(insumosRepository.findByIdAndAtivoTrue("insumo-1")).thenReturn(Optional.of(insumo));
        when(ordemServicoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.finalizar("os-1");

        verify(insumosRepository).save(argThat(i -> i.getEstoqueAtual() == 7));
    }

    // --- entregar ---

    @Test
    void deveEntregarVeiculoDeOsFinalizada() {
        osEntity.setStatus("FINALIZADA");
        osEntity.setOrcamentoStatus("APROVADO");
        osEntity.setItensOrcamento(itensEntity());
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        OrdemServicoJpaEntity entregue = osEntityComOrcamento("APROVADO", "ENTREGUE");
        when(ordemServicoRepository.save(any())).thenReturn(entregue);

        OrdemServicoResponse resp = service.entregar("os-1");

        assertThat(resp.getStatus()).isEqualTo("ENTREGUE");
    }

    @Test
    void deveLancarConflictAoEntregarVeiculoDeOsNaoFinalizada() {
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));

        assertThatThrownBy(() -> service.entregar("os-1"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    // --- iniciarDiagnostico ---

    @Test
    void deveIniciarDiagnosticoComSucesso() {
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(ordemServicoRepository.save(any())).thenReturn(osEntityComStatus("EM_DIAGNOSTICO"));

        OrdemServicoResponse resp = service.iniciarDiagnostico("os-1");

        assertThat(resp.getStatus()).isEqualTo("EM_DIAGNOSTICO");
    }

    @Test
    void deveLancarConflictAoIniciarDiagnosticoEmStatusInvalido() {
        osEntity.setStatus("EM_DIAGNOSTICO");
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));

        assertThatThrownBy(() -> service.iniciarDiagnostico("os-1"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    // --- negarOrcamento ---

    @Test
    void deveNegarOrcamentoComSucesso() {
        osEntity.setStatus("AGUARDANDO_APROVACAO");
        osEntity.setOrcamentoStatus("ENVIADO");
        osEntity.setItensOrcamento(itensEntity());
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(ordemServicoRepository.save(any())).thenReturn(osEntityComOrcamento("REJEITADO", "EM_DIAGNOSTICO"));

        OrdemServicoResponse resp = service.negarOrcamento("os-1");

        assertThat(resp.getOrcamento().getStatus()).isEqualTo("REJEITADO");
    }

    @Test
    void deveLancarConflictAoNegarOrcamentoEmStatusInvalido() {
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));

        assertThatThrownBy(() -> service.negarOrcamento("os-1"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    // --- atualizarOrcamento ---

    @Test
    void deveAtualizarOrcamentoComSucesso() {
        osEntity.setStatus("EM_DIAGNOSTICO");
        osEntity.setOrcamentoStatus("AGUARDANDO");
        osEntity.setItensOrcamento(itensEntity());
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(insumosRepository.findByIdAndAtivoTrue("insumo-1")).thenReturn(Optional.of(insumoEntity()));
        when(ordemServicoRepository.save(any())).thenReturn(osEntityComOrcamento("PENDENTE", "EM_DIAGNOSTICO"));

        OrdemServicoResponse resp = service.atualizarOrcamento("os-1", gerarOrcamentoRequest());

        assertThat(resp.getOrcamento()).isNotNull();
        verify(ordemServicoRepository).save(any());
    }

    @Test
    void deveLancarConflictAoAtualizarOrcamentoJaAprovado() {
        osEntity.setStatus("AGUARDANDO_APROVACAO");
        osEntity.setOrcamentoStatus("APROVADO");
        osEntity.setItensOrcamento(itensEntity());
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(insumosRepository.findByIdAndAtivoTrue("insumo-1")).thenReturn(Optional.of(insumoEntity()));

        assertThatThrownBy(() -> service.atualizarOrcamento("os-1", gerarOrcamentoRequest()))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    // --- finalizar ---

    @Test
    void deveLancarConflictAoFinalizarOsEmStatusInvalido() {
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));

        assertThatThrownBy(() -> service.finalizar("os-1"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    // --- toItensComOrigem: servicos e erros ---

    @Test
    void deveGerarOrcamentoComServicoComSucesso() {
        osEntity.setStatus("EM_DIAGNOSTICO");
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(servicoRepository.findByIdAndAtivoTrue("servico-1")).thenReturn(Optional.of(servicoEntity()));
        when(ordemServicoRepository.save(any())).thenReturn(osEntityComOrcamento("PENDENTE", "EM_DIAGNOSTICO"));

        OrdemServicoResponse resp = service.gerarOrcamento("os-1", gerarOrcamentoComServicoRequest());

        assertThat(resp.getOrcamento()).isNotNull();
    }

    @Test
    void deveLancarNotFoundQuandoInsumoNaoEncontradoAoGerarOrcamento() {
        osEntity.setStatus("EM_DIAGNOSTICO");
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(insumosRepository.findByIdAndAtivoTrue("insumo-x")).thenReturn(Optional.empty());

        ItemOrcamentoRequest item = new ItemOrcamentoRequest();
        item.setInsumoId("insumo-x");
        item.setQuantidade(1);
        GerarOrcamentoRequest req = new GerarOrcamentoRequest();
        req.setInsumos(List.of(item));

        assertThatThrownBy(() -> service.gerarOrcamento("os-1", req))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void deveLancarNotFoundQuandoServicoNaoEncontradoAoGerarOrcamento() {
        osEntity.setStatus("EM_DIAGNOSTICO");
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(servicoRepository.findByIdAndAtivoTrue("servico-x")).thenReturn(Optional.empty());

        ItemServicoRequest item = new ItemServicoRequest();
        item.setServicoId("servico-x");
        item.setQuantidade(1);
        GerarOrcamentoRequest req = new GerarOrcamentoRequest();
        req.setServicos(List.of(item));

        assertThatThrownBy(() -> service.gerarOrcamento("os-1", req))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void deveLancarBadRequestQuandoOrcamentoSemItens() {
        osEntity.setStatus("EM_DIAGNOSTICO");
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));

        assertThatThrownBy(() -> service.gerarOrcamento("os-1", new GerarOrcamentoRequest()))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    // --- darBaixaInsumos: branches ---

    @Test
    void deveIgnorarItemSemInsumoIdNaFinalizacao() {
        osEntity.setStatus("EM_EXECUCAO");
        osEntity.setOrcamentoStatus("APROVADO");
        ItemOrcamentoJpaEntity item = new ItemOrcamentoJpaEntity();
        item.setDescricao("Serviço sem insumo");
        item.setQuantidade(1);
        item.setPrecoUnitario(BigDecimal.valueOf(100));
        item.setInsumoId(null);
        osEntity.setItensOrcamento(new java.util.ArrayList<>(List.of(item)));
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(ordemServicoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.finalizar("os-1");

        verify(insumosRepository, never()).save(any());
    }

    @Test
    void deveIgnorarItemQuandoInsumoNaoEncontradoNaFinalizacao() {
        osEntity.setStatus("EM_EXECUCAO");
        osEntity.setOrcamentoStatus("APROVADO");
        ItemOrcamentoJpaEntity item = new ItemOrcamentoJpaEntity();
        item.setDescricao("Óleo");
        item.setQuantidade(1);
        item.setPrecoUnitario(BigDecimal.valueOf(100));
        item.setInsumoId("insumo-inexistente");
        osEntity.setItensOrcamento(new java.util.ArrayList<>(List.of(item)));
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(insumosRepository.findByIdAndAtivoTrue("insumo-inexistente")).thenReturn(Optional.empty());
        when(ordemServicoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.finalizar("os-1");

        verify(insumosRepository, never()).save(any());
    }

    @Test
    void deveZerarEstoqueQuandoQuantidadeSuperaEstoqueNaFinalizacao() {
        osEntity.setStatus("EM_EXECUCAO");
        osEntity.setOrcamentoStatus("APROVADO");
        ItemOrcamentoJpaEntity item = new ItemOrcamentoJpaEntity();
        item.setDescricao("Óleo");
        item.setQuantidade(20);
        item.setPrecoUnitario(BigDecimal.valueOf(100));
        item.setInsumoId("insumo-1");
        osEntity.setItensOrcamento(new java.util.ArrayList<>(List.of(item)));
        InsumosJpaEntity insumo = insumoEntity();
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(insumosRepository.findByIdAndAtivoTrue("insumo-1")).thenReturn(Optional.of(insumo));
        when(ordemServicoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.finalizar("os-1");

        verify(insumosRepository).save(argThat(i -> i.getEstoqueAtual() == 0));
    }

    // --- listarMinhasOs ---

    @Test
    void deveListarMinhasOsSemFiltros() {
        configurarSecurityContext("cliente-1");
        when(ordemServicoRepository.findByClienteId("cliente-1")).thenReturn(List.of(osEntity));
        when(veiculoRepository.findById("veiculo-1")).thenReturn(Optional.of(veiculo));

        List<MinhaOrdemServicoResponse> lista = service.listarMinhasOs(null, null);

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).id()).isEqualTo("os-1");
    }

    @Test
    void deveListarMinhasOsComFiltroDeStatus() {
        configurarSecurityContext("cliente-1");
        when(ordemServicoRepository.findByClienteId("cliente-1")).thenReturn(List.of(osEntity));
        when(veiculoRepository.findById("veiculo-1")).thenReturn(Optional.of(veiculo));

        List<MinhaOrdemServicoResponse> lista = service.listarMinhasOs(OrdemServicoStatus.RECEBIDA, null);

        assertThat(lista).hasSize(1);
    }

    @Test
    void deveListarMinhasOsComFiltroDeStatusDiferenteRetornaVazio() {
        configurarSecurityContext("cliente-1");
        when(ordemServicoRepository.findByClienteId("cliente-1")).thenReturn(List.of(osEntity));

        List<MinhaOrdemServicoResponse> lista = service.listarMinhasOs(OrdemServicoStatus.FINALIZADA, null);

        assertThat(lista).isEmpty();
    }

    @Test
    void deveListarMinhasOsComFiltroDePlacaEncontrada() {
        configurarSecurityContext("cliente-1");
        when(ordemServicoRepository.findByClienteId("cliente-1")).thenReturn(List.of(osEntity));
        when(veiculoRepository.findByPlacaIgnoreCaseAndAtivoTrue("ABC1234")).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.findById("veiculo-1")).thenReturn(Optional.of(veiculo));

        List<MinhaOrdemServicoResponse> lista = service.listarMinhasOs(null, "ABC1234");

        assertThat(lista).hasSize(1);
    }

    @Test
    void deveRetornarVazioQuandoPlacaNaoEncontradaEmListarMinhasOs() {
        configurarSecurityContext("cliente-1");
        when(ordemServicoRepository.findByClienteId("cliente-1")).thenReturn(List.of(osEntity));
        when(veiculoRepository.findByPlacaIgnoreCaseAndAtivoTrue("XYZ9999")).thenReturn(Optional.empty());

        List<MinhaOrdemServicoResponse> lista = service.listarMinhasOs(null, "XYZ9999");

        assertThat(lista).isEmpty();
    }

    @Test
    void deveListarMinhasOsComVeiculoNaoEncontradoNoResponse() {
        configurarSecurityContext("cliente-1");
        when(ordemServicoRepository.findByClienteId("cliente-1")).thenReturn(List.of(osEntity));
        when(veiculoRepository.findById("veiculo-1")).thenReturn(Optional.empty());

        List<MinhaOrdemServicoResponse> lista = service.listarMinhasOs(null, null);

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).veiculo()).isNull();
    }

    // --- helpers ---

    private GerarOrcamentoRequest gerarOrcamentoRequest() {
        ItemOrcamentoRequest item = new ItemOrcamentoRequest();
        item.setInsumoId("insumo-1");
        item.setQuantidade(1);

        GerarOrcamentoRequest req = new GerarOrcamentoRequest();
        req.setInsumos(List.of(item));
        req.setObservacoes("obs");
        return req;
    }

    private InsumosJpaEntity insumoEntity() {
        InsumosJpaEntity ie = new InsumosJpaEntity();
        ie.setId("insumo-1");
        ie.setNome("Óleo de motor");
        ie.setPrecoUnitario(BigDecimal.valueOf(100.0));
        ie.setEstoqueAtual(10);
        ie.setEstoqueMinimo(2);
        ie.setUnidade("Litro");
        ie.setAtivo(true);
        return ie;
    }

    private List<ItemOrcamentoJpaEntity> itensEntity() {
        ItemOrcamentoJpaEntity ie = new ItemOrcamentoJpaEntity();
        ie.setDescricao("Troca de óleo");
        ie.setQuantidade(1);
        ie.setPrecoUnitario(BigDecimal.valueOf(100.0));
        ie.setInsumoId("insumo-1");
        return new java.util.ArrayList<>(List.of(ie));
    }

    private void configurarSecurityContext(String clienteId) {
        UsuarioPrincipal principal = new UsuarioPrincipal("id", "email", "senha", clienteId, List.of());
        Authentication auth = mock(Authentication.class);
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(principal);
        SecurityContextHolder.setContext(ctx);
    }

    private OrdemServicoJpaEntity osEntityComStatus(String osStatus) {
        OrdemServicoJpaEntity e = new OrdemServicoJpaEntity();
        e.setId("os-1");
        e.setVeiculoId("veiculo-1");
        e.setClienteId("cliente-1");
        e.setStatus(osStatus);
        return e;
    }

    private ServicoJpaEntity servicoEntity() {
        ServicoJpaEntity e = new ServicoJpaEntity();
        e.setId("servico-1");
        e.setNome("Alinhamento");
        e.setDescricao("Alinhamento e balanceamento");
        e.setPreco(BigDecimal.valueOf(150));
        e.setTempoEstimadoHoras(Duration.ofHours(1));
        e.setAtivo(true);
        return e;
    }

    private GerarOrcamentoRequest gerarOrcamentoComServicoRequest() {
        ItemServicoRequest item = new ItemServicoRequest();
        item.setServicoId("servico-1");
        item.setQuantidade(1);
        GerarOrcamentoRequest req = new GerarOrcamentoRequest();
        req.setServicos(List.of(item));
        req.setObservacoes("obs");
        return req;
    }

    private OrdemServicoJpaEntity osEntityComOrcamento(String orcStatus, String osStatus) {
        OrdemServicoJpaEntity e = new OrdemServicoJpaEntity();
        e.setId("os-1");
        e.setVeiculoId("veiculo-1");
        e.setClienteId("cliente-1");
        e.setStatus(osStatus);
        e.setOrcamentoStatus(orcStatus);
        e.setItensOrcamento(itensEntity());
        return e;
    }
}
