package com.mecanica.oficina_api.adapters.web.presenter;

import com.mecanica.oficina_api.adapters.web.dto.response.VeiculoResponse;
import com.mecanica.oficina_api.domain.veiculo.Veiculo;
import org.springframework.stereotype.Component;

import java.util.List;

/** Converte a entidade de domínio {@link Veiculo} em DTOs de resposta HTTP. */
@Component
public class VeiculoPresenter {

    public VeiculoResponse apresentar(Veiculo v) {
        return new VeiculoResponse(v.getId(), v.getClienteId(), v.getPlaca(), v.getMarca(), v.getModelo(), v.getAno(), v.getCor());
    }

    public List<VeiculoResponse> apresentar(List<Veiculo> veiculos) {
        return veiculos.stream().map(this::apresentar).toList();
    }
}
