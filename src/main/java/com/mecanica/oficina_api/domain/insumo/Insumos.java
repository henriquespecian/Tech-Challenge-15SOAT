package com.mecanica.oficina_api.domain.insumo;

import java.math.BigDecimal;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Insumos {

  private String id;
  private String nome;
  private BigDecimal precoUnitario;
  private Integer estoqueAtual;
  private Integer estoqueMinimo;
  private String unidade;
  private Boolean ativo;

  protected Insumos() {}

  public static Insumos criar(String nome, BigDecimal precoUnitario,  Integer estoqueAtual, Integer estoqueMinimo, String unidade) {
    Insumos insumos = new Insumos();

    insumos.nome = Objects.requireNonNull(nome, "Nome é obrigatório");
    insumos.setPrecoUnitario(precoUnitario);
    insumos.setEstoqueAtual(estoqueAtual);
    insumos.setEstoqueMinimo(estoqueMinimo);
    insumos.unidade = Objects.requireNonNull(unidade, "Unidade é obrigatório");
    insumos.ativo = true;

    return insumos;
  }

  private void setPrecoUnitario(BigDecimal precoUnitario) {
    if(Objects.isNull(precoUnitario) || precoUnitario.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Preço Unitário deve ser um número positivo");
    }

    this.precoUnitario = precoUnitario;
  }

  private void setEstoqueAtual(Integer estoqueAtual) {
    if(Objects.isNull(estoqueAtual) || estoqueAtual < 0) {
      throw new IllegalArgumentException("Estoque Atual deve ser um número inteiro positivo");
    }

    this.estoqueAtual = estoqueAtual;
  }

  private void setEstoqueMinimo(Integer estoqueMinimo) {
    if(Objects.isNull(estoqueMinimo) || estoqueMinimo < 0) {
      throw new IllegalArgumentException("Estoque Mínimo deve ser um número inteiro positivo");
    }

    this.estoqueMinimo = estoqueMinimo;
  }

  public void darBaixa(int quantidade) {
    if (quantidade <= 0)
      throw new IllegalArgumentException("Quantidade para baixa deve ser positiva");
    if (this.estoqueAtual - quantidade < 0)
      throw new IllegalStateException("Estoque insuficiente para o insumo: " + nome);
    this.estoqueAtual -= quantidade;
  }
}
