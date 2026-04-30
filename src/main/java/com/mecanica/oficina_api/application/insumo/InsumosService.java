package com.mecanica.oficina_api.application.insumo;

import com.mecanica.oficina_api.domain.insumo.Insumos;
import com.mecanica.oficina_api.infrastructure.persistence.InsumosJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.repository.InsumosSpringDataRepository;
import com.mecanica.oficina_api.interfaces.dto.request.AlterarInsumosRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarInsumosRequest;
import com.mecanica.oficina_api.interfaces.dto.response.InsumosResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class InsumosService {

  private final InsumosSpringDataRepository insumosSpringDataRepository;

  public InsumosService(InsumosSpringDataRepository insumosSpringDataRepository) {
    this.insumosSpringDataRepository = insumosSpringDataRepository;
  }

  public void cadastrar(CadastrarInsumosRequest request) {
    insumosSpringDataRepository.findByNome(request.getNome()).ifPresent(insumosJpaEntity -> {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "O Insumo "+ request.getNome() +" já está cadastrado");
    });

    Insumos insumos = Insumos.criar(
        request.getNome(),
        request.getPrecoUnitario(),
        request.getEstoqueAtual(),
        request.getEstoqueMinimo(),
        request.getUnidade()
    );

    InsumosJpaEntity entity = new InsumosJpaEntity();

    entity.setNome(insumos.getNome());
    entity.setPrecoUnitario(insumos.getPrecoUnitario());
    entity.setEstoqueAtual(insumos.getEstoqueAtual());
    entity.setEstoqueMinimo(insumos.getEstoqueMinimo());
    entity.setUnidade(insumos.getUnidade());
    entity.setAtivo(insumos.getAtivo());

    insumosSpringDataRepository.save(entity);
  }

  public List<InsumosResponse> listar() {
    var entities = insumosSpringDataRepository.findAllByAtivoTrue();

    List<InsumosResponse> response_list = entities.stream().map((entity) -> {
        return new InsumosResponse(
            entity.getNome(),
            entity.getPrecoUnitario(),
            entity.getEstoqueAtual(),
            entity.getEstoqueMinimo(),
            entity.getUnidade(),
            entity.getAtivo()
        );
    }).toList();

    return response_list;
  }

  public void atualizar(String id, AlterarInsumosRequest request) {
    var entity = insumosSpringDataRepository.findByIdAndAtivoTrue(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Insumo não encontrado"));

    var insumos = Insumos.criar(
        request.getNome(),
        request.getPrecoUnitario(),
        request.getEstoqueAtual(),
        request.getEstoqueMinimo(),
        request.getUnidade()
    );

    entity.setNome(insumos.getNome());
    entity.setPrecoUnitario(insumos.getPrecoUnitario());
    entity.setEstoqueAtual(insumos.getEstoqueAtual());
    entity.setEstoqueMinimo(insumos.getEstoqueMinimo());
    entity.setUnidade(insumos.getUnidade());

    insumosSpringDataRepository.save(entity);
  }

  public void deletar(String id) {
    var entity = insumosSpringDataRepository.findById(id);

    if(entity.isPresent()) {
      entity.get().setAtivo(false);
      insumosSpringDataRepository.save(entity.get());
    }
  }
}
