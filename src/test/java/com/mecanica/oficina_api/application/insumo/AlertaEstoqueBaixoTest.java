package com.mecanica.oficina_api.application.insumo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

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
}
