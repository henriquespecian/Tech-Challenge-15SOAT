package com.mecanica.oficina_api.domain.cliente;

import static org.assertj.core.api.Assertions.*;

import com.mecanica.oficina_api.domain.cliente.model.Cliente;
import com.mecanica.oficina_api.domain.cliente.model.Cpf;
import com.mecanica.oficina_api.domain.cliente.model.Email;
import com.mecanica.oficina_api.domain.cliente.model.Telefone;
import org.junit.jupiter.api.Test;

public class ClienteTest {
  private final Cpf cpf;
  private final Email email;
  private final Telefone telefone;

  public ClienteTest() {
    this.cpf = new Cpf("37518712091");
    this.email = new Email("cliente@teste.com");
    this.telefone = new Telefone("5437891237");
  }

  @Test
  void deveCriarClienteComDadosValidos() {
    Cliente cliente = Cliente.criar("Cliente Novo", cpf, email, telefone);

    assertThat(cliente.getNome()).isEqualTo("Cliente Novo");
    assertThat(cliente.getCpf()).isEqualTo(cpf);
    assertThat(cliente.getTelefone()).isEqualTo(telefone);
  }

  @Test
  void deveAdicionarVeiculo() {
    Cliente cliente = Cliente.criar("Cliente Novo", cpf, email, telefone);
    cliente.adicionarVeiculo("veiculo-1");

    assertThat(cliente.getVeiculoIds()).containsExactly("veiculo-1");
  }

  @Test
  void deveLancarExcecaoQuandoVeiculoForNulo() {
    Cliente cliente = Cliente.criar("Cliente Novo", cpf, email, telefone);

    assertThatThrownBy(() -> cliente.adicionarVeiculo(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void deveLancarExcecaoQuandoNomeForNulo() {
    assertThatThrownBy(() -> Cliente.criar(null , cpf, email, telefone))
        .isInstanceOf(NullPointerException.class).hasMessage("Nome é obrigatório");
  }

  @Test
  void deveLancarExcecaoQuandoCpfForNulo() {
    assertThatThrownBy(() -> Cliente.criar("Cliente Novo" , null, email, telefone))
        .isInstanceOf(NullPointerException.class).hasMessage("CPF é obrigatório");
  }

  @Test
  void deveLancarExcecaoQuandoCpfForInvalido() {
    String cpf = "123.xxx.345-Y";

    assertThatThrownBy(() -> new Cpf(cpf))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("CPF inválido: " + cpf);
  }

  @Test
  void deveLancarExcecaoQuandoEmailForNulo() {
    assertThatThrownBy(() -> Cliente.criar("Cliente Novo" , cpf, null, telefone))
        .isInstanceOf(NullPointerException.class).hasMessage("Email é obrigatório");
  }

  @Test
  void deveLancarExcecaoQuandoEmailForInvalido() {
    String email = "teste.123@123";
    assertThatThrownBy(() -> new Email(email))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Email inválido: " + email);
  }

  @Test
  void deveLancarExcecaoQuandoTelefoneForNulo() {
    assertThatThrownBy(() -> Cliente.criar("Cliente Novo" , cpf, email, null))
        .isInstanceOf(NullPointerException.class).hasMessage("Telefone é obrigatório");
  }

  @Test
  void deveLancarExcecaoQuandoTelefoneForInvalido() {
    String telefone = "123";
    assertThatThrownBy(() -> new Telefone(telefone))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Telefone inválido: " + telefone);

  }
}
