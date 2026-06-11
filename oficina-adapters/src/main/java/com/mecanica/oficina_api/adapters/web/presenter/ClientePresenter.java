package com.mecanica.oficina_api.adapters.web.presenter;

import com.mecanica.oficina_api.adapters.web.dto.response.ConsultarClienteResponse;
import com.mecanica.oficina_api.domain.cliente.Cliente;
import org.springframework.stereotype.Component;

import java.util.List;

/** Converte a entidade de domínio {@link Cliente} em DTOs de resposta HTTP. */
@Component
public class ClientePresenter {

    public ConsultarClienteResponse apresentar(Cliente c) {
        return new ConsultarClienteResponse(
                c.getId(), c.getNome(), c.getDocumento().getValue(),
                c.getEmail().getValue(), c.getTelefone().getValue());
    }

    public List<ConsultarClienteResponse> apresentar(List<Cliente> clientes) {
        return clientes.stream().map(this::apresentar).toList();
    }
}
