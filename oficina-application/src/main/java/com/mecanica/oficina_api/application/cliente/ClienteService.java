package com.mecanica.oficina_api.application.cliente;

import com.mecanica.oficina_api.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_api.domain.cliente.Cliente;
import com.mecanica.oficina_api.domain.cliente.Documento;
import com.mecanica.oficina_api.domain.cliente.Email;
import com.mecanica.oficina_api.domain.cliente.Telefone;

import java.util.List;
import java.util.Optional;


public class ClienteService {

    private final ClienteGateway gateway;

    public ClienteService(ClienteGateway gateway) {
        this.gateway = gateway;
    }

    public Cliente cadastrar(String nome, String documento, String email, String telefone) {
        Documento doc = Documento.parse(documento);
        if (gateway.existsByDocumento(doc.getValue())) {
            throw new IllegalStateException("Documento já cadastrado");
        }
        Cliente cliente = Cliente.criar(nome, doc, new Email(email), new Telefone(telefone));
        return gateway.save(cliente);
    }

    public Optional<Cliente> consultar(String documento) {
        Documento doc = Documento.parse(documento);
        return gateway.findByDocumentoAtivo(doc.getValue());
    }

    public List<Cliente> listar() {
        return gateway.findAllAtivos();
    }

    public void alterar(String documento, String nome, String email, String telefone) {
        Documento doc = Documento.parse(documento);
        Cliente existente = gateway.findByDocumentoAtivo(doc.getValue())
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        existente.alterar(nome, doc, new Email(email), new Telefone(telefone));
        gateway.save(existente);
    }

    public void deletar(String documento) {
        Documento doc = Documento.parse(documento);
        gateway.softDelete(doc.getValue());
    }
}
