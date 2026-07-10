package com.mecanica.oficina_api.adapters.persistence;

import com.mecanica.oficina_api.adapters.persistence.repository.OrdemServicoSpringDataRepository;
import com.mecanica.oficina_api.adapters.persistence.repository.VeiculoSpringDataRepository;
import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
import com.mecanica.oficina_api.application.ordemservico.output.MinhaOrdemServicoOutput;
import com.mecanica.oficina_api.domain.ordemservico.ItemOrcamento;
import com.mecanica.oficina_api.domain.ordemservico.Orcamento;
import com.mecanica.oficina_api.domain.ordemservico.OrcamentoStatus;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServicoStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class OrdemServicoJpaGateway implements OrdemServicoGateway {

    private final OrdemServicoSpringDataRepository repository;
    private final VeiculoSpringDataRepository veiculoRepository;

    public OrdemServicoJpaGateway(OrdemServicoSpringDataRepository repository,
                                  VeiculoSpringDataRepository veiculoRepository) {
        this.repository = repository;
        this.veiculoRepository = veiculoRepository;
    }

    @Override
    public OrdemServico cadastrar(String veiculoId, String clienteId) {
        OrdemServicoJpaEntity entity = new OrdemServicoJpaEntity();
        entity.setVeiculoId(veiculoId);
        entity.setClienteId(clienteId);
        entity.setStatus(OrdemServicoStatus.RECEBIDA.name());
        entity.setDataCriacao(java.time.LocalDateTime.now());
        return toDomain(repository.save(entity));
    }

    @Override
    public OrdemServico atualizar(OrdemServico ordemServico) {
        OrdemServicoJpaEntity entity = repository.findById(ordemServico.getId())
                .orElseThrow(() -> new IllegalArgumentException("Ordem de serviço não encontrada: " + ordemServico.getId()));
        aplicar(ordemServico, entity);
        return toDomain(repository.save(entity));
    }

    @Override
    public Optional<OrdemServico> buscar(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<OrdemServico> listar(OrdemServicoStatus status) {
        var entities = status != null
                ? repository.findByStatus(status.name())
                : repository.findAll();
        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    public List<OrdemServico> listarAtivas() {
        return repository.findAtivasOrdenadas().stream().map(this::toDomain).toList();
    }

    @Override
    public List<OrdemServico> listarPorVeiculo(String veiculoId) {
        return repository.findByVeiculoId(veiculoId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<MinhaOrdemServicoOutput> buscarPorCliente(String clienteId) {
        return repository.findByClienteId(clienteId).stream().map(this::toMinhaOutput).toList();
    }

    // --- mapeamento ---

    private OrdemServico toDomain(OrdemServicoJpaEntity entity) {
        OrcamentoStatus orcStatus = entity.getOrcamentoStatus() != null
                ? OrcamentoStatus.valueOf(entity.getOrcamentoStatus())
                : null;
        Orcamento orcamento = null;
        if (orcStatus != null) {
            List<ItemOrcamento> itens = entity.getItensOrcamento().stream()
                    .map(i -> new ItemOrcamento(i.getInsumoId(), i.getServicoId(), i.getDescricao(),
                            i.getQuantidade(), i.getPrecoUnitario()))
                    .toList();
            orcamento = Orcamento.reconstituir(itens, orcStatus, entity.getOrcamentoObservacoes(),
                    entity.getOrcamentoRespondidoEm());
        }
        return OrdemServico.reconstituir(entity.getId(), entity.getVeiculoId(), entity.getClienteId(),
                OrdemServicoStatus.valueOf(entity.getStatus()), orcamento, entity.getValorFinal(),
                entity.getDataFinal());
    }

    private void aplicar(OrdemServico os, OrdemServicoJpaEntity entity) {
        entity.setStatus(os.getStatus().name());
        entity.setValorFinal(os.getValorFinal());
        entity.setDataFinal(os.getDataFinal());

        Orcamento orc = os.getOrcamento();
        if (orc != null) {
            entity.setOrcamentoStatus(orc.getStatus().name());
            entity.setOrcamentoObservacoes(orc.getObservacoes());
            entity.setOrcamentoRespondidoEm(orc.getRespondidoEm());

            List<ItemOrcamentoJpaEntity> itensEntity = new ArrayList<>();
            for (ItemOrcamento di : orc.getItens()) {
                ItemOrcamentoJpaEntity ie = new ItemOrcamentoJpaEntity();
                ie.setDescricao(di.getDescricao());
                ie.setQuantidade(di.getQuantidade());
                ie.setPrecoUnitario(di.getPrecoUnitario());
                ie.setInsumoId(di.getInsumoId());
                ie.setServicoId(di.getServicoId());
                itensEntity.add(ie);
            }
            entity.getItensOrcamento().clear();
            entity.getItensOrcamento().addAll(itensEntity);
        }
    }

    private MinhaOrdemServicoOutput toMinhaOutput(OrdemServicoJpaEntity entity) {
        MinhaOrdemServicoOutput.VeiculoResumo veiculo = veiculoRepository.findById(entity.getVeiculoId())
                .map(v -> new MinhaOrdemServicoOutput.VeiculoResumo(
                        v.getId(), v.getPlaca(), v.getMarca(), v.getModelo(), v.getAno(), v.getCor()))
                .orElse(null);
        return new MinhaOrdemServicoOutput(entity.getId(), entity.getStatus(), entity.getDataCriacao(), entity.getOrcamentoStatus(), veiculo);
    }
}
