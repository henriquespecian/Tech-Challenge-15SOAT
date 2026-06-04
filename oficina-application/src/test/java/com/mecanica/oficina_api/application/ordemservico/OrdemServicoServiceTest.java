package com.mecanica.oficina_api.application.ordemservico;

import com.mecanica.oficina_api.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_api.application.insumo.gateway.InsumosGateway;
import com.mecanica.oficina_api.application.insumo.gateway.NotificarEstoqueBaixoGateway;
import com.mecanica.oficina_api.application.ordemservico.gateway.NotificadorCliente;
import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
import com.mecanica.oficina_api.application.ordemservico.input.GerarOrcamentoInput;
import com.mecanica.oficina_api.application.ordemservico.output.MinhaOrdemServicoOutput;
import com.mecanica.oficina_api.application.servico.gateway.ServicoGateway;
import com.mecanica.oficina_api.application.servico.gateway.StatusServicoGateway;
import com.mecanica.oficina_api.application.veiculo.gateway.VeiculoGateway;
import com.mecanica.oficina_api.domain.insumo.Insumos;
import com.mecanica.oficina_api.domain.ordemservico.ItemOrcamento;
import com.mecanica.oficina_api.domain.ordemservico.Orcamento;
import com.mecanica.oficina_api.domain.ordemservico.OrcamentoStatus;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServicoStatus;
import com.mecanica.oficina_api.domain.ordemservico.ServicoStatus;
import com.mecanica.oficina_api.domain.ordemservico.StatusServico;
import com.mecanica.oficina_api.domain.servico.Servico;
import com.mecanica.oficina_api.domain.veiculo.Veiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdemServicoServiceTest {

    private OrdemServicoGateway ordemServicoGateway;
    private VeiculoGateway veiculoGateway;
    private ClienteGateway clienteGateway;
    private InsumosGateway insumosGateway;
    private ServicoGateway servicoGateway;
    private NotificarEstoqueBaixoGateway notificadorEstoqueBaixo;
    private NotificadorCliente notificadorCliente;
    private StatusServicoGateway statusServicoGateway;

    private OrdemServicoService service;

    @BeforeEach
    void setUp() {
        ordemServicoGateway = mock(OrdemServicoGateway.class);
        veiculoGateway = mock(VeiculoGateway.class);
        clienteGateway = mock(ClienteGateway.class);
        insumosGateway = mock(InsumosGateway.class);
        servicoGateway = mock(ServicoGateway.class);
        notificadorEstoqueBaixo = mock(NotificarEstoqueBaixoGateway.class);
        notificadorCliente = mock(NotificadorCliente.class);
        statusServicoGateway = mock(StatusServicoGateway.class);

        service = new OrdemServicoService(ordemServicoGateway, veiculoGateway, clienteGateway,
                insumosGateway, servicoGateway, notificadorEstoqueBaixo, notificadorCliente, statusServicoGateway);
    }

    // --- criar ---

    @Test
    void deveCriarOrdemServicoComSucesso() {
        when(veiculoGateway.buscar("veiculo-1")).thenReturn(Optional.of(veiculo()));
        when(clienteGateway.findByIdAtivo("cliente-1")).thenReturn(Optional.of(mock(com.mecanica.oficina_api.domain.cliente.Cliente.class)));
        OrdemServico criada = os(OrdemServicoStatus.RECEBIDA, null);
        when(ordemServicoGateway.cadastrar("veiculo-1", "cliente-1")).thenReturn(criada);

        OrdemServico resp = service.criar("veiculo-1", "cliente-1");

        assertThat(resp.getStatus()).isEqualTo(OrdemServicoStatus.RECEBIDA);
        verify(ordemServicoGateway).cadastrar("veiculo-1", "cliente-1");
    }

    @Test
    void deveLancarQuandoVeiculoNaoEncontradoAoCriar() {
        when(veiculoGateway.buscar("veiculo-x")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.criar("veiculo-x", "cliente-1"))
                .isInstanceOf(IllegalArgumentException.class);
        verify(ordemServicoGateway, never()).cadastrar(any(), any());
    }

    @Test
    void deveLancarQuandoClienteNaoEncontradoAoCriar() {
        when(veiculoGateway.buscar("veiculo-1")).thenReturn(Optional.of(veiculo()));
        when(clienteGateway.findByIdAtivo("cliente-x")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.criar("veiculo-1", "cliente-x"))
                .isInstanceOf(IllegalArgumentException.class);
        verify(ordemServicoGateway, never()).cadastrar(any(), any());
    }

    // --- buscarPorId ---

    @Test
    void deveBuscarPorIdComSucesso() {
        when(ordemServicoGateway.buscar("os-1")).thenReturn(Optional.of(os(OrdemServicoStatus.RECEBIDA, null)));

        OrdemServico resp = service.buscarPorId("os-1");

        assertThat(resp.getStatus()).isEqualTo(OrdemServicoStatus.RECEBIDA);
    }

    @Test
    void deveLancarQuandoOsNaoEncontrada() {
        when(ordemServicoGateway.buscar("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId("inexistente"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // --- listar / listarPorVeiculo ---

    @Test
    void deveListarTodasSemFiltro() {
        when(ordemServicoGateway.listar(null)).thenReturn(List.of(os(OrdemServicoStatus.RECEBIDA, null)));

        assertThat(service.listar(null)).hasSize(1);
        verify(ordemServicoGateway).listar(null);
    }

    @Test
    void deveListarComFiltroDeStatus() {
        when(ordemServicoGateway.listar(OrdemServicoStatus.RECEBIDA))
                .thenReturn(List.of(os(OrdemServicoStatus.RECEBIDA, null)));

        assertThat(service.listar(OrdemServicoStatus.RECEBIDA)).hasSize(1);
    }

    @Test
    void deveListarPorVeiculo() {
        when(ordemServicoGateway.listarPorVeiculo("veiculo-1"))
                .thenReturn(List.of(os(OrdemServicoStatus.RECEBIDA, null)));

        assertThat(service.listarPorVeiculo("veiculo-1")).hasSize(1);
    }

    // --- listarMinhasOs ---

    @Test
    void deveListarMinhasOsSemFiltros() {
        when(ordemServicoGateway.buscarPorCliente("cliente-1")).thenReturn(List.of(minhaOutput("RECEBIDA", "ABC1234")));

        assertThat(service.listarMinhasOs("cliente-1", null, null)).hasSize(1);
    }

    @Test
    void deveFiltrarMinhasOsPorStatus() {
        when(ordemServicoGateway.buscarPorCliente("cliente-1")).thenReturn(List.of(minhaOutput("RECEBIDA", "ABC1234")));

        assertThat(service.listarMinhasOs("cliente-1", OrdemServicoStatus.RECEBIDA, null)).hasSize(1);
        assertThat(service.listarMinhasOs("cliente-1", OrdemServicoStatus.FINALIZADA, null)).isEmpty();
    }

    @Test
    void deveFiltrarMinhasOsPorPlaca() {
        when(ordemServicoGateway.buscarPorCliente("cliente-1")).thenReturn(List.of(minhaOutput("RECEBIDA", "ABC1234")));

        assertThat(service.listarMinhasOs("cliente-1", null, "ABC1234")).hasSize(1);
        assertThat(service.listarMinhasOs("cliente-1", null, "XYZ9999")).isEmpty();
    }

    // --- iniciarDiagnostico ---

    @Test
    void deveIniciarDiagnosticoComSucesso() {
        when(ordemServicoGateway.buscar("os-1")).thenReturn(Optional.of(os(OrdemServicoStatus.RECEBIDA, null)));
        when(ordemServicoGateway.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        OrdemServico resp = service.iniciarDiagnostico("os-1");

        assertThat(resp.getStatus()).isEqualTo(OrdemServicoStatus.EM_DIAGNOSTICO);
        verify(ordemServicoGateway).atualizar(any());
    }

    @Test
    void deveLancarConflitoAoIniciarDiagnosticoEmStatusInvalido() {
        when(ordemServicoGateway.buscar("os-1")).thenReturn(Optional.of(os(OrdemServicoStatus.EM_DIAGNOSTICO, null)));

        assertThatThrownBy(() -> service.iniciarDiagnostico("os-1"))
                .isInstanceOf(IllegalStateException.class);
        verify(ordemServicoGateway, never()).atualizar(any());
    }

    // --- gerarOrcamento ---

    @Test
    void deveGerarOrcamentoComInsumoComSucesso() {
        when(ordemServicoGateway.buscar("os-1")).thenReturn(Optional.of(os(OrdemServicoStatus.EM_DIAGNOSTICO, null)));
        when(insumosGateway.buscar("insumo-1")).thenReturn(Optional.of(insumo(10, 2)));
        when(ordemServicoGateway.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        OrdemServico resp = service.gerarOrcamento("os-1", inputComInsumo());

        assertThat(resp.getOrcamento()).isNotNull();
        assertThat(resp.getOrcamento().getStatus()).isEqualTo(OrcamentoStatus.PENDENTE);
    }

    @Test
    void deveGerarOrcamentoComServicoComSucesso() {
        when(ordemServicoGateway.buscar("os-1")).thenReturn(Optional.of(os(OrdemServicoStatus.EM_DIAGNOSTICO, null)));
        when(servicoGateway.buscar("servico-1")).thenReturn(Optional.of(servico()));
        when(ordemServicoGateway.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        OrdemServico resp = service.gerarOrcamento("os-1", inputComServico());

        assertThat(resp.getOrcamento()).isNotNull();
    }

    @Test
    void deveLancarQuandoInsumoNaoEncontradoAoGerarOrcamento() {
        when(ordemServicoGateway.buscar("os-1")).thenReturn(Optional.of(os(OrdemServicoStatus.EM_DIAGNOSTICO, null)));
        when(insumosGateway.buscar("insumo-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.gerarOrcamento("os-1", inputComInsumo()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveLancarQuandoServicoNaoEncontradoAoGerarOrcamento() {
        when(ordemServicoGateway.buscar("os-1")).thenReturn(Optional.of(os(OrdemServicoStatus.EM_DIAGNOSTICO, null)));
        when(servicoGateway.buscar("servico-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.gerarOrcamento("os-1", inputComServico()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveLancarQuandoOrcamentoSemItens() {
        when(ordemServicoGateway.buscar("os-1")).thenReturn(Optional.of(os(OrdemServicoStatus.EM_DIAGNOSTICO, null)));

        assertThatThrownBy(() -> service.gerarOrcamento("os-1", new GerarOrcamentoInput(List.of(), List.of(), "obs")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveLancarConflitoAoGerarOrcamentoEmOsFinalizada() {
        when(ordemServicoGateway.buscar("os-1"))
                .thenReturn(Optional.of(os(OrdemServicoStatus.FINALIZADA, orcamento(OrcamentoStatus.APROVADO))));
        when(insumosGateway.buscar("insumo-1")).thenReturn(Optional.of(insumo(10, 2)));

        assertThatThrownBy(() -> service.gerarOrcamento("os-1", inputComInsumo()))
                .isInstanceOf(IllegalStateException.class);
    }

    // --- atualizarOrcamento ---

    @Test
    void deveAtualizarOrcamentoComSucesso() {
        when(ordemServicoGateway.buscar("os-1"))
                .thenReturn(Optional.of(os(OrdemServicoStatus.AGUARDANDO_APROVACAO, orcamento(OrcamentoStatus.AGUARDANDO))));
        when(insumosGateway.buscar("insumo-1")).thenReturn(Optional.of(insumo(10, 2)));
        when(ordemServicoGateway.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        OrdemServico resp = service.atualizarOrcamento("os-1", inputComInsumo());

        assertThat(resp.getOrcamento().getStatus()).isEqualTo(OrcamentoStatus.PENDENTE);
    }

    @Test
    void deveLancarConflitoAoAtualizarOrcamentoJaAprovado() {
        when(ordemServicoGateway.buscar("os-1"))
                .thenReturn(Optional.of(os(OrdemServicoStatus.AGUARDANDO_APROVACAO, orcamento(OrcamentoStatus.APROVADO))));
        when(insumosGateway.buscar("insumo-1")).thenReturn(Optional.of(insumo(10, 2)));

        assertThatThrownBy(() -> service.atualizarOrcamento("os-1", inputComInsumo()))
                .isInstanceOf(IllegalStateException.class);
    }

    // --- enviarOrcamento ---

    @Test
    void deveEnviarOrcamentoENotificarCliente() {
        when(ordemServicoGateway.buscar("os-1"))
                .thenReturn(Optional.of(os(OrdemServicoStatus.EM_DIAGNOSTICO, orcamento(OrcamentoStatus.PENDENTE))));
        when(ordemServicoGateway.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        OrdemServico resp = service.enviarOrcamento("os-1");

        assertThat(resp.getStatus()).isEqualTo(OrdemServicoStatus.AGUARDANDO_APROVACAO);
        assertThat(resp.getOrcamento().getStatus()).isEqualTo(OrcamentoStatus.ENVIADO);
        verify(notificadorCliente).notificar(any());
    }

    @Test
    void deveLancarConflitoAoEnviarOrcamentoJaEnviado() {
        when(ordemServicoGateway.buscar("os-1"))
                .thenReturn(Optional.of(os(OrdemServicoStatus.AGUARDANDO_APROVACAO, orcamento(OrcamentoStatus.ENVIADO))));

        assertThatThrownBy(() -> service.enviarOrcamento("os-1"))
                .isInstanceOf(IllegalStateException.class);
        verify(notificadorCliente, never()).notificar(any());
    }

    // --- aprovarOrcamento ---

    @Test
    void deveAprovarOrcamentoEDarBaixaNoInsumo() {
        when(ordemServicoGateway.buscar("os-1"))
                .thenReturn(Optional.of(os(OrdemServicoStatus.AGUARDANDO_APROVACAO, orcamento(OrcamentoStatus.ENVIADO))));
        when(insumosGateway.buscar("insumo-1")).thenReturn(Optional.of(insumo(10, 2)));
        when(insumosGateway.alterar(any(), any())).thenAnswer(inv -> inv.getArgument(1));
        when(ordemServicoGateway.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        OrdemServico resp = service.aprovarOrcamento("os-1");

        assertThat(resp.getOrcamento().getStatus()).isEqualTo(OrcamentoStatus.APROVADO);
        verify(insumosGateway).alterar(eq("insumo-1"), any());
    }

    @Test
    void deveNotificarEstoqueBaixoAoAprovarQuandoCruzaMinimo() {
        when(ordemServicoGateway.buscar("os-1"))
                .thenReturn(Optional.of(os(OrdemServicoStatus.AGUARDANDO_APROVACAO, orcamento(OrcamentoStatus.ENVIADO))));
        when(insumosGateway.buscar("insumo-1")).thenReturn(Optional.of(insumo(3, 2)));
        when(insumosGateway.alterar(any(), any())).thenAnswer(inv -> inv.getArgument(1));
        when(ordemServicoGateway.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        service.aprovarOrcamento("os-1");

        verify(notificadorEstoqueBaixo).notificar(org.mockito.ArgumentMatchers.argThat(a ->
                a.insumoId().equals("insumo-1") && a.estoqueAnterior() == 3 && a.estoqueAtual() == 2));
    }

    // --- iniciarExecucao ---

    @Test
    void deveIniciarExecucaoECriarStatusDosServicos() {
        when(ordemServicoGateway.buscar("os-1"))
                .thenReturn(Optional.of(os(OrdemServicoStatus.AGUARDANDO_APROVACAO, orcamentoComServico())));
        when(ordemServicoGateway.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        OrdemServico resp = service.iniciarExecucao("os-1");

        assertThat(resp.getStatus()).isEqualTo(OrdemServicoStatus.EM_EXECUCAO);
        verify(statusServicoGateway).salvarLista(any());
    }

    // --- finalizar ---

    @Test
    void deveFinalizarQuandoServicosFinalizados() {
        when(ordemServicoGateway.buscar("os-1"))
                .thenReturn(Optional.of(os(OrdemServicoStatus.EM_EXECUCAO, orcamento(OrcamentoStatus.APROVADO))));
        when(statusServicoGateway.listarServicosPorOS("os-1"))
                .thenReturn(List.of(statusServico(ServicoStatus.FINALIZADO)));
        when(ordemServicoGateway.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        OrdemServico resp = service.finalizar("os-1");

        assertThat(resp.getStatus()).isEqualTo(OrdemServicoStatus.FINALIZADA);
        verify(notificadorCliente).notificar(any());
    }

    @Test
    void deveLancarConflitoAoFinalizarComServicosPendentes() {
        when(ordemServicoGateway.buscar("os-1"))
                .thenReturn(Optional.of(os(OrdemServicoStatus.EM_EXECUCAO, orcamento(OrcamentoStatus.APROVADO))));
        when(statusServicoGateway.listarServicosPorOS("os-1"))
                .thenReturn(List.of(statusServico(ServicoStatus.INICIADO)));

        assertThatThrownBy(() -> service.finalizar("os-1"))
                .isInstanceOf(IllegalStateException.class);
        verify(ordemServicoGateway, never()).atualizar(any());
    }

    @Test
    void deveLancarConflitoAoFinalizarEmStatusInvalido() {
        when(ordemServicoGateway.buscar("os-1")).thenReturn(Optional.of(os(OrdemServicoStatus.RECEBIDA, null)));

        assertThatThrownBy(() -> service.finalizar("os-1"))
                .isInstanceOf(IllegalStateException.class);
    }

    // --- negar / entregar ---

    @Test
    void deveNegarOrcamentoComSucesso() {
        when(ordemServicoGateway.buscar("os-1"))
                .thenReturn(Optional.of(os(OrdemServicoStatus.AGUARDANDO_APROVACAO, orcamento(OrcamentoStatus.ENVIADO))));
        when(ordemServicoGateway.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        OrdemServico resp = service.negarOrcamento("os-1");

        assertThat(resp.getOrcamento().getStatus()).isEqualTo(OrcamentoStatus.NEGADO);
    }

    @Test
    void deveEntregarVeiculoDeOsFinalizada() {
        when(ordemServicoGateway.buscar("os-1"))
                .thenReturn(Optional.of(os(OrdemServicoStatus.FINALIZADA, orcamento(OrcamentoStatus.APROVADO))));
        when(ordemServicoGateway.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        OrdemServico resp = service.entregar("os-1");

        assertThat(resp.getStatus()).isEqualTo(OrdemServicoStatus.ENTREGUE);
    }

    @Test
    void deveLancarConflitoAoEntregarOsNaoFinalizada() {
        when(ordemServicoGateway.buscar("os-1")).thenReturn(Optional.of(os(OrdemServicoStatus.RECEBIDA, null)));

        assertThatThrownBy(() -> service.entregar("os-1"))
                .isInstanceOf(IllegalStateException.class);
    }

    // --- serviços individuais ---

    @Test
    void deveListarServicosDaOs() {
        when(statusServicoGateway.listarServicosPorOS("os-1"))
                .thenReturn(List.of(statusServico(ServicoStatus.AGUARDANDO)));

        assertThat(service.listarServicos("os-1")).hasSize(1);
    }

    @Test
    void deveIniciarServicoComSucesso() {
        when(statusServicoGateway.buscarPorIdEStatus("status-1", ServicoStatus.AGUARDANDO))
                .thenReturn(Optional.of(statusServico(ServicoStatus.AGUARDANDO)));
        when(statusServicoGateway.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        StatusServico resp = service.iniciarServico("status-1");

        assertThat(resp.getStatus()).isEqualTo(ServicoStatus.INICIADO);
    }

    @Test
    void deveLancarQuandoServicoNaoEncontradoAoIniciar() {
        when(statusServicoGateway.buscarPorIdEStatus("status-x", ServicoStatus.AGUARDANDO))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.iniciarServico("status-x"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveFinalizarServicoComSucesso() {
        StatusServico iniciado = statusServico(ServicoStatus.INICIADO);
        when(statusServicoGateway.buscarPorIdEStatus("status-1", ServicoStatus.INICIADO))
                .thenReturn(Optional.of(iniciado));
        when(statusServicoGateway.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        StatusServico resp = service.finalizarServico("status-1");

        assertThat(resp.getStatus()).isEqualTo(ServicoStatus.FINALIZADO);
    }

    // --- helpers ---

    private Veiculo veiculo() {
        return Veiculo.reconstituir("veiculo-1", "cliente-1", "ABC1234", "Toyota", "Corolla", 2020, "Branco", true);
    }

    private Insumos insumo(int estoqueAtual, int estoqueMinimo) {
        return Insumos.reconstituir("insumo-1", "Oleo de motor", BigDecimal.valueOf(100.0),
                estoqueAtual, estoqueMinimo, "Litro");
    }

    private Servico servico() {
        return Servico.reconstituir("servico-1", "Alinhamento", "Alinhamento e balanceamento",
                BigDecimal.valueOf(150), Duration.ofHours(1), true);
    }

    private StatusServico statusServico(ServicoStatus status) {
        return StatusServico.recriar("status-1", status, "os-1", "servico-1", null, null);
    }

    private OrdemServico os(OrdemServicoStatus status, Orcamento orcamento) {
        return OrdemServico.reconstituir("os-1", "veiculo-1", "cliente-1", status, orcamento, null, null);
    }

    private Orcamento orcamento(OrcamentoStatus status) {
        ItemOrcamento item = new ItemOrcamento("insumo-1", null, "Oleo de motor", 1, BigDecimal.valueOf(100.0));
        return Orcamento.reconstituir(List.of(item), status, "obs", null);
    }

    private Orcamento orcamentoComServico() {
        ItemOrcamento item = new ItemOrcamento(null, "servico-1", "Alinhamento", 1, BigDecimal.valueOf(150));
        return Orcamento.reconstituir(List.of(item), OrcamentoStatus.APROVADO, "obs", null);
    }

    private GerarOrcamentoInput inputComInsumo() {
        return new GerarOrcamentoInput(
                List.of(new GerarOrcamentoInput.ItemInsumoInput("insumo-1", 1)), List.of(), "obs");
    }

    private GerarOrcamentoInput inputComServico() {
        return new GerarOrcamentoInput(
                List.of(), List.of(new GerarOrcamentoInput.ItemServicoInput("servico-1", 1)), "obs");
    }

    private MinhaOrdemServicoOutput minhaOutput(String status, String placa) {
        return new MinhaOrdemServicoOutput("os-1", status, null,
                new MinhaOrdemServicoOutput.VeiculoResumo("veiculo-1", placa, "Toyota", "Corolla", 2020, "Branco"));
    }
}
