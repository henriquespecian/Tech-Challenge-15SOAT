package com.mecanica.oficina_api.application.usuario;

import com.mecanica.oficina_api.application.usuario.gateway.PasswordEncoder;
import com.mecanica.oficina_api.application.usuario.gateway.UsuarioGateway;
import com.mecanica.oficina_api.domain.usuario.Perfil;
import com.mecanica.oficina_api.domain.usuario.Usuario;

public class UsuarioService {

    private final UsuarioGateway usuarioGateway;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioGateway usuarioGateway,
                          PasswordEncoder passwordEncoder) {
        this.usuarioGateway = usuarioGateway;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario cadastrar(String nome, String email, String senha, String perfilStr, String clienteId) {
        if (usuarioGateway.existePorEmail(email)) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        Perfil perfil = Perfil.fromString(perfilStr);
        String senhaHash = passwordEncoder.encode(senha);
        Usuario usuario = Usuario.criar(nome, email, senhaHash, perfil, clienteId);

        return usuarioGateway.cadastrar(usuario);
    }

    public Usuario buscar(String id) {
        return usuarioGateway.buscarOuFalhar(id);
    }

    public Usuario alterar(String id, String nome, String email, String perfilStr, String clienteId) {
        Usuario existente = usuarioGateway.buscarOuFalhar(id);

        Perfil perfil = Perfil.fromString(perfilStr);
        return usuarioGateway.alterar(id, Usuario.reconstituir(id, nome, email, existente.getSenha(), perfil, clienteId));
    }

    public void deletar(String id) {
        usuarioGateway.buscarOuFalhar(id);
        usuarioGateway.inativar(id);
    }
}
