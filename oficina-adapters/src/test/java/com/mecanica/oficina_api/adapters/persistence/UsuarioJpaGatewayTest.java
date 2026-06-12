package com.mecanica.oficina_api.adapters.persistence;

import com.mecanica.oficina_api.domain.usuario.Perfil;
import com.mecanica.oficina_api.domain.usuario.Usuario;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Import(UsuarioJpaGateway.class)
@Transactional
class UsuarioJpaGatewayTest {

    @Autowired
    private UsuarioJpaGateway gateway;

    private Usuario novoUsuario(String email) {
        return Usuario.criar("Ana Souza", email, "hash-secreto", Perfil.ATENDENTE, null);
    }

    @Test
    void deveCadastrarEBuscarUsuarioPorId() {
        Usuario salvo = gateway.cadastrar(novoUsuario("ana@email.com"));

        assertThat(salvo.getId()).isNotBlank();

        Optional<Usuario> encontrado = gateway.buscar(salvo.getId());
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Ana Souza");
        assertThat(encontrado.get().getEmail()).isEqualTo("ana@email.com");
        assertThat(encontrado.get().getPerfil()).isEqualTo(Perfil.ATENDENTE);
    }

    @Test
    void deveRetornarVazio_quandoIdNaoExiste() {
        assertThat(gateway.buscar("inexistente")).isEmpty();
    }

    @Test
    void deveRetornarTrue_quandoEmailExiste() {
        gateway.cadastrar(novoUsuario("ana@email.com"));

        assertThat(gateway.existePorEmail("ana@email.com")).isTrue();
    }

    @Test
    void deveRetornarFalse_quandoEmailNaoExiste() {
        assertThat(gateway.existePorEmail("ninguem@email.com")).isFalse();
    }

    @Test
    void deveAlterarUsuario() {
        Usuario salvo = gateway.cadastrar(novoUsuario("ana@email.com"));

        Usuario dadosAlterados = Usuario.reconstituir(
                salvo.getId(), "Ana Alterada", "ana.nova@email.com",
                salvo.getSenha(), Perfil.MECANICO, null);

        Usuario alterado = gateway.alterar(salvo.getId(), dadosAlterados);

        assertThat(alterado.getNome()).isEqualTo("Ana Alterada");
        assertThat(alterado.getEmail()).isEqualTo("ana.nova@email.com");
        assertThat(alterado.getPerfil()).isEqualTo(Perfil.MECANICO);

        Optional<Usuario> rebuscado = gateway.buscar(salvo.getId());
        assertThat(rebuscado).isPresent();
        assertThat(rebuscado.get().getNome()).isEqualTo("Ana Alterada");
    }

    @Test
    void deveFazerSoftDelete_tornandoUsuarioInacessivelPorBuscaAtiva() {
        Usuario salvo = gateway.cadastrar(novoUsuario("ana@email.com"));

        gateway.inativar(salvo.getId());

        // buscar filtra por ativo: o usuário inativado não é mais encontrado
        assertThat(gateway.buscar(salvo.getId())).isEmpty();
        // existePorEmail não filtra por ativo: o registro continua existindo
        assertThat(gateway.existePorEmail("ana@email.com")).isTrue();
    }
}
