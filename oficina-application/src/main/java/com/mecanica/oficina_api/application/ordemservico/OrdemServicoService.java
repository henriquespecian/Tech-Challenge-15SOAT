package com.mecanica.oficina_api.application.ordemservico;

import com.mecanica.oficina_api.domain.insumo.Insumos;
import com.mecanica.oficina_api.domain.insumo.OrigemNotificacaoEstoque;
import com.mecanica.oficina_api.domain.ordemservico.ItemOrcamento;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServicoStatus;
import com.mecanica.oficina_api.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_api.application.insumo.gateway.InsumosGateway;
import com.mecanica.oficina_api.application.insumo.gateway.NotificarEstoqueBaixoGateway;
import com.mecanica.oficina_api.application.insumo.output.AlertaEstoqueBaixo;
import com.mecanica.oficina_api.application.ordemservico.gateway.NotificadorCliente;
import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
import com.mecanica.oficina_api.application.ordemservico.input.GerarOrcamentoInput;
import com.mecanica.oficina_api.application.ordemservico.output.MinhaOrdemServicoOutput;
import com.mecanica.oficina_api.application.servico.gateway.ServicoGateway;
import com.mecanica.oficina_api.application.servico.gateway.StatusServicoGateway;
import com.mecanica.oficina_api.application.veiculo.gateway.VeiculoGateway;
import com.mecanica.oficina_api.domain.ordemservico.ServicoStatus;
import com.mecanica.oficina_api.domain.ordemservico.StatusServico;
import com.mecanica.oficina_api.domain.servico.Servico;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;


public class OrdemServicoService {

    private final OrdemServicoGateway ordemServicoGateway;
    private final VeiculoGateway veiculoGateway;
    private final ClienteGateway clienteGateway;
    private final InsumosGateway insumosGateway;
    private final ServicoGateway servicoGateway;
    private final NotificarEstoqueBaixoGateway notificadorEstoqueBaixo;
    private final NotificadorCliente notificadorCliente;
    private final StatusServicoGateway statusServicoGateway;

    public OrdemServicoService(OrdemServicoGateway ordemServicoGateway,
            VeiculoGateway veiculoGateway,
            ClienteGateway clienteGateway,
            InsumosGateway insumosGateway,
            ServicoGateway servicoGateway,
            NotificarEstoqueBaixoGateway notificadorEstoqueBaixo,
            NotificadorCliente notificadorCliente,
            StatusServicoGateway statusServicoGateway) {
        this.ordemServicoGateway = ordemServicoGateway;
        this.veiculoGateway = veiculoGateway;
        this.clienteGateway = clienteGateway;
        this.insumosGateway = insumosGateway;
        this.servicoGateway = servicoGateway;
        this.notificadorEstoqueBaixo = notificadorEstoqueBaixo;
        this.notificadorCliente = notificadorCliente;
        this.statusServicoGateway = statusServicoGateway;
    }

    public OrdemServico criar(String veiculoId, String clienteId) {
        veiculoGateway.buscar(veiculoId)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + veiculoId));
        clienteGateway.findByIdAtivo(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + clienteId));

        return ordemServicoGateway.cadastrar(veiculoId, clienteId);
    }

    public OrdemServico buscarPorId(String id) {
        return encontrarOuLancar(id);
    }

    public List<OrdemServico> listar(OrdemServicoStatus status) {
        return ordemServicoGateway.listar(status);
    }

    public List<OrdemServico> listarPorVeiculo(String veiculoId) {
        return ordemServicoGateway.listarPorVeiculo(veiculoId);
    }

    public List<MinhaOrdemServicoOutput> listarMinhasOs(String clienteId, OrdemServicoStatus status, String placa) {

        List<MinhaOrdemServicoOutput> osList = ordemServicoGateway.buscarPorCliente(clienteId);

        if (status != null) {
            osList = osList.stream()
                .filter(os -> os.getStatus().equals(status.name()))
                .toList();
        }

        if (placa != null && !placa.isBlank()) {
            osList = osList.stream().filter(os -> os.getPlaca().equals(placa)).toList();
        }

        return osList;
    }

    public OrdemServico iniciarDiagnostico(String id) {
        return executarTransicao(id, OrdemServico::iniciarDiagnostico);
    }

    public OrdemServico gerarOrcamento(String id, GerarOrcamentoInput input) {
        OrdemServico os = encontrarOuLancar(id);
        os.gerarOrcamento(montarItens(input), input.observacoes());
        return ordemServicoGateway.atualizar(os);
    }

    public OrdemServico atualizarOrcamento(String id, GerarOrcamentoInput input) {
        OrdemServico os = encontrarOuLancar(id);
        os.atualizarOrcamento(montarItens(input), input.observacoes());
        return ordemServicoGateway.atualizar(os);
    }

    public OrdemServico enviarOrcamento(String id) {
        OrdemServico os = encontrarOuLancar(id);
        os.enviarOrcamento();
        OrdemServico salva = ordemServicoGateway.atualizar(os);
        notificadorCliente.notificar(NotificacaoCliente.envioOrcamento(salva));
        return salva;
    }

    public OrdemServico aguardarOrcamento(String id) {
        return executarTransicao(id, OrdemServico::aguardarOrcamento);
    }

    public OrdemServico aprovarOrcamento(String id) {
        OrdemServico os = encontrarOuLancar(id);
        os.aprovarOrcamento();
        darBaixaInsumos(os);
        return ordemServicoGateway.atualizar(os);
    }

    public OrdemServico iniciarExecucao(String id) {
        OrdemServico os = encontrarOuLancar(id);
        os.iniciarExecucao();
        criarStatusIndividuaisPorServico(os.getOrcamento().getItens(), os.getId());
        return ordemServicoGateway.atualizar(os);
    }

    public OrdemServico negarOrcamento(String id) {
        return executarTransicao(id, OrdemServico::negarOrcamento);
    }

    public List<StatusServico> listarServicos(String id) {
        return statusServicoGateway.listarServicosPorOS(id);
    }

    public StatusServico iniciarServico(String servico_id) {
        StatusServico servico = statusServicoGateway.buscarPorIdEStatus(servico_id, ServicoStatus.AGUARDANDO)
            .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));
        servico.iniciarServico();
        return statusServicoGateway.atualizar(servico);
    }

    public StatusServico finalizarServico(String servico_id) {
        StatusServico servico = statusServicoGateway.buscarPorIdEStatus(servico_id, ServicoStatus.INICIADO)
            .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));
        servico.finalizarServico();
        return statusServicoGateway.atualizar(servico);
    }

    public OrdemServico finalizar(String id) {
        OrdemServico os = encontrarOuLancar(id);
        os.finalizar();
        verificarServicosFinalizados(os.getId());

        OrdemServico salva = ordemServicoGateway.atualizar(os);
        notificadorCliente.notificar(NotificacaoCliente.finalizacao(salva));
        return salva;
    }

    public OrdemServico entregar(String id) {
        return executarTransicao(id, OrdemServico::entregar);
    }

    // --- helpers ---

    private void verificarServicosFinalizados(String ordemServicoId) {
        List<StatusServico> servicoEntityList = statusServicoGateway.listarServicosPorOS(ordemServicoId);

        int servicoFinalizadoContador = 0;
        for (StatusServico servicoEntity : servicoEntityList) {
            if (servicoEntity.getStatus().equals(ServicoStatus.FINALIZADO)) {
                servicoFinalizadoContador++;
            }
        }

        if (servicoFinalizadoContador != servicoEntityList.size()) {
            throw new IllegalStateException("Ainda há serviços para serem realizados na OS");
        }
    }

    private List<StatusServico> criarStatusIndividuaisPorServico(List<ItemOrcamento> itemOrcamentoList, String ordemServicoId) {
        List<StatusServico> statusServicoList = new ArrayList<>();
        itemOrcamentoList.forEach(item -> {
            if (!Objects.isNull(item.getServicoId())) {
                StatusServico statusServico = StatusServico.criar(ordemServicoId, item.getServicoId());
                statusServicoList.add(statusServico);
            }
        });

        return statusServicoGateway.salvarLista(statusServicoList);
    }

    private OrdemServico executarTransicao(String id, java.util.function.Consumer<OrdemServico> acao) {
        OrdemServico os = encontrarOuLancar(id);
        acao.accept(os);
        return ordemServicoGateway.atualizar(os);
    }

    private List<ItemOrcamento> montarItens(GerarOrcamentoInput input) {
        List<ItemOrcamento> itens = new ArrayList<>();

        if (input.insumos() != null) {
            for (var i : input.insumos()) {
                Insumos insumo = insumosGateway.buscar(i.insumoId())
                        .orElseThrow(() -> new IllegalArgumentException("Insumo não encontrado: " + i.insumoId()));
                itens.add(new ItemOrcamento(insumo.getId(), null, insumo.getNome(), i.quantidade(), insumo.getPrecoUnitario()));
            }
        }

        if (input.servicos() != null) {
            for (var s : input.servicos()) {
                Servico servico = servicoGateway.buscar(s.servicoId())
                        .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado: " + s.servicoId()));
                itens.add(new ItemOrcamento(null, servico.getId(), servico.getNome(), s.quantidade(), servico.getPreco()));
            }
        }

        if (itens.isEmpty()) {
            throw new IllegalArgumentException("Orçamento deve ter ao menos um item");
        }

        return itens;
    }

    private void darBaixaInsumos(OrdemServico ordemServico) {
        for (ItemOrcamento item : ordemServico.getOrcamento().getItens()) {

            if (item.getInsumoId() == null) {
                continue;
            }

            Insumos insumo = insumosGateway.buscar(item.getInsumoId()).orElse(null);

            if (insumo == null) {
                continue;
            }

            //Calcula o estoque
            int estoqueAnterior = insumo.getEstoqueAtual();
            int estoqueMinimoAnterior = insumo.getEstoqueMinimo();
            int novoEstoque = insumo.getEstoqueAtual() - item.getQuantidade();

            if (novoEstoque < 0) {
                throw new IllegalStateException("Estoque insuficiente para o insumo: " + insumo.getNome());
            }

            insumo.setEstoqueAtual(novoEstoque);
            insumosGateway.alterar(insumo.getId(), insumo);

            if (Insumos.deveEmitirAlerta(estoqueAnterior, estoqueMinimoAnterior, novoEstoque, insumo.getEstoqueMinimo())) {
                notificadorEstoqueBaixo.notificar(new AlertaEstoqueBaixo(
                        insumo.getId(),
                        insumo.getNome(),
                        estoqueAnterior,
                        novoEstoque,
                        insumo.getEstoqueMinimo(),
                        OrigemNotificacaoEstoque.BAIXA_ORDEM_SERVICO));
            }
        }
    }

    private OrdemServico encontrarOuLancar(String id) {
        return ordemServicoGateway.buscar(id)
                .orElseThrow(() -> new IllegalArgumentException("Ordem de serviço não encontrada: " + id));
    }
}
