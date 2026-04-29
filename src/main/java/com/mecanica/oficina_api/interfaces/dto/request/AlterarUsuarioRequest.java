package com.mecanica.oficina_api.interfaces.dto.request;

public class AlterarUsuarioRequest {
    private String nome;
    private String email;
    private String perfil;
    private String clienteId;

    public AlterarUsuarioRequest() {}

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
}
