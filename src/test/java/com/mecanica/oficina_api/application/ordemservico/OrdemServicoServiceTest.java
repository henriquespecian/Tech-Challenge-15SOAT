package com.mecanica.oficina_api.application.ordemservico;

import com.mecanica.oficina_api.application.insumo.NotificadorEstoqueBaixo;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServicoStatus;
import com.mecanica.oficina_api.infrastructure.persistence.ClienteJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.InsumosJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.ItemOrcamentoJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.OrdemServicoJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.ServicoJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.StatusServicoJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.VeiculoJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.repository.ClienteSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.InsumosSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.OrdemServicoSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.ServicoSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.StatusServicoSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.VeiculoSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.security.UsuarioPrincipal;
import com.mecanica.oficina_api.interfaces.dto.request.CriarOrdemServicoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.GerarOrcamentoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.ItemOrcamentoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.ItemServicoRequest;
import com.mecanica.oficina_api.interfaces.dto.response.MinhaOrdemServicoResponse;
import com.mecanica.oficina_api.interfaces.dto.response.OrdemServicoResponse;
import com.mecanica.oficina_api.interfaces.dto.response.ServicoStatusResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdemServicoServiceTest {

    @Mock private OrdemServicoSpringDataRepository ordemServicoRepository;
    @Mock private VeiculoSpringDataRepository veiculoRepository;
    @Mock private ClienteSpringDataRepository clienteRepository;
    @Mock private InsumosSpringDataRepository insumosRepository;
    @Mock private ServicoSpringDataRepository servicoRepository;
    @Mock private NotificadorEstoqueBaixo notificadorEstoqueBaixo;
    @Mock private NotificadorCliente notificadorCliente;
    @Mock private StatusServicoSpringDataRepository statusServicoRepository;
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

        OrdemServicoResponse resp = service.gerarOrcamento("os-1", gerarOrcamentoComInsumo());

        assertThat(resp.getOrcamento()).isNotNull();
        verify(ordemServicoRepository).save(argThat(e -> "PENDENTE".equals(e.getOrcamentoStatus())));
    }

    @Test
    void deveLancarConflictAoGerarOrcamentoEmOsFinalizada() {
        osEntity.setStatus("FINALIZADA");
        osEntity.setOrcamentoStatus("APROVADO");
        osEntity.setItensOrcamento(itensEntityComInsumo());
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(insumosRepository.findByIdAndAtivoTrue("insumo-1")).thenReturn(Optional.of(insumoEntity()));

        assertThatThrownBy(() -> service.gerarOrcamento("os-1", gerarOrcamentoComInsumo()))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    // --- enviarOrcamento ---

    @Test
    void deveEnviarOrcamentoComSucesso() {
        osEntity.setStatus("EM_DIAGNOSTICO");
        osEntity.setOrcamentoStatus("PENDENTE");
        osEntity.setItensOrcamento(itensEntityComInsumo());
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(ordemServicoRepository.save(any())).thenReturn(osEntityComOrcamento("ENVIADO", "AGUARDANDO_APROVACAO"));

        OrdemServicoResponse resp = service.enviarOrcamento("os-1");

        assertThat(resp.getOrcamento().getStatus()).isEqualTo("ENVIADO");
        verify(notificadorCliente).notificar(argThat(n ->
                n.tipo() == TipoNotificacaoCliente.ENVIO_ORCAMENTO
                        && "os-1".equals(n.ordemServicoId())
                        && "cliente-1".equals(n.clienteId())
                        && "veiculo-1".equals(n.veiculoId())));
    }

    @Test
    void deveAtualizarOrcamentoERetornarStatusPendente() {
        osEntity.setStatus("AGUARDANDO_APROVACAO");
        osEntity.setOrcamentoStatus("AGUARDANDO");
        osEntity.setItensOrcamento(itensEntityComInsumo());

        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(ordemServicoRepository.save(any())).thenReturn(osEntityComOrcamento("PENDENTE", "AGUARDANDO_APROVACAO"));
        when(insumosRepository.findByIdAndAtivoTrue("insumo-1")).thenReturn(Optional.of(insumoEntity()));

        OrdemServicoResponse resp = service.atualizarOrcamento("os-1", gerarOrcamentoComInsumo());

        assertThat(resp.getStatus()).isEqualTo("AGUARDANDO_APROVACAO");
        assertThat(resp.getOrcamento().getStatus()).isEqualTo("PENDENTE");
    }

    @Test
    void deveLancarConflictAoEnviarOrcamentoJaEnviado() {
        osEntity.setStatus("AGUARDANDO_APROVACAO");
        osEntity.setOrcamentoStatus("ENVIADO");
        osEntity.setItensOrcamento(itensEntityComInsumo());
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));

        assertThatThrownBy(() -> service.enviarOrcamento("os-1"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));

        verify(notificadorCliente, never()).notificar(any());
    }

    // --- aprovarOrcamento ---

    @Test
    void deveAprovarOrcamentoEManterOsEmAguardandoAprovacao() {
        osEntity.setStatus("AGUARDANDO_APROVACAO");
        osEntity.setOrcamentoStatus("ENVIADO");
        osEntity.setItensOrcamento(itensEntityComInsumo());
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
        osEntity.setItensOrcamento(itensEntityComServico());
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(ordemServicoRepository.save(any())).thenReturn(osEntityComOrcamento("APROVADO", "EM_EXECUCAO"));

        OrdemServicoResponse resp = service.iniciarExecucao("os-1");

        assertThat(resp.getStatus()).isEqualTo("EM_EXECUCAO");
        assertThat(resp.getOrcamento().getStatus()).isEqualTo("APROVADO");
        verify(statusServicoRepository).saveAll(any());
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
        verify(notificadorCliente).notificar(argThat(n ->
                n.tipo() == TipoNotificacaoCliente.FINALIZACAO_OS
                        && "os-1".equals(n.ordemServicoId())
                        && "cliente-1".equals(n.clienteId())
                        && "veiculo-1".equals(n.veiculoId())));
    }

    @Test
    void deveListarServicosDaOs() {
        StatusServicoJpaEntity servico = statusServicoEntity("status-1", "AGUARDANDO", "os-1", "servico-1");
        when(statusServicoRepository.findByOrdemServicoId("os-1")).thenReturn(List.of(servico));

        List<ServicoStatusResponse> resposta = service.listarServicos("os-1");

        assertThat(resposta).hasSize(1);
        assertThat(resposta.getFirst().getStatus()).isEqualTo("AGUARDANDO");
        assertThat(resposta.getFirst().getServicoId()).isEqualTo("servico-1");
    }

    @Test
    void deveIniciarServicoComSucesso() {
        StatusServicoJpaEntity servico = statusServicoEntity("status-1", "AGUARDANDO", "os-1", "servico-1");
        when(statusServicoRepository.findByIdAndStatus("status-1", "AGUARDANDO")).thenReturn(Optional.of(servico));
        when(statusServicoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ServicoStatusResponse resposta = service.iniciarServico("status-1");

        assertThat(resposta.getStatus()).isEqualTo("INICIADO");
        assertThat(resposta.getDataInicio()).isNotNull();
    }

    @Test
    void deveFinalizarServicoComSucesso() {
        StatusServicoJpaEntity servico = statusServicoEntity("status-1", "INICIADO", "os-1", "servico-1");
        servico.setDataInicio(LocalDateTime.now().minusHours(1));
        when(statusServicoRepository.findByIdAndStatus("status-1", "INICIADO")).thenReturn(Optional.of(servico));
        when(statusServicoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ServicoStatusResponse resposta = service.finalizarServico("status-1");

        assertThat(resposta.getStatus()).isEqualTo("FINALIZADO");
    }

    @Test
    void deveFinalizarOsQuandoTodosServicosEstaoFinalizados() {
        osEntity.setStatus("EM_EXECUCAO");
        osEntity.setOrcamentoStatus("APROVADO");
        osEntity.setItensOrcamento(itensEntityComInsumo());

        OrdemServicoJpaEntity osFinalizada = osEntityComOrcamento("APROVADO", "FINALIZADA");
        osFinalizada.setItensOrcamento(itensEntityComInsumo());

        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(ordemServicoRepository.save(any())).thenReturn(osFinalizada);
        when(insumosRepository.findByIdAndAtivoTrue("insumo-1")).thenReturn(Optional.of(insumoEntity()));
        when(statusServicoRepository.findByOrdemServicoId("os-1"))
            .thenReturn(List.of(statusServicoEntity("status-1", "FINALIZADO", "os-1", "servico-1")));

        OrdemServicoResponse resposta = service.finalizar("os-1");

        assertThat(resposta.getStatus()).isEqualTo("FINALIZADA");
        verify(notificadorEstoqueBaixo, never()).notificar(any());
    }

    @Test
    void deveLancarBadRequestAoFinalizarOsComServicosPendentes() {
        osEntity.setStatus("EM_EXECUCAO");
        osEntity.setOrcamentoStatus("APROVADO");
        osEntity.setItensOrcamento(itensEntityComInsumo());

        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(statusServicoRepository.findByOrdemServicoId("os-1"))
            .thenReturn(List.of(statusServicoEntity("status-1", "INICIADO", "os-1", "servico-1")));

        assertThatThrownBy(() -> service.finalizar("os-1"))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void deveNotificarEstoqueBaixoAoFinalizarOsQuandoCruzarEstoqueMinimo() {
        osEntity.setStatus("EM_EXECUCAO");
        osEntity.setOrcamentoStatus("APROVADO");
        osEntity.setItensOrcamento(itensEntityComInsumo(9));

        OrdemServicoJpaEntity osFinalizada = osEntityComOrcamento("APROVADO", "FINALIZADA");
        osFinalizada.setItensOrcamento(itensEntityComInsumo(9));

        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(ordemServicoRepository.save(any())).thenReturn(osFinalizada);
        when(insumosRepository.findByIdAndAtivoTrue("insumo-1")).thenReturn(Optional.of(insumoEntity()));
        when(statusServicoRepository.findByOrdemServicoId("os-1"))
            .thenReturn(List.of(statusServicoEntity("status-1", "FINALIZADO", "os-1", "servico-1")));

        OrdemServicoResponse resposta = service.finalizar("os-1");

        assertThat(resposta.getStatus()).isEqualTo("FINALIZADA");
        verify(notificadorEstoqueBaixo).notificar(argThat(alerta ->
            alerta.insumoId().equals("insumo-1")
                && alerta.estoqueAnterior() == 10
                && alerta.estoqueAtual() == 1
                && alerta.estoqueMinimo() == 2
                && alerta.referenciaOrigem().equals("os-1")
        ));
    }

    @Test
    void deveEntregarVeiculoDeOsFinalizada() {
        osEntity.setStatus("FINALIZADA");
        osEntity.setOrcamentoStatus("APROVADO");
        osEntity.setItensOrcamento(itensEntityComInsumo());
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
        osEntity.setItensOrcamento(itensEntityComInsumo());
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
        osEntity.setItensOrcamento(itensEntityComInsumo());
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(insumosRepository.findByIdAndAtivoTrue("insumo-1")).thenReturn(Optional.of(insumoEntity()));
        when(ordemServicoRepository.save(any())).thenReturn(osEntityComOrcamento("PENDENTE", "EM_DIAGNOSTICO"));

        OrdemServicoResponse resp = service.atualizarOrcamento("os-1", gerarOrcamentoComInsumo());

        assertThat(resp.getOrcamento()).isNotNull();
        verify(ordemServicoRepository).save(any());
    }

    @Test
    void deveLancarConflictAoAtualizarOrcamentoJaAprovado() {
        osEntity.setStatus("AGUARDANDO_APROVACAO");
        osEntity.setOrcamentoStatus("APROVADO");
        osEntity.setItensOrcamento(itensEntityComInsumo());
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(insumosRepository.findByIdAndAtivoTrue("insumo-1")).thenReturn(Optional.of(insumoEntity()));

        assertThatThrownBy(() -> service.atualizarOrcamento("os-1", gerarOrcamentoComInsumo()))
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

    private GerarOrcamentoRequest gerarOrcamentoComInsumo() {
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
        ie.setNome("Oleo de motor");
        ie.setPrecoUnitario(BigDecimal.valueOf(100.0));
        ie.setEstoqueAtual(10);
        ie.setEstoqueMinimo(2);
        ie.setUnidade("Litro");
        ie.setAtivo(true);
        return ie;
    }

    private List<ItemOrcamentoJpaEntity> itensEntityComInsumo() {
        return itensEntityComInsumo(1);
    }

    private List<ItemOrcamentoJpaEntity> itensEntityComInsumo(int quantidade) {
        ItemOrcamentoJpaEntity ie = new ItemOrcamentoJpaEntity();
        ie.setDescricao("Oleo de motor");
        ie.setQuantidade(quantidade);
        ie.setPrecoUnitario(BigDecimal.valueOf(100.0));
        ie.setInsumoId("insumo-1");
        return new ArrayList<>(List.of(ie));
    }

    private List<ItemOrcamentoJpaEntity> itensEntityComServico() {
        ItemOrcamentoJpaEntity ie = new ItemOrcamentoJpaEntity();
        ie.setDescricao("Troca de oleo");
        ie.setQuantidade(1);
        ie.setPrecoUnitario(BigDecimal.valueOf(100.0));
        ie.setServicoId("servico-1");
        return new ArrayList<>(List.of(ie));
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

    private GerarOrcamentoRequest gerarOrcamentoComInsumoRequest() {
        ItemServicoRequest item = new ItemServicoRequest();
        item.setServicoId("insumo-1");
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
        e.setItensOrcamento(itensEntityComInsumo());
        return e;
    }

    private StatusServicoJpaEntity statusServicoEntity(String id, String status, String ordemServicoId, String servicoId) {
        StatusServicoJpaEntity entity = new StatusServicoJpaEntity();
        entity.setId(id);
        entity.setStatus(status);
        entity.setOrdemServicoId(ordemServicoId);
        entity.setServicoId(servicoId);
        return entity;
    }
}
