package com.mecanica.oficina_api.application.ordemservico;

import com.mecanica.oficina_api.domain.ordemservico.ItemOrcamento;
import com.mecanica.oficina_api.domain.ordemservico.Orcamento;
import com.mecanica.oficina_api.domain.ordemservico.OrcamentoStatus;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServicoStatus;
import com.mecanica.oficina_api.application.insumo.AlertaEstoqueBaixo;
import com.mecanica.oficina_api.application.insumo.NotificadorEstoqueBaixo;
import com.mecanica.oficina_api.application.insumo.OrigemNotificacaoEstoque;
import com.mecanica.oficina_api.domain.ordemservico.ServicoStatus;
import com.mecanica.oficina_api.domain.ordemservico.StatusServico;
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
import com.mecanica.oficina_api.interfaces.dto.request.GerarOrcamentoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CriarOrdemServicoRequest;
import com.mecanica.oficina_api.interfaces.dto.response.ItemOrcamentoResponse;
import com.mecanica.oficina_api.interfaces.dto.response.MinhaOrdemServicoResponse;
import com.mecanica.oficina_api.interfaces.dto.response.OrcamentoResponse;
import com.mecanica.oficina_api.interfaces.dto.response.OrdemServicoResponse;
import com.mecanica.oficina_api.interfaces.dto.response.ServicoStatusResponse;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrdemServicoService {

    private final OrdemServicoSpringDataRepository ordemServicoRepository;
    private final VeiculoSpringDataRepository veiculoRepository;
    private final ClienteSpringDataRepository clienteRepository;
    private final InsumosSpringDataRepository insumosRepository;
    private final ServicoSpringDataRepository servicoRepository;
    private final NotificadorEstoqueBaixo notificadorEstoqueBaixo;
    private final NotificadorCliente notificadorCliente;
    private final StatusServicoSpringDataRepository statusServicoSpringDataRepository;

    public OrdemServicoService(OrdemServicoSpringDataRepository ordemServicoRepository,
            VeiculoSpringDataRepository veiculoRepository,
            ClienteSpringDataRepository clienteRepository,
            InsumosSpringDataRepository insumosRepository,
            ServicoSpringDataRepository servicoRepository,
            NotificadorEstoqueBaixo notificadorEstoqueBaixo,
            NotificadorCliente notificadorCliente,
            StatusServicoSpringDataRepository statusServicoSpringDataRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.veiculoRepository = veiculoRepository;
        this.clienteRepository = clienteRepository;
        this.insumosRepository = insumosRepository;
        this.servicoRepository = servicoRepository;
        this.notificadorEstoqueBaixo = notificadorEstoqueBaixo;
        this.notificadorCliente = notificadorCliente;
        this.statusServicoSpringDataRepository = statusServicoSpringDataRepository;
    }

    public OrdemServicoResponse criar(CriarOrdemServicoRequest request) {
        veiculoRepository.findByIdAndAtivoTrue(request.getVeiculoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Veículo não encontrado: " + request.getVeiculoId()));
        clienteRepository.findById(request.getClienteId())
                .filter(c -> c.getAtivo())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cliente não encontrado: " + request.getClienteId()));

        OrdemServico os = OrdemServico.criar(request.getVeiculoId(), request.getClienteId());

        OrdemServicoJpaEntity entity = new OrdemServicoJpaEntity();
        entity.setVeiculoId(os.getVeiculoId());
        entity.setClienteId(os.getClienteId());
        entity.setStatus(os.getStatus().name());

        return toResponse(ordemServicoRepository.save(entity));
    }

    public OrdemServicoResponse buscarPorId(String id) {
        return toResponse(encontrarOuLancar(id));
    }

    public List<OrdemServicoResponse> listar(OrdemServicoStatus status) {
        var entities = status != null
                ? ordemServicoRepository.findByStatus(status.name())
                : ordemServicoRepository.findAll();
        return entities.stream().map(this::toResponse).toList();
    }

    public List<OrdemServicoResponse> listarPorVeiculo(String veiculoId) {
        return ordemServicoRepository.findByVeiculoId(veiculoId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<MinhaOrdemServicoResponse> listarMinhasOs(OrdemServicoStatus status, String placa) {
        UsuarioPrincipal principal = (UsuarioPrincipal)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String clienteId = principal.clienteId();

        List<OrdemServicoJpaEntity> osList = ordemServicoRepository.findByClienteId(clienteId);

        if (status != null) {
            osList = osList.stream().filter(os -> os.getStatus().equals(status.name())).toList();
        }

        if (placa != null && !placa.isBlank()) {
            String veiculoIdFiltro = veiculoRepository.findByPlacaIgnoreCaseAndAtivoTrue(placa)
                    .map(VeiculoJpaEntity::getId).orElse(null);
            if (veiculoIdFiltro == null) return List.of();
            final String vid = veiculoIdFiltro;
            osList = osList.stream().filter(os -> os.getVeiculoId().equals(vid)).toList();
        }

        return osList.stream().map(os -> {
            VeiculoJpaEntity v = veiculoRepository.findById(os.getVeiculoId()).orElse(null);
            MinhaOrdemServicoResponse.VeiculoResumo vr = v == null ? null
                    : new MinhaOrdemServicoResponse.VeiculoResumo(
                            v.getId(), v.getPlaca(), v.getMarca(), v.getModelo(), v.getAno(), v.getCor());
            return new MinhaOrdemServicoResponse(os.getId(), os.getStatus(), os.getOrcamentoStatus(), vr);
        }).toList();
    }

    public OrdemServicoResponse iniciarDiagnostico(String id) {
        return executarTransicao(id, OrdemServico::iniciarDiagnostico);
    }

    public OrdemServicoResponse gerarOrcamento(String id, GerarOrcamentoRequest request) {
        OrdemServicoJpaEntity entity = encontrarOuLancar(id);
        OrdemServico os = toDomain(entity);
        List<ItemComOrigem> itensComOrigem = toItensComOrigem(request);
        try {
            os.gerarOrcamento(itensComOrigem.stream().map(ItemComOrigem::item).toList(), request.getObservacoes());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
        return toResponse(salvarOrcamento(entity, os, itensComOrigem));
    }

    public OrdemServicoResponse atualizarOrcamento(String id, GerarOrcamentoRequest request) {
        OrdemServicoJpaEntity entity = encontrarOuLancar(id);
        OrdemServico os = toDomain(entity);
        List<ItemComOrigem> itensComOrigem = toItensComOrigem(request);
        try {
            os.atualizarOrcamento(itensComOrigem.stream().map(ItemComOrigem::item).toList(), request.getObservacoes());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
        return toResponse(salvarOrcamento(entity, os, itensComOrigem));
    }

    public OrdemServicoResponse enviarOrcamento(String id) {
        OrdemServicoJpaEntity entity = encontrarOuLancar(id);
        OrdemServico os = toDomain(entity);
        try {
            os.enviarOrcamento();
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
        OrdemServicoJpaEntity salva = salvarOrcamento(entity, os);
        notificadorCliente.notificar(NotificacaoCliente.envioOrcamento(salva));
        return toResponse(salva);
    }

    public OrdemServicoResponse aguardarOrcamento(String id) {
        return executarTransicao(id, OrdemServico::aguardarOrcamento);
    }

    public OrdemServicoResponse aprovarOrcamento(String id) {
        var response = executarTransicao(id, OrdemServico::aprovarOrcamento);
        return response;
    }

    public OrdemServicoResponse iniciarExecucao(String id) {
        OrdemServicoJpaEntity entity = encontrarOuLancar(id);
        OrdemServico os = toDomain(entity);

        criarStatusIndividuaisPorServico(os.getOrcamento().getItens(), os.getId());
        return executarTransicao(id, OrdemServico::iniciarExecucao);
    }

    public OrdemServicoResponse negarOrcamento(String id) {
        return executarTransicao(id, OrdemServico::negarOrcamento);
    }

    public List<ServicoStatusResponse> listarServicos(String id) {
        List<ServicoStatusResponse> response = statusServicoSpringDataRepository.findByOrdemServicoId(id).stream().map(servico ->
            new ServicoStatusResponse(
                servico.getId(),
                servico.getStatus(),
                servico.getOrdemServicoId(),
                servico.getServicoId(),
                servico.getDataInicio(),
                servico.getDataFim()
            )
        ).toList();


        return response;
    }

    public ServicoStatusResponse iniciarServico(String servico_id) {
        var servicoEntity = statusServicoSpringDataRepository.findByIdAndStatus(servico_id, ServicoStatus.AGUARDANDO.toString()).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Servico não encontrado"));

        var servico = StatusServico.recriar(
            servicoEntity.getId(),
            ServicoStatus.valueOf(servicoEntity.getStatus()),
            servicoEntity.getOrdemServicoId(),
            servicoEntity.getServicoId(),
            servicoEntity.getDataInicio(),
            servicoEntity.getDataFim()
        );

        servico.iniciarServico();

        servicoEntity.setStatus(servico.getStatus().toString());
        servicoEntity.setDataInicio(servico.getDataInicio());

        var servicoResponse = statusServicoSpringDataRepository.save(servicoEntity);

        return new ServicoStatusResponse(
            servicoResponse.getId().toString(),
            servicoResponse.getStatus(),
            servicoResponse.getOrdemServicoId(),
            servicoResponse.getServicoId(),
            servicoResponse.getDataInicio(),
            servicoResponse.getDataFim()
        );
    }

    public ServicoStatusResponse finalizarServico(String servico_id){
        var servicoEntity = statusServicoSpringDataRepository.findByIdAndStatus(servico_id, ServicoStatus.INICIADO.toString()).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Servico não encontrado"));

        var servico = StatusServico.recriar(
            servicoEntity.getId(),
            ServicoStatus.valueOf(servicoEntity.getStatus()),
            servicoEntity.getOrdemServicoId(),
            servicoEntity.getServicoId(),
            servicoEntity.getDataInicio(),
            servicoEntity.getDataFim()
        );

        servico.finalizarServico();

        servicoEntity.setStatus(servico.getStatus().toString());
        servicoEntity.setDataInicio(servico.getDataInicio());

        var servicoResponse = statusServicoSpringDataRepository.save(servicoEntity);

        return new ServicoStatusResponse(
            servicoResponse.getId().toString(),
            servicoResponse.getStatus(),
            servicoResponse.getOrdemServicoId(),
            servicoResponse.getServicoId(),
            servicoResponse.getDataInicio(),
            servicoResponse.getDataFim()
        );
    }

    @Transactional
    public OrdemServicoResponse finalizar(String id) {
        OrdemServicoJpaEntity entity = encontrarOuLancar(id);
        OrdemServico os = toDomain(entity);
        try {
            os.finalizar();
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
        darBaixaInsumos(entity);
        verificarServicosFinalizados(os.getId());

        OrdemServicoJpaEntity salva = salvarOrcamento(entity, os);
        notificadorCliente.notificar(NotificacaoCliente.finalizacao(salva));
        return toResponse(salva);
    }

    public OrdemServicoResponse entregar(String id) {
        return executarTransicao(id, OrdemServico::entregar);
    }

    // --- helpers ---

    private void verificarServicosFinalizados(String ordemServicoId) {
        List<StatusServicoJpaEntity> servicoEntityList = statusServicoSpringDataRepository.findByOrdemServicoId(ordemServicoId);

        int servicoFinalizadoContador = 0;
        for(StatusServicoJpaEntity servicoEntity : servicoEntityList){
            if(servicoEntity.getStatus().equals(ServicoStatus.FINALIZADO.toString())) {
                servicoFinalizadoContador++;
            }
        }

        if(servicoFinalizadoContador != servicoEntityList.size()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ainda há serviços para serem realizados na OS");
        }
    }

    private List<StatusServicoJpaEntity> criarStatusIndividuaisPorServico(List<ItemOrcamento> itemOrcamentoList, String ordemServicoId) {
        List<ItemOrcamento> itensOrcamento = itemOrcamentoList;
        List<StatusServico> statusServicoList = new ArrayList<>();
        itensOrcamento.forEach(item -> {
            if(!Objects.isNull(item.getServicoId())){
                StatusServico statusServico = StatusServico.criar(ordemServicoId, item.getServicoId());
                statusServicoList.add(statusServico);
            }
        });

        List<StatusServicoJpaEntity> servicoEntity = statusServicoList.stream().map(servico -> {
            StatusServicoJpaEntity servicoJpaEntity = new StatusServicoJpaEntity();
            servicoJpaEntity.setServicoId(servico.getServicoId());
            servicoJpaEntity.setOrdemServicoId(servico.getOrdemServicoId());
            servicoJpaEntity.setStatus(servico.getStatus().toString());
            servicoJpaEntity.setDataFim(servico.getDataInicio());
            servicoJpaEntity.setDataFim(servico.getDataFim());

            return servicoJpaEntity;
        }).toList();

        return statusServicoSpringDataRepository.saveAll(servicoEntity);
    }

    private OrdemServicoResponse executarTransicao(String id, java.util.function.Consumer<OrdemServico> acao) {
        OrdemServicoJpaEntity entity = encontrarOuLancar(id);
        OrdemServico os = toDomain(entity);
        try {
            acao.accept(os);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
        return toResponse(salvarOrcamento(entity, os));
    }

    private void darBaixaInsumos(OrdemServicoJpaEntity entity) {
        for (ItemOrcamentoJpaEntity item : entity.getItensOrcamento()) {
            if (item.getInsumoId() == null) continue;
            InsumosJpaEntity insumoEntity = insumosRepository.findByIdAndAtivoTrue(item.getInsumoId())
                    .orElse(null);
            if (insumoEntity == null) continue;
            int estoqueAnterior = insumoEntity.getEstoqueAtual();
            int estoqueMinimoAnterior = insumoEntity.getEstoqueMinimo();
            int novoEstoque = insumoEntity.getEstoqueAtual() - item.getQuantidade();
            if (novoEstoque < 0) novoEstoque = 0;
            insumoEntity.setEstoqueAtual(novoEstoque);
            insumosRepository.save(insumoEntity);
            if (AlertaEstoqueBaixo.deveEmitirAlerta(
                    estoqueAnterior, estoqueMinimoAnterior, novoEstoque, insumoEntity.getEstoqueMinimo())) {
                notificadorEstoqueBaixo.notificar(new AlertaEstoqueBaixo(
                        insumoEntity.getId(),
                        insumoEntity.getNome(),
                        estoqueAnterior,
                        novoEstoque,
                        insumoEntity.getEstoqueMinimo(),
                        OrigemNotificacaoEstoque.BAIXA_ORDEM_SERVICO,
                        entity.getId()));
            }
        }
    }

    private OrdemServicoJpaEntity encontrarOuLancar(String id) {
        return ordemServicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Ordem de serviço não encontrada: " + id));
    }

    private record ItemComOrigem(ItemOrcamento item, Boolean EhInsumo) {}

    private List<ItemComOrigem> toItensComOrigem(GerarOrcamentoRequest request) {
        List<ItemComOrigem> itens = new ArrayList<>();

        if (request.getInsumos() != null) {
            for (var i : request.getInsumos()) {
                InsumosJpaEntity insumo = insumosRepository.findByIdAndAtivoTrue(i.getInsumoId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Insumo não encontrado: " + i.getInsumoId()));
                itens.add(new ItemComOrigem(
                        new ItemOrcamento(insumo.getId(), null, insumo.getNome(), i.getQuantidade(), insumo.getPrecoUnitario()),
                        true));
            }
        }

        if (request.getServicos() != null) {
            for (var s : request.getServicos()) {
                ServicoJpaEntity servico = servicoRepository.findByIdAndAtivoTrue(s.getServicoId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Serviço não encontrado: " + s.getServicoId()));
                itens.add(new ItemComOrigem(
                        new ItemOrcamento(null, servico.getId(), servico.getNome(), s.getQuantidade(), servico.getPreco()),
                        false));
            }
        }

        if (itens.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Orçamento deve ter ao menos um item");

        return itens;
    }

    private OrdemServico toDomain(OrdemServicoJpaEntity entity) {
        OrcamentoStatus orcStatus = entity.getOrcamentoStatus() != null
                ? OrcamentoStatus.valueOf(entity.getOrcamentoStatus())
                : null;
        Orcamento orcamento = null;
        if (orcStatus != null && !entity.getItensOrcamento().isEmpty()) {
            List<ItemOrcamento> itens = entity.getItensOrcamento().stream()
                    .map(i -> new ItemOrcamento(i.getInsumoId(), i.getServicoId(), i.getDescricao(), i.getQuantidade(), i.getPrecoUnitario()))
                    .toList();
            orcamento = Orcamento.reconstituir(itens, orcStatus, entity.getOrcamentoObservacoes(),
                    entity.getOrcamentoRespondidoEm());
        }
        OrdemServicoStatus domainStatus = OrdemServicoStatus.valueOf(entity.getStatus());
        return OrdemServico.reconstituir(entity.getId(), entity.getVeiculoId(), entity.getClienteId(),
                domainStatus, orcamento, entity.getValorFinal(), entity.getDataFinal());
    }

    private OrdemServicoJpaEntity salvarOrcamento(OrdemServicoJpaEntity entity, OrdemServico os) {
        return salvarOrcamento(entity, os, null);
    }

    private OrdemServicoJpaEntity salvarOrcamento(OrdemServicoJpaEntity entity, OrdemServico os,
                                                   List<ItemComOrigem> itensComOrigem) {
        entity.setStatus(os.getStatus().name());
        entity.setValorFinal(os.getValorFinal());
        entity.setDataFinal(os.getDataFinal());
        Orcamento orc = os.getOrcamento();
        if (orc != null) {
            entity.setOrcamentoStatus(orc.getStatus().name());
            entity.setOrcamentoObservacoes(orc.getObservacoes());
            entity.setOrcamentoRespondidoEm(orc.getRespondidoEm());
            List<ItemOrcamento> domainItens = orc.getItens();
            // Preserva insumo_id ao regravar só status do orçamento (enviar, aprovar, finalizar…).
            // Sem isso, itensComOrigem=null zera o vínculo e darBaixaInsumos ignora o item na finalização.
            List<ItemOrcamentoJpaEntity> anteriores = List.copyOf(entity.getItensOrcamento());
            List<ItemOrcamentoJpaEntity> itensEntity = new ArrayList<>();
            for (int idx = 0; idx < domainItens.size(); idx++) {
                ItemOrcamento di = domainItens.get(idx);
                ItemOrcamentoJpaEntity ie = new ItemOrcamentoJpaEntity();
                ie.setDescricao(di.getDescricao());
                ie.setQuantidade(di.getQuantidade());
                ie.setPrecoUnitario(di.getPrecoUnitario());
                if (itensComOrigem != null && idx < itensComOrigem.size()) {
                    if(itensComOrigem.get(idx).EhInsumo()){
                        ie.setInsumoId(di.getInsumoId());
                    } else {
                        ie.setServicoId(di.getServicoId());
                    }
                } else if (idx < anteriores.size()) {
                    ie.setInsumoId(anteriores.get(idx).getInsumoId());
                }

                if(di.getServicoId() != null){
                    ie.setServicoId(di.getServicoId());
                }

                if(di.getInsumoId() != null){
                    ie.setInsumoId(di.getInsumoId());
                }
                itensEntity.add(ie);
            }
            entity.getItensOrcamento().clear();
            entity.getItensOrcamento().addAll(itensEntity);
        }
        return ordemServicoRepository.save(entity);
    }

    private OrdemServicoResponse toResponse(OrdemServicoJpaEntity entity) {
        OrcamentoResponse orcResp = null;
        if (entity.getOrcamentoStatus() != null) {
            List<ItemOrcamentoResponse> itensResp = entity.getItensOrcamento().stream()
                    .map(i -> new ItemOrcamentoResponse(
                            i.getInsumoId(), i.getServicoId(), i.getDescricao(), i.getQuantidade(), i.getPrecoUnitario(),
                            i.getPrecoUnitario().multiply(BigDecimal.valueOf(i.getQuantidade()))))
                    .toList();
            BigDecimal total = itensResp.stream()
                    .map(ItemOrcamentoResponse::getValorTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            orcResp = new OrcamentoResponse(entity.getOrcamentoStatus(), itensResp, total,
                    entity.getOrcamentoObservacoes(), entity.getOrcamentoRespondidoEm());
        }
        return new OrdemServicoResponse(entity.getId(), entity.getVeiculoId(), entity.getClienteId(),
                entity.getStatus(), entity.getValorFinal(), entity.getDataFinal(), orcResp);
    }
}
