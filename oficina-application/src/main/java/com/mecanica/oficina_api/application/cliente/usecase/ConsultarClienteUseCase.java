package com.mecanica.oficina_api.application.cliente.usecase;

import java.util.Optional;

import com.mecanica.oficina_api.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_api.domain.cliente.Cliente;
import com.mecanica.oficina_api.domain.cliente.Documento;

public class ConsultarClienteUseCase {
    
    private final ClienteGateway gateway;

    public ConsultarClienteUseCase(ClienteGateway gateway) {
        this.gateway = gateway;
    }

    public Optional<Cliente> executar(String documento) {
        Documento doc = Documento.parse(documento);
        return gateway.findByDocumentoAtivo(doc.getValue());
    }
}
