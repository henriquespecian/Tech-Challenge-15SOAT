package com.mecanica.oficina_api.application.usuario.usecase;

import com.mecanica.oficina_api.application.usuario.gateway.PasswordEncoder;
import com.mecanica.oficina_api.application.usuario.gateway.UsuarioGateway;
import com.mecanica.oficina_api.domain.usuario.Perfil;
import com.mecanica.oficina_api.domain.usuario.Usuario;

public class CadastrarUsuarioUseCase {

    private final UsuarioGateway usuarioGateway;
    private final PasswordEncoder passwordEncoder;

    public CadastrarUsuarioUseCase(UsuarioGateway usuarioGateway, PasswordEncoder passwordEncoder) {
        this.usuarioGateway = usuarioGateway;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario executar(String nome, String email, String senha, String perfilStr, String clienteId) {
        if (usuarioGateway.existePorEmail(email)) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        Perfil perfil = Perfil.fromString(perfilStr);
        String senhaHash = passwordEncoder.encode(senha);
        Usuario usuario = Usuario.criar(nome, email, senhaHash, perfil, clienteId);

        return usuarioGateway.cadastrar(usuario);
    }
}
