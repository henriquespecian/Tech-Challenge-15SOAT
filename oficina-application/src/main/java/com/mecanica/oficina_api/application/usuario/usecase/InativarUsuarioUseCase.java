package com.mecanica.oficina_api.application.usuario.usecase;

import com.mecanica.oficina_api.application.usuario.gateway.UsuarioGateway;

public class InativarUsuarioUseCase {
    private final UsuarioGateway usuarioGateway;

    public InativarUsuarioUseCase(UsuarioGateway usuarioGateway) {
        this.usuarioGateway = usuarioGateway;
    }

    public void executar(String id) {
        usuarioGateway.buscarOuFalhar(id);
        usuarioGateway.inativar(id);
    }
}
