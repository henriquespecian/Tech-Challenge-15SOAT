package com.mecanica.oficina_api.application.cliente.usecase;

import com.mecanica.oficina_api.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_api.domain.cliente.Cliente;
import com.mecanica.oficina_api.domain.cliente.Documento;
import com.mecanica.oficina_api.domain.cliente.Email;
import com.mecanica.oficina_api.domain.cliente.Telefone;

public class CadastrarClienteUseCase {
    private final ClienteGateway gateway;

    public CadastrarClienteUseCase(ClienteGateway gateway) {
        this.gateway = gateway;
    }

    public Cliente executar(String nome, String documento, String email, String telefone) {
        Documento doc = Documento.parse(documento);
        if (gateway.existsByDocumento(doc.getValue())) {
            throw new IllegalStateException("Documento já cadastrado");
        }
        Cliente cliente = Cliente.criar(nome, doc, new Email(email), new Telefone(telefone));
        return gateway.save(cliente);
    }
}
