package com.mecanica.oficina_api.application.cliente.usecase;

import java.util.List;

import com.mecanica.oficina_api.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_api.domain.cliente.Cliente;

public class ListarClientesUseCase {
    
    private final ClienteGateway gateway;

    public ListarClientesUseCase(ClienteGateway gateway) {
        this.gateway = gateway;
    }

    public List<Cliente> executar() {
        return gateway.findAllAtivos();
    }

}
