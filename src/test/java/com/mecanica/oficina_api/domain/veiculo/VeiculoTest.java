package com.mecanica.oficina_api.domain.veiculo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class VeiculoTest {

    @Test
    void deveCriarVeiculoComDadosValidos() {
        Veiculo veiculo = Veiculo.criar("cliente-1", "ABC1234", "Toyota", "Corolla", 2020, "Branco");

        assertThat(veiculo.getClienteId()).isEqualTo("cliente-1");
        assertThat(veiculo.getPlaca()).isEqualTo("ABC1234");
        assertThat(veiculo.getMarca()).isEqualTo("Toyota");
        assertThat(veiculo.getModelo()).isEqualTo("Corolla");
        assertThat(veiculo.getAno()).isEqualTo(2020);
        assertThat(veiculo.getCor()).isEqualTo("Branco");
    }

    @Test
    void deveSalvarPlacaEmMaiusculas() {
        Veiculo veiculo = Veiculo.criar("cliente-1", "abc1234", "Honda", "Civic", 2021, "Preto");

        assertThat(veiculo.getPlaca()).isEqualTo("ABC1234");
    }

    @Test
    void deveLancarExcecaoQuandoPlacaForNula() {
        assertThatThrownBy(() -> Veiculo.criar("cliente-1", null, "Ford", "Ka", 2019, "Vermelho"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Placa é obrigatória");
    }

    @Test
    void deveLancarExcecaoQuandoPlacaForVazia() {
        assertThatThrownBy(() -> Veiculo.criar("cliente-1", "   ", "Ford", "Ka", 2019, "Vermelho"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Placa é obrigatória");
    }

    @Test
    void deveLancarExcecaoQuandoClienteIdForNulo() {
        assertThatThrownBy(() -> Veiculo.criar(null, "ABC1234", "Ford", "Ka", 2019, "Vermelho"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Cliente é obrigatório");
    }

    @Test
    void deveLancarExcecaoQuandoMarcaForNula() {
        assertThatThrownBy(() -> Veiculo.criar("cliente-1", "ABC1234", null, "Ka", 2019, "Vermelho"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Marca é obrigatória");
    }

    @Test
    void deveLancarExcecaoQuandoModeloForNulo() {
        assertThatThrownBy(() -> Veiculo.criar("cliente-1", "ABC1234", "Ford", null, 2019, "Vermelho"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Modelo é obrigatório");
    }
}
