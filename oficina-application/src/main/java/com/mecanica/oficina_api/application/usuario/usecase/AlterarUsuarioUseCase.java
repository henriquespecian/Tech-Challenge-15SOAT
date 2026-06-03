package com.mecanica.oficina_api.application.usuario.usecase;

import com.mecanica.oficina_api.application.usuario.gateway.UsuarioGateway;
import com.mecanica.oficina_api.domain.usuario.Perfil;
import com.mecanica.oficina_api.domain.usuario.Usuario;

public class AlterarUsuarioUseCase {
    private final UsuarioGateway usuarioGateway;

    public AlterarUsuarioUseCase(UsuarioGateway usuarioGateway) {
        this.usuarioGateway = usuarioGateway;
    }

    public Usuario executar(String id, String nome, String email, String perfilStr, String clienteId) {
        Usuario existente = usuarioGateway.buscarOuFalhar(id);

        Perfil perfil = Perfil.fromString(perfilStr);
        return usuarioGateway.alterar(id, Usuario.reconstituir(id, nome, email, existente.getSenha(), perfil, clienteId));
    }
}
