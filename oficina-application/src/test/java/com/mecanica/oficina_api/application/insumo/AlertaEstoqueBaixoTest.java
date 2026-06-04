package com.mecanica.oficina_api.application.insumo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.mecanica.oficina_api.application.insumo.output.AlertaEstoqueBaixo;

class AlertaEstoqueBaixoTest {

    @Test
    void deveEmitirQuandoSoAumentaMinimoEstoqueIgualAoMinimoAntigo() {
        assertThat(AlertaEstoqueBaixo.deveEmitirAlerta(5, 5, 5, 10)).isTrue();
    }

    @Test
    void deveEmitirQuandoEstoqueCruzaParaCriticoPorBaixa() {
        assertThat(AlertaEstoqueBaixo.deveEmitirAlerta(10, 2, 1, 2)).isTrue();
    }

    @Test
    void naoDeveEmitirQuandoJaCriticoESoBaixaMaisSemMudarMinimo() {
        assertThat(AlertaEstoqueBaixo.deveEmitirAlerta(2, 2, 1, 2)).isFalse();
    }

    @Test
    void naoDeveEmitirQuandoPermaneceAcimaDoNovoMinimo() {
        assertThat(AlertaEstoqueBaixo.deveEmitirAlerta(30, 5, 30, 10)).isFalse();
    }

    @Test
    void deveNotificarAlteracaoInsumoQuandoEstoqueIgualAoMinimo() {
        assertThat(AlertaEstoqueBaixo.deveNotificarAlteracaoInsumo(5, 5)).isTrue();
    }

    @Test
    void deveNotificarAlteracaoInsumoQuandoEstoqueAbaixoDoMinimo() {
        assertThat(AlertaEstoqueBaixo.deveNotificarAlteracaoInsumo(2, 10)).isTrue();
    }

    @Test
    void naoDeveNotificarAlteracaoInsumoQuandoEstoqueAcimaDoMinimo() {
        assertThat(AlertaEstoqueBaixo.deveNotificarAlteracaoInsumo(10, 5)).isFalse();
    }
}
