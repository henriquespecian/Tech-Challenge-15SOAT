package com.mecanica.oficina_api.application.insumo;

import com.mecanica.oficina_api.application.insumo.gateway.InsumosGateway;
import com.mecanica.oficina_api.domain.insumo.Insumos;

import java.math.BigDecimal;
import java.util.List;

public class InsumosService {

  private final InsumosGateway insumosGateway;
  private final NotificadorEstoqueBaixo notificadorEstoqueBaixo;

  public InsumosService(InsumosGateway insumosGateway,
      NotificadorEstoqueBaixo notificadorEstoqueBaixo) {
    this.insumosGateway = insumosGateway;
    this.notificadorEstoqueBaixo = notificadorEstoqueBaixo;
  }

  public Insumos cadastrar(String nome, BigDecimal precoUnitario,  Integer estoqueAtual, Integer estoqueMinimo, String unidade) {

    if(insumosGateway.existePorNome(nome)){
      throw new IllegalArgumentException("O Insumo "+ nome +" já está cadastrado");
    }

    Insumos insumo = Insumos.criar(nome, precoUnitario, estoqueAtual, estoqueMinimo, unidade);

    return insumosGateway.criar(insumo);
  }

  public Insumos buscarPorId(String id) {
    return insumosGateway.buscar(id).orElseThrow(() -> new IllegalArgumentException("Insumo não encontrado"));
  }

  public List<Insumos> listar() {    
    return insumosGateway.listar();
  }

  public void atualizar(String id, String nome, BigDecimal precoUnitario,  Integer estoqueAtual, Integer estoqueMinimo, String unidade) {

    if(!insumosGateway.existePorId(id)) {
      throw new IllegalArgumentException("Insumo não encontrado");
    }

    int estoqueAnterior = insumosGateway.obterEstoqueAtual(id);

    var insumos = Insumos.reconstituir(
        id,
        nome,
        precoUnitario,
        estoqueAtual,
        estoqueMinimo,
        unidade
    );

    insumosGateway.alterar(id, insumos);

    if (AlertaEstoqueBaixo.deveNotificarAlteracaoInsumo(estoqueAtual, estoqueMinimo)){
      notificadorEstoqueBaixo.notificar(new AlertaEstoqueBaixo(
          id,
          nome,
          estoqueAnterior,
          estoqueAtual,
          estoqueMinimo,
          OrigemNotificacaoEstoque.ALTERACAO_INSUMO,
          id));

    }    
  }

  public Insumos ativar(String id) {
    insumosGateway.buscar(id).orElseThrow(() -> new IllegalArgumentException("Insumo não encontrado"));
    return insumosGateway.ativar(id);
  }

  public void deletar(String id) {
    insumosGateway.buscar(id).orElseThrow(() -> new IllegalArgumentException("Insumo não encontrado"));
    insumosGateway.inativar(id);
  }

  /**
   * Simula conclusão de compra/entrega: incrementa o estoque sem integração externa.
   */
  public Insumos registrarCompraSimulada(String id, Integer quantidade) {
    if (quantidade == null || quantidade <= 0) {
      throw new IllegalArgumentException("Quantidade deve ser informada e ser um inteiro positivo");
    }
    var entity = insumosGateway.buscar(id)
        .orElseThrow(() -> new IllegalArgumentException("Insumo não encontrado"));

    long novoEstoque = (long) entity.getEstoqueAtual() + quantidade;

    if (novoEstoque > Integer.MAX_VALUE) {
      throw new IllegalArgumentException("Quantidade excede o limite permitido para o estoque");
    }

    return insumosGateway.atualizaEstoque(id, (int) novoEstoque);
  }
}
