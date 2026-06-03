package com.mecanica.oficina_api.application.cliente.gateway;

import java.util.List;
import java.util.Optional;

import com.mecanica.oficina_api.domain.cliente.Cliente;

public interface ClienteGateway {
    boolean existsByDocumento(String documento);
    Optional<Cliente> findByDocumentoAtivo(String documento);

    default Cliente buscarPorDocumentoOuFalhar(String documento) {
        return findByDocumentoAtivo(documento)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
    }

    List<Cliente> findAllAtivos();
    Cliente save(Cliente cliente);
    void softDelete(String documento);
    Optional<Cliente> findByIdAtivo(String id);
}