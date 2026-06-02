package com.mecanica.oficina_api.application.cliente.usecase;

import com.mecanica.oficina_api.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_api.domain.cliente.Documento;

public class DeletarClienteUseCase {
    private final ClienteGateway gateway;

    public DeletarClienteUseCase(ClienteGateway gateway) {
        this.gateway = gateway;
    }

    public void executar(String documento) {
        Documento doc = Documento.parse(documento);
        gateway.softDelete(doc.getValue());
    }

}
