package com.mecanica.oficina_api.adapters.web.presenter;

import com.mecanica.oficina_api.adapters.web.dto.response.VeiculoResponse;
import com.mecanica.oficina_api.domain.veiculo.Veiculo;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VeiculoPresenterTest {

    private final VeiculoPresenter presenter = new VeiculoPresenter();

    @Test
    void deveApresentarVeiculo_quandoEntidadeUnica() {
        Veiculo veiculo = Veiculo.reconstituir(
                "veiculo-1", "cliente-1", "ABC1D23", "Ford", "Ka", 2020, "Prata", true);

        VeiculoResponse response = presenter.apresentar(veiculo);

        assertThat(response.getId()).isEqualTo("veiculo-1");
        assertThat(response.getClienteId()).isEqualTo("cliente-1");
        assertThat(response.getPlaca()).isEqualTo("ABC1D23");
        assertThat(response.getMarca()).isEqualTo("Ford");
        assertThat(response.getModelo()).isEqualTo("Ka");
        assertThat(response.getAno()).isEqualTo(2020);
        assertThat(response.getCor()).isEqualTo("Prata");
    }

    @Test
    void deveApresentarListaDeVeiculos_quandoColecao() {
        Veiculo v1 = Veiculo.reconstituir(
                "veiculo-1", "cliente-1", "ABC1D23", "Ford", "Ka", 2020, "Prata", true);
        Veiculo v2 = Veiculo.reconstituir(
                "veiculo-2", "cliente-1", "XYZ9K88", "Fiat", "Uno", 2021, "Branco", true);

        List<VeiculoResponse> responses = presenter.apresentar(List.of(v1, v2));

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getId()).isEqualTo("veiculo-1");
        assertThat(responses.get(0).getPlaca()).isEqualTo("ABC1D23");
        assertThat(responses.get(1).getId()).isEqualTo("veiculo-2");
        assertThat(responses.get(1).getModelo()).isEqualTo("Uno");
    }

    @Test
    void deveApresentarListaVazia_quandoColecaoVazia() {
        List<VeiculoResponse> responses = presenter.apresentar(List.of());

        assertThat(responses).isEmpty();
    }
}
