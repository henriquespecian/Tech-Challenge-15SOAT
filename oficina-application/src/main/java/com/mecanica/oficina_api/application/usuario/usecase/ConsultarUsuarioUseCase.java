package com.mecanica.oficina_api.application.usuario.usecase;

import com.mecanica.oficina_api.application.usuario.gateway.UsuarioGateway;
import com.mecanica.oficina_api.domain.usuario.Usuario;

public class ConsultarUsuarioUseCase {
    private final UsuarioGateway usuarioGateway;

    public ConsultarUsuarioUseCase(UsuarioGateway usuarioGateway) {
        this.usuarioGateway = usuarioGateway;
    }

    public Usuario executar(String id) {
        return usuarioGateway.buscarOuFalhar(id);
    }
}
