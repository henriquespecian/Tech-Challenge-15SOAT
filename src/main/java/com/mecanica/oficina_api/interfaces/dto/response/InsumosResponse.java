package com.mecanica.oficina_api.interfaces.dto.response;

import java.math.BigDecimal;

public class InsumosResponse {

  private String nome;
  private BigDecimal precoUnitario;
  private Integer estoqueAtual;
  private Integer estoqueMinimo;
  private String unidade;
  private Boolean ativo;

  public InsumosResponse() {}

  public InsumosResponse(String nome, BigDecimal precoUnitario, Integer estoqueAtual, Integer estoqueMinimo, String unidade, Boolean ativo) {
    this.nome = nome;
    this.precoUnitario = precoUnitario;
    this.estoqueAtual = estoqueAtual;
    this.estoqueMinimo = estoqueMinimo;
    this.unidade = unidade;
    this.ativo = ativo;
  }

  public String getNome() {return nome;}
  public BigDecimal getPrecoUnitario() {return precoUnitario;}
  public Integer getEstoqueAtual() {return estoqueAtual;}
  public Integer getEstoqueMinimo() {return estoqueMinimo;}
  public String getUnidade() {return unidade;}
  public Boolean getAtivo() {return ativo;}
}
