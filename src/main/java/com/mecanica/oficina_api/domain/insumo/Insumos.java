package com.mecanica.oficina_api.domain.insumo;

import java.math.BigDecimal;
import java.util.Objects;

public class Insumos {

  private String id;
  private String nome;
  private BigDecimal precoUnitario;
  private Integer estoqueAtual;
  private Integer estoqueMinimo;
  private String unidade; // Faz sentido?
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

  public String getId() {return id;}
  public String getNome() {return nome;}
  public BigDecimal getPrecoUnitario() {return precoUnitario;}
  public Integer getEstoqueAtual() {return estoqueAtual;}
  public Integer getEstoqueMinimo() {return estoqueMinimo;}
  public String getUnidade() {return unidade;}
  public Boolean getAtivo() {return ativo;}
}
