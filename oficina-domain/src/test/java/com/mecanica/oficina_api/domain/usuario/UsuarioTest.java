package com.mecanica.oficina_api.domain.usuario;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UsuarioTest {

    @Test
    void deveCriarUsuarioComDadosValidos() {
        Usuario usuario = Usuario.criar("João Silva", "joao@email.com", "hashSenha", Perfil.ADMIN, null);

        assertThat(usuario.getNome()).isEqualTo("João Silva");
        assertThat(usuario.getEmail()).isEqualTo("joao@email.com");
        assertThat(usuario.getSenha()).isEqualTo("hashSenha");
        assertThat(usuario.getPerfil()).isEqualTo(Perfil.ADMIN);
        assertThat(usuario.getClienteId()).isNull();
        assertThat(usuario.getAtivo()).isTrue();
        assertThat(usuario.getDataCadastro()).isNotNull();
        assertThat(usuario.getDataAtualizacao()).isNotNull();
    }

    @Test
    void deveCriarUsuarioComClienteId() {
        Usuario usuario = Usuario.criar("Maria", "maria@email.com", "hash", Perfil.CLIENTE, "cliente-1");

        assertThat(usuario.getPerfil()).isEqualTo(Perfil.CLIENTE);
        assertThat(usuario.getClienteId()).isEqualTo("cliente-1");
    }

    @Test
    void deveLancarExcecaoQuandoNomeForNulo() {
        assertThatThrownBy(() -> Usuario.criar(null, "email@email.com", "hash", Perfil.ADMIN, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Nome é obrigatório");
    }

    @Test
    void deveLancarExcecaoQuandoNomeForVazio() {
        assertThatThrownBy(() -> Usuario.criar("  ", "email@email.com", "hash", Perfil.ADMIN, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Nome é obrigatório");
    }

    @Test
    void deveLancarExcecaoQuandoEmailForNulo() {
        assertThatThrownBy(() -> Usuario.criar("João", null, "hash", Perfil.ADMIN, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Email é obrigatório");
    }

    @Test
    void deveLancarExcecaoQuandoEmailForVazio() {
        assertThatThrownBy(() -> Usuario.criar("João", "  ", "hash", Perfil.ADMIN, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Email é obrigatório");
    }

    @Test
    void deveLancarExcecaoQuandoSenhaForNula() {
        assertThatThrownBy(() -> Usuario.criar("João", "joao@email.com", null, Perfil.ADMIN, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Senha é obrigatória");
    }

    @Test
    void deveLancarExcecaoQuandoSenhaForVazia() {
        assertThatThrownBy(() -> Usuario.criar("João", "joao@email.com", "  ", Perfil.ADMIN, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Senha é obrigatória");
    }

    @Test
    void deveLancarExcecaoQuandoPerfilForNulo() {
        assertThatThrownBy(() -> Usuario.criar("João", "joao@email.com", "hash", null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("Perfil é obrigatório");
    }
}
