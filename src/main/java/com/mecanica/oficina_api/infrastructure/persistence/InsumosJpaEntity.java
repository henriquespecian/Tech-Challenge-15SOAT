package com.mecanica.oficina_api.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "insumos")
public class InsumosJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false)
  private String nome;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal precoUnitario;

  @Column(nullable = false)
  private Integer estoqueAtual;

  @Column(nullable = false)
  private Integer estoqueMinimo;

  @Column(nullable = false)
  private String unidade;

  @Column(nullable = false)
  @ColumnDefault("true")
  private Boolean ativo;

  public String getId() {return id;}
  public void setId(String id) {this.id = id;}
  public String getNome() {return nome;}
  public void setNome(String nome) {this.nome = nome;}
  public BigDecimal getPrecoUnitario() {return precoUnitario;}
  public void setPrecoUnitario(BigDecimal precoUnitario) {this.precoUnitario = precoUnitario;}
  public Integer getEstoqueAtual() {return estoqueAtual;}
  public void setEstoqueAtual(Integer estoqueAtual) {this.estoqueAtual = estoqueAtual;}
  public Integer getEstoqueMinimo() {return estoqueMinimo;}
  public void setEstoqueMinimo(Integer estoqueMinimo) {this.estoqueMinimo = estoqueMinimo;}
  public String getUnidade() {return unidade;}
  public void setUnidade(String unidade) {this.unidade = unidade;}
  public Boolean getAtivo() {return ativo;}
  public void setAtivo(Boolean ativo) {this.ativo = ativo;}
}
