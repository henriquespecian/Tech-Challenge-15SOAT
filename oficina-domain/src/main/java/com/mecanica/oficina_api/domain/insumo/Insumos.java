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

  public static Insumos reconstituir(String id, String nome, BigDecimal precoUnitario,  Integer estoqueAtual, Integer estoqueMinimo, String unidade) {
    Insumos insumos = new Insumos();

    insumos.id = Objects.requireNonNull(id, "ID é obrigatório");
    insumos.nome = Objects.requireNonNull(nome, "Nome é obrigatório");
    insumos.setPrecoUnitario(precoUnitario);
    insumos.setEstoqueAtual(estoqueAtual);
    insumos.setEstoqueMinimo(estoqueMinimo);
    insumos.unidade = Objects.requireNonNull(unidade, "Unidade é obrigatório");

    return insumos;
  }

  private void setPrecoUnitario(BigDecimal precoUnitario) {
    if(Objects.isNull(precoUnitario) || precoUnitario.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Preço Unitário deve ser um número positivo");
    }

    this.precoUnitario = precoUnitario;
  }

  public void setEstoqueAtual(Integer estoqueAtual) {
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

  /**
   * Na alteração manual do insumo (cadastro), emite sempre que após salvar o estoque
   * estiver na zona crítica ({@code estoque <= mínimo}), independentemente de qual campo foi mudado.
   */
  public static boolean deveNotificarAlteracaoInsumo(int estoqueAtual, int estoqueMinimo) {
      return estoqueAtual <= estoqueMinimo;
  }

  /**
   * Para baixa por ordem de serviço: emite quando o par (estoque, mínimo) fica na zona crítica ({@code estoque <= mínimo})
   * e pelo menos uma destas situações ocorre:
   * <ul>
   *   <li>o estoque estava estritamente acima do mínimo antigo (baixa típica ou ajuste de quantidade);</li>
   *   <li>o mínimo foi aumentado e o estoque atual fica igual ou abaixo do novo mínimo
   *       (ex.: estava 5 com mínimo 5 e só sobe o mínimo para 10).</li>
   * </ul>
   * Não emite quando já era crítico com os mesmos limites e só baixa mais estoque (evita spam).
   */
  public static boolean deveEmitirAlerta(
          int estoqueAnterior,
          int estoqueMinimoAnterior,
          int estoqueAtual,
          int estoqueMinimoAtual) {
      boolean agoraCritico = estoqueAtual <= estoqueMinimoAtual;

      if (!agoraCritico) {
          return false;
      }

      boolean estavaEstritamenteAcimaDoMinimoAntigo = estoqueAnterior > estoqueMinimoAnterior;
      boolean minimoAumentou = estoqueMinimoAtual > estoqueMinimoAnterior;
      return estavaEstritamenteAcimaDoMinimoAntigo || minimoAumentou;
  }
}
