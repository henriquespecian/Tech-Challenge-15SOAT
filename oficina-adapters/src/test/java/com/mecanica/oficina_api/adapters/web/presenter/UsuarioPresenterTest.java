package com.mecanica.oficina_api.adapters.web.presenter;

import com.mecanica.oficina_api.adapters.web.dto.response.UsuarioResponse;
import com.mecanica.oficina_api.domain.usuario.Perfil;
import com.mecanica.oficina_api.domain.usuario.Usuario;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioPresenterTest {

    private final UsuarioPresenter presenter = new UsuarioPresenter();

    @Test
    void deveMapearTodosOsCamposDoUsuario() {
        Usuario usuario = Usuario.reconstituir(
                "usuario-1", "Ana Souza", "ana@email.com",
                "hash-secreto", Perfil.ADMIN, null);

        UsuarioResponse response = presenter.apresentar(usuario);

        assertThat(response.getId()).isEqualTo("usuario-1");
        assertThat(response.getNome()).isEqualTo("Ana Souza");
        assertThat(response.getEmail()).isEqualTo("ana@email.com");
        assertThat(response.getPerfil()).isEqualTo(Perfil.ADMIN);
        assertThat(response.getClienteId()).isNull();
        // reconstituir não popula datas: presenter apenas repassa o que existe na entidade
        assertThat(response.getDataCadastro()).isNull();
        assertThat(response.getDataAtualizacao()).isNull();
    }

    @Test
    void deveMapearClienteId_quandoPerfilCliente() {
        Usuario usuario = Usuario.reconstituir(
                "usuario-2", "Cliente Final", "cliente@email.com",
                "hash-secreto", Perfil.CLIENTE, "cliente-99");

        UsuarioResponse response = presenter.apresentar(usuario);

        assertThat(response.getPerfil()).isEqualTo(Perfil.CLIENTE);
        assertThat(response.getClienteId()).isEqualTo("cliente-99");
    }

    @Test
    void naoDeveExporSenhaNaResponse() {
        boolean possuiGetterDeSenha = false;
        for (Method method : UsuarioResponse.class.getMethods()) {
            String nome = method.getName().toLowerCase();
            if (nome.equals("getsenha") || nome.equals("getpassword")) {
                possuiGetterDeSenha = true;
                break;
            }
        }

        assertThat(possuiGetterDeSenha)
                .as("UsuarioResponse não deve expor a senha")
                .isFalse();
    }

    @Test
    void deveApresentarListaDeUsuarios() {
        Usuario u1 = Usuario.reconstituir(
                "usuario-1", "Ana", "ana@email.com", "hash", Perfil.ADMIN, null);
        Usuario u2 = Usuario.reconstituir(
                "usuario-2", "Bruno", "bruno@email.com", "hash", Perfil.MECANICO, null);

        List<UsuarioResponse> responses = presenter.apresentar(List.of(u1, u2));

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getId()).isEqualTo("usuario-1");
        assertThat(responses.get(0).getPerfil()).isEqualTo(Perfil.ADMIN);
        assertThat(responses.get(1).getId()).isEqualTo("usuario-2");
        assertThat(responses.get(1).getPerfil()).isEqualTo(Perfil.MECANICO);
    }
}
