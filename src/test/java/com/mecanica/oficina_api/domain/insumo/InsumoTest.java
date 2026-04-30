package com.mecanica.oficina_api.domain.insumo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class InsumoTest {

  @Test
  void deveCriarInsumoComDadosValidados() {
    Insumos insumo = Insumos.criar("Fluido de arrefecimento", BigDecimal.valueOf(19.9), 10, 2, "Unidade");

    assertThat(insumo.getNome()).isEqualTo("Fluido de arrefecimento");
    assertThat(insumo.getPrecoUnitario()).isEqualTo(BigDecimal.valueOf(19.9));
    assertThat(insumo.getEstoqueAtual()).isEqualTo(10);
    assertThat(insumo.getEstoqueMinimo()).isEqualTo(2);
    assertThat(insumo.getUnidade()).isEqualTo("Unidade");
    assertThat(insumo.getAtivo()).isEqualTo(true);
  }

  @Test
  void deveLancarExcecaoQuandoPrecoForNegativo() {
    assertThatThrownBy(() -> Insumos.criar("Fluido de arrefecimento", BigDecimal.valueOf(-10.23), 10, 2, "Unidade"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Preço Unitário deve ser um número positivo");
  }

  @Test
  void deveLancarExcecaoQuandoEstoqueAtualForNegativo() {
    assertThatThrownBy(() -> Insumos.criar("Fluido de arrefecimento", BigDecimal.valueOf(19.9), -5, 2, "Unidade"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Estoque Atual deve ser um número inteiro positivo");
  }

  @Test
  void deveLancarExcecaoQuandoEstoqueMinimoForNegativo() {
    assertThatThrownBy(() -> Insumos.criar("Fluido de arrefecimento", BigDecimal.valueOf(19.9), 10, -10, "Unidade"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Estoque Mínimo deve ser um número inteiro positivo");
  }

  @Test
  void deveLancarExcecaoQuandoNomeForNulo() {
    assertThatThrownBy(() -> Insumos.criar(null, BigDecimal.valueOf(19.9), 10, 2, "Unidade"))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Nome é obrigatório");
  }

  @Test
  void deveLancarExcecaoQuandoUnidadeForNula() {
    assertThatThrownBy(() -> Insumos.criar("Fluido de arrefecimento", BigDecimal.valueOf(19.9), 10, 2, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Unidade é obrigatório");
  }
}
