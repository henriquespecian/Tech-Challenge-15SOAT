package com.mecanica.oficina_api.interfaces.dto.response;

import com.mecanica.oficina_api.domain.usuario.Perfil;
import java.time.LocalDateTime;

public class UsuarioResponse {
    private String id;
    private String nome;
    private String email;
    private Perfil perfil;
    private String clienteId;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;

    public UsuarioResponse() {}

    public UsuarioResponse(String id, String nome, String email, Perfil perfil, String clienteId,
                           LocalDateTime dataCadastro, LocalDateTime dataAtualizacao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.perfil = perfil;
        this.clienteId = clienteId;
        this.dataCadastro = dataCadastro;
        this.dataAtualizacao = dataAtualizacao;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public Perfil getPerfil() { return perfil; }
    public String getClienteId() { return clienteId; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
}
