package com.mecanica.oficina_api.application.ordemservico;

import com.mecanica.oficina_api.application.insumo.NotificadorEstoqueBaixo;
import com.mecanica.oficina_api.infrastructure.persistence.ClienteJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.InsumosJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.ItemOrcamentoJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.OrdemServicoJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.StatusServicoJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.VeiculoJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.repository.ClienteSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.InsumosSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.OrdemServicoSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.ServicoSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.StatusServicoSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.VeiculoSpringDataRepository;
import com.mecanica.oficina_api.interfaces.dto.request.CriarOrdemServicoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.GerarOrcamentoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.ItemOrcamentoRequest;
import com.mecanica.oficina_api.interfaces.dto.response.OrdemServicoResponse;
import com.mecanica.oficina_api.interfaces.dto.response.ServicoStatusResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
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
    @Mock private StatusServicoSpringDataRepository statusServicoRepository;
    @Mock private NotificadorEstoqueBaixo notificadorEstoqueBaixo;
    @InjectMocks private OrdemServicoService service;

    private VeiculoJpaEntity veiculo;
    private ClienteJpaEntity cliente;
    private OrdemServicoJpaEntity osEntity;

    @BeforeEach
    void setUp() {
        veiculo = new VeiculoJpaEntity();
        veiculo.setId("veiculo-1");
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
        verify(ordemServicoRepository).save(any());
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

    @Test
    void deveListarPorVeiculoComSucesso() {
        when(ordemServicoRepository.findByVeiculoId("veiculo-1")).thenReturn(List.of(osEntity));

        List<OrdemServicoResponse> lista = service.listarPorVeiculo("veiculo-1");

        assertThat(lista).hasSize(1);
    }

    @Test
    void deveGerarOrcamentoComSucesso() {
        osEntity.setStatus("EM_DIAGNOSTICO");
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(ordemServicoRepository.save(any())).thenReturn(osEntityComOrcamento("PENDENTE", "EM_DIAGNOSTICO"));
        when(insumosRepository.findByIdAndAtivoTrue("insumo-1")).thenReturn(Optional.of(insumoEntity()));

        OrdemServicoResponse resp = service.gerarOrcamento("os-1", gerarOrcamentoComInsumo());

        assertThat(resp.getOrcamento()).isNotNull();
        assertThat(resp.getOrcamento().getStatus()).isEqualTo("PENDENTE");
        verify(ordemServicoRepository).save(any());
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

    @Test
    void deveEnviarOrcamentoComSucesso() {
        osEntity.setStatus("EM_DIAGNOSTICO");
        osEntity.setOrcamentoStatus("PENDENTE");
        osEntity.setItensOrcamento(itensEntityComInsumo());
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(ordemServicoRepository.save(any())).thenReturn(osEntityComOrcamento("ENVIADO", "AGUARDANDO_APROVACAO"));

        OrdemServicoResponse resp = service.enviarOrcamento("os-1");

        assertThat(resp.getOrcamento().getStatus()).isEqualTo("ENVIADO");
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
    }

    @Test
    void deveAprovarOrcamentoETransicionarOsParaEmExecucao() {
        osEntity.setStatus("AGUARDANDO_APROVACAO");
        osEntity.setOrcamentoStatus("ENVIADO");
        osEntity.setItensOrcamento(itensEntityComServico());

        OrdemServicoJpaEntity osAprovada = osEntityComOrcamento("APROVADO", "EM_EXECUCAO");
        osAprovada.setItensOrcamento(itensEntityComServico());

        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity), Optional.of(osAprovada));
        when(ordemServicoRepository.save(any())).thenReturn(osAprovada);
        when(statusServicoRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        OrdemServicoResponse resp = service.aprovarOrcamento("os-1");

        assertThat(resp.getStatus()).isEqualTo("EM_EXECUCAO");
        assertThat(resp.getOrcamento().getStatus()).isEqualTo("APROVADO");
        verify(statusServicoRepository).saveAll(any());
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
        assertThat(resposta.getDataFim()).isNotNull();
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
        when(ordemServicoRepository.save(any())).thenReturn(osEntityComOrcamento("APROVADO", "FINALIZADA"));
        when(insumosRepository.findByIdAndAtivoTrue("insumo-1")).thenReturn(Optional.of(insumoEntity()));
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
