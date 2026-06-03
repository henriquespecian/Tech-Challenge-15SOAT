package com.mecanica.oficina_api.application.usuario.gateway;

import java.util.Optional;

import com.mecanica.oficina_api.domain.usuario.Usuario;

public interface UsuarioGateway {
    Optional<Usuario> buscar(String id);

    default Usuario buscarOuFalhar(String id) {
        return buscar(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    boolean existePorEmail(String email);
    Usuario cadastrar(Usuario request);
    void inativar(String id);
    Usuario alterar(String id, Usuario request);
}
