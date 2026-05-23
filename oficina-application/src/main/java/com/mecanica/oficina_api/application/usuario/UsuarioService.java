package com.mecanica.oficina_api.application.usuario;

import com.mecanica.oficina_api.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_api.application.usuario.gateway.UsuarioGateway;
import com.mecanica.oficina_api.domain.usuario.Perfil;
import com.mecanica.oficina_api.domain.usuario.Usuario;

public class UsuarioService {

    private final UsuarioGateway usuarioGateway;
    private final ClienteGateway clienteGateway;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioGateway usuarioGateway,
                          ClienteGateway clienteGateway,
                          PasswordEncoder passwordEncoder) {
        this.usuarioGateway = usuarioGateway;
        this.clienteGateway = clienteGateway;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario cadastrar(String nome, String email, String senha, String perfilStr, String clienteId) {
        if (usuarioGateway.existePorEmail(email)) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        Perfil perfil = parsePerfil(perfilStr);
        validarClienteId(perfil, clienteId);

        String senhaHash = passwordEncoder.encode(senha);
        Usuario usuario = Usuario.criar(nome, email, senhaHash, perfil, clienteId);

        return usuarioGateway.cadastrar(usuario);
    }

    public Usuario buscar(String id) {
        return usuarioGateway.buscar(id).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    public Usuario alterar(String id, String nome, String email, String perfilStr, String clienteId) {
        Usuario existente = usuarioGateway.buscar(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Perfil perfil = parsePerfil(perfilStr);
        validarClienteId(perfil, clienteId);

        return usuarioGateway.alterar(id, Usuario.reconstituir(id, nome, email, existente.getSenha(), perfil));
    }

    public void deletar(String id) {
        usuarioGateway.buscar(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        usuarioGateway.inativar(id);
    }

    private Perfil parsePerfil(String perfil) {
        try {
            return Perfil.valueOf(perfil);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Perfil inválido. Valores aceitos: ADMIN, MECANICO, CLIENTE, ATENDENTE");
        }
    }

    private void validarClienteId(Perfil perfil, String clienteId) {
        if (perfil != Perfil.CLIENTE) return;
        if (clienteId == null || clienteId.isBlank()) {
            throw new IllegalArgumentException("clienteId é obrigatório para perfil CLIENTE");
        }

        clienteGateway.findByIdAtivo(clienteId)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
    }
}
