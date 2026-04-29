package com.mecanica.oficina_api.domain.usuario;

import java.time.LocalDateTime;
import java.util.Objects;

public class Usuario {
    private String id;
    private String nome;
    private String email;
    private String senha;
    private Perfil perfil;
    private String clienteId;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;
    private boolean ativo;

    protected Usuario() {}

    public static Usuario criar(String nome, String email, String senha, Perfil perfil, String clienteId) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome é obrigatório");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email é obrigatório");
        if (senha == null || senha.isBlank()) throw new IllegalArgumentException("Senha é obrigatória");
        Objects.requireNonNull(perfil, "Perfil é obrigatório");

        Usuario usuario = new Usuario();
        usuario.nome = nome;
        usuario.email = email;
        usuario.senha = senha;
        usuario.perfil = perfil;
        usuario.clienteId = clienteId;
        usuario.dataCadastro = LocalDateTime.now();
        usuario.dataAtualizacao = LocalDateTime.now();
        usuario.ativo = true;
        return usuario;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public Perfil getPerfil() { return perfil; }
    public String getClienteId() { return clienteId; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public boolean getAtivo() { return ativo; }
}
