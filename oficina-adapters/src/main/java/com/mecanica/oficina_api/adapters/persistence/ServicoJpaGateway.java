package com.mecanica.oficina_api.adapters.persistence;

import com.mecanica.oficina_api.adapters.persistence.repository.ServicoSpringDataRepository;
import com.mecanica.oficina_api.application.servico.gateway.ServicoGateway;
import com.mecanica.oficina_api.domain.servico.Servico;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ServicoJpaGateway implements ServicoGateway {

    private final ServicoSpringDataRepository repository;

    public ServicoJpaGateway(ServicoSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public Servico cadastrar(Servico servico) {
        return toDomain(repository.save(toEntity(servico)));
    }

    @Override
    public Optional<Servico> buscar(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Servico> listar() {
        return repository.findAllByAtivoTrue().stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public Servico alterar(String id, Servico servico) {
        return toDomain(repository.save(toEntity(servico)));
    }

    @Override
    public Servico ativar(String id) {
        ServicoJpaEntity entity = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));
        entity.setAtivo(true);
        return toDomain(repository.save(entity));
    }

    @Override
    public void inativar(String id) {
        repository.findById(id).ifPresent(e -> {
            e.setAtivo(false);
            repository.save(e);
        });
    }

    private Servico toDomain(ServicoJpaEntity e) {
        return Servico.reconstituir(
            e.getId(), e.getNome(), e.getDescricao(),
            e.getPreco(), e.getTempoEstimadoHoras(), e.isAtivo()
        );
    }

    private ServicoJpaEntity toEntity(Servico s) {
        ServicoJpaEntity e = new ServicoJpaEntity();
        e.setId(s.getId());
        e.setNome(s.getNome());
        e.setDescricao(s.getDescricao());
        e.setPreco(s.getPreco());
        e.setTempoEstimadoHoras(s.getTempoEstimadoHoras());
        e.setAtivo(s.isAtivo());
        return e;
    }
}
