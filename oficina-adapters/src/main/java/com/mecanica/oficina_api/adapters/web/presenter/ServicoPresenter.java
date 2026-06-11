package com.mecanica.oficina_api.adapters.web.presenter;

import com.mecanica.oficina_api.adapters.web.dto.response.ServicoResponse;
import com.mecanica.oficina_api.adapters.web.dto.response.TempoMedioServicoResponse;
import com.mecanica.oficina_api.domain.servico.Servico;
import com.mecanica.oficina_api.domain.servico.TempoMedioServico;
import org.springframework.stereotype.Component;

import java.util.List;

/** Converte as entidades de domínio {@link Servico} e {@link TempoMedioServico} em DTOs de resposta HTTP. */
@Component
public class ServicoPresenter {

    public ServicoResponse apresentar(Servico s) {
        return new ServicoResponse(
                s.getId(), s.getNome(), s.getDescricao(), s.getPreco(),
                (int) s.getTempoEstimadoHoras().toHours(), s.isAtivo());
    }

    public List<ServicoResponse> apresentar(List<Servico> servicos) {
        return servicos.stream().map(this::apresentar).toList();
    }

    public TempoMedioServicoResponse apresentarTempoMedio(TempoMedioServico tempoMedio) {
        return new TempoMedioServicoResponse(
                tempoMedio.servicoId(), tempoMedio.nome(), tempoMedio.tempoMedioEmMinutos());
    }
}
