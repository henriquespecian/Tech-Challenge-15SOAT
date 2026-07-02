package com.mecanica.oficina_api.adapters.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AlterarClienteRequest {
  @NotBlank(message = "O nome é obrigatório")
  private String nome;

  @NotBlank(message = "O e-mail é obrigatório")
  @Email(message = "E-mail inválido")
  private String email;

  @NotBlank(message = "O telefone é obrigatório")
  private String telefone;

  public AlterarClienteRequest() {}

  public String getNome() { return nome; }
  public void setNome(String nome) { this.nome = nome; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getTelefone() { return telefone; }
  public void setTelefone(String telefone) { this.telefone = telefone; }

}
