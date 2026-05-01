package com.mecanica.oficina_api.application.ordemservico;

import com.mecanica.oficina_api.domain.ordemservico.ItemOrcamento;
import com.mecanica.oficina_api.domain.ordemservico.Orcamento;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;
import com.mecanica.oficina_api.infrastructure.persistence.ItemOrcamentoJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.OrdemServicoJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.repository.ClienteSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.OrdemServicoSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.VeiculoSpringDataRepository;
import com.mecanica.oficina_api.interfaces.dto.request.GerarOrcamentoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CriarOrdemServicoRequest;
import com.mecanica.oficina_api.interfaces.dto.response.ItemOrcamentoResponse;
import com.mecanica.oficina_api.interfaces.dto.response.OrcamentoResponse;
import com.mecanica.oficina_api.interfaces.dto.response.OrdemServicoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class OrdemServicoService {

    private final OrdemServicoSpringDataRepository ordemServicoRepository;
    private final VeiculoSpringDataRepository veiculoRepository;
    private final ClienteSpringDataRepository clienteRepository;

    public OrdemServicoService(OrdemServicoSpringDataRepository ordemServicoRepository,
                               VeiculoSpringDataRepository veiculoRepository,
                               ClienteSpringDataRepository clienteRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.veiculoRepository = veiculoRepository;
        this.clienteRepository = clienteRepository;
    }

    public void criar(CriarOrdemServicoRequest request) {
        veiculoRepository.findByIdAndAtivoTrue(request.getVeiculoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veículo não encontrado: " + request.getVeiculoId()));
        clienteRepository.findById(request.getClienteId())
                .filter(c -> c.getAtivo())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado: " + request.getClienteId()));

        OrdemServico os = OrdemServico.criar(request.getVeiculoId(), request.getClienteId());

        OrdemServicoJpaEntity entity = new OrdemServicoJpaEntity();
        entity.setVeiculoId(os.getVeiculoId());
        entity.setClienteId(os.getClienteId());
        entity.setStatus(os.getStatus().name());

        ordemServicoRepository.save(entity);
    }

    public OrdemServicoResponse buscarPorId(String id) {
        return toResponse(encontrarOuLancar(id));
    }

    public List<OrdemServicoResponse> listarPorVeiculo(String veiculoId) {
        return ordemServicoRepository.findByVeiculoId(veiculoId).stream()
                .map(this::toResponse)
                .toList();
    }

    public OrdemServicoResponse gerarOrcamento(String id, GerarOrcamentoRequest request) {
        OrdemServicoJpaEntity entity = encontrarOuLancar(id);
        OrdemServico os = toDomain(entity);
        try {
            os.gerarOrcamento(toItemOrcamentoDomain(request), request.getObservacoes());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
        return toResponse(salvarOrcamento(entity, os));
    }

    public OrdemServicoResponse atualizarOrcamento(String id, GerarOrcamentoRequest request) {
        OrdemServicoJpaEntity entity = encontrarOuLancar(id);
        OrdemServico os = toDomain(entity);
        try {
            os.atualizarOrcamento(toItemOrcamentoDomain(request), request.getObservacoes());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
        return toResponse(salvarOrcamento(entity, os));
    }

    public OrdemServicoResponse enviarOrcamento(String id) {
        return executarTransicao(id, OrdemServico::enviarOrcamento);
    }

    public OrdemServicoResponse negociarOrcamento(String id) {
        return executarTransicao(id, OrdemServico::negociarOrcamento);
    }

    public OrdemServicoResponse aprovarOrcamento(String id) {
        return executarTransicao(id, OrdemServico::aprovarOrcamento);
    }

    public OrdemServicoResponse negarOrcamento(String id) {
        return executarTransicao(id, OrdemServico::negarOrcamento);
    }

    public OrdemServicoResponse retirarVeiculo(String id) {
        return executarTransicao(id, OrdemServico::retirarVeiculo);
    }

    // --- helpers ---

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

    private OrdemServicoJpaEntity encontrarOuLancar(String id) {
        return ordemServicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem de serviço não encontrada: " + id));
    }

    private List<ItemOrcamento> toItemOrcamentoDomain(GerarOrcamentoRequest request) {
        return request.getItens().stream()
                .map(i -> new ItemOrcamento(i.getDescricao(), i.getQuantidade(), i.getPrecoUnitario()))
                .toList();
    }

    private OrdemServico toDomain(OrdemServicoJpaEntity entity) {
        com.mecanica.oficina_api.domain.ordemservico.OrcamentoStatus orcStatus = entity.getOrcamentoStatus() != null
                ? com.mecanica.oficina_api.domain.ordemservico.OrcamentoStatus.valueOf(entity.getOrcamentoStatus())
                : null;
        com.mecanica.oficina_api.domain.ordemservico.Orcamento orcamento = null;
        if (orcStatus != null && !entity.getItensOrcamento().isEmpty()) {
            List<ItemOrcamento> itens = entity.getItensOrcamento().stream()
                    .map(i -> new ItemOrcamento(i.getDescricao(), i.getQuantidade(), i.getPrecoUnitario()))
                    .toList();
            orcamento = com.mecanica.oficina_api.domain.ordemservico.Orcamento.reconstituir(
                    itens, orcStatus, entity.getOrcamentoObservacoes());
        }
        com.mecanica.oficina_api.domain.ordemservico.OrdemServicoStatus domainStatus =
                com.mecanica.oficina_api.domain.ordemservico.OrdemServicoStatus.valueOf(entity.getStatus());
        return OrdemServico.reconstituir(entity.getId(), entity.getVeiculoId(), entity.getClienteId(), domainStatus, orcamento);
    }

    private OrdemServicoJpaEntity salvarOrcamento(OrdemServicoJpaEntity entity, OrdemServico os) {
        entity.setStatus(os.getStatus().name());
        Orcamento orc = os.getOrcamento();
        if (orc != null) {
            entity.setOrcamentoStatus(orc.getStatus().name());
            entity.setOrcamentoObservacoes(orc.getObservacoes());
            List<ItemOrcamentoJpaEntity> itensEntity = orc.getItens().stream()
                    .map(i -> {
                        ItemOrcamentoJpaEntity ie = new ItemOrcamentoJpaEntity();
                        ie.setDescricao(i.getDescricao());
                        ie.setQuantidade(i.getQuantidade());
                        ie.setPrecoUnitario(i.getPrecoUnitario());
                        return ie;
                    }).toList();
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
                            i.getDescricao(), i.getQuantidade(), i.getPrecoUnitario(),
                            i.getPrecoUnitario().multiply(java.math.BigDecimal.valueOf(i.getQuantidade()))))
                    .toList();
            java.math.BigDecimal total = itensResp.stream()
                    .map(ItemOrcamentoResponse::getValorTotal)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            orcResp = new OrcamentoResponse(entity.getOrcamentoStatus(), itensResp, total, entity.getOrcamentoObservacoes());
        }
        return new OrdemServicoResponse(entity.getId(), entity.getVeiculoId(), entity.getClienteId(), entity.getStatus(), orcResp);
    }
}
