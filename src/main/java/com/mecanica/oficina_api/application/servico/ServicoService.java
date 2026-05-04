package com.mecanica.oficina_api.application.servico;

import com.mecanica.oficina_api.domain.ordemservico.ServicoStatus;
import com.mecanica.oficina_api.domain.servico.Servico;
import com.mecanica.oficina_api.infrastructure.persistence.ServicoJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.repository.ServicoSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.StatusServicoSpringDataRepository;
import com.mecanica.oficina_api.interfaces.dto.request.AlterarServicoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarServicoRequest;
import com.mecanica.oficina_api.interfaces.dto.response.ServicoResponse;
import com.mecanica.oficina_api.interfaces.dto.response.TempoMedioServicoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.List;

@Service
public class ServicoService {

    private final ServicoSpringDataRepository repository;
    private final StatusServicoSpringDataRepository statusRepository;

    public ServicoService(ServicoSpringDataRepository repository, StatusServicoSpringDataRepository statusRepository) {
        this.repository = repository;
        this.statusRepository = statusRepository;
    }

    public ServicoResponse cadastrar(CadastrarServicoRequest request) {
        Servico servico = Servico.criar(request.getNome(), request.getDescricao(),
                request.getPreco(), Duration.ofHours(request.getTempoEstimadoHoras()));
        return toResponse(repository.save(toEntity(servico)));
    }

    public ServicoResponse buscar(String id) {
        return toResponse(encontrarOuLancar(id));
    }

    public TempoMedioServicoResponse buscarTempoMedio(String id) {
        var servicoEntity = encontrarOuLancar(id);

        var servicosFinalizados = statusRepository.findByServicoIdAndStatus(servicoEntity.getId(), ServicoStatus.FINALIZADO.toString());

        double mediaMinutos = servicosFinalizados.stream().mapToLong(servicoFinalizado ->
            Duration.between(servicoFinalizado.getDataInicio(), servicoFinalizado.getDataFim()).toMinutes()
        ).average().orElse(0);

        return TempoMedioServicoResponse.builder()
            .servicoId(id)
            .nome(servicoEntity.getNome())
            .tempoMedioEmMinutos(mediaMinutos)
            .build();
    }

    public List<ServicoResponse> listar() {
        return repository.findAllByAtivoTrue().stream().map(this::toResponse).toList();
    }

    public ServicoResponse alterar(String id, AlterarServicoRequest request) {
        ServicoJpaEntity entity = encontrarOuLancar(id);
        Servico servico = toDomain(entity);
        servico.atualizar(request.getNome(), request.getDescricao(),
                request.getPreco(), Duration.ofHours(request.getTempoEstimadoHoras()));
        entity.setNome(servico.getNome());
        entity.setDescricao(servico.getDescricao());
        entity.setPreco(servico.getPreco());
        entity.setTempoEstimadoHoras(servico.getTempoEstimadoHoras());
        return toResponse(repository.save(entity));
    }

    public ServicoResponse ativar(String id) {
        ServicoJpaEntity entity = encontrarOuLancar(id);
        entity.setAtivo(true);
        return toResponse(repository.save(entity));
    }

    public void inativar(String id) {
        ServicoJpaEntity entity = encontrarOuLancar(id);
        entity.setAtivo(false);
        repository.save(entity);
    }

    private ServicoJpaEntity encontrarOuLancar(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado: " + id));
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

    private Servico toDomain(ServicoJpaEntity e) {
        return Servico.reconstituir(e.getId(), e.getNome(), e.getDescricao(),
                e.getPreco(), e.getTempoEstimadoHoras(), e.isAtivo());
    }

    private ServicoResponse toResponse(ServicoJpaEntity e) {
        return new ServicoResponse(e.getId(), e.getNome(), e.getDescricao(),
                e.getPreco(), (int) e.getTempoEstimadoHoras().toHours(), e.isAtivo());
    }
}
