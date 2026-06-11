package com.mecanica.oficina_api.application.usuario.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.usuario.gateway.PasswordEncoder;
import com.mecanica.oficina_api.application.usuario.gateway.UsuarioGateway;
import com.mecanica.oficina_api.domain.usuario.Perfil;
import com.mecanica.oficina_api.domain.usuario.Usuario;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CadastrarUsuarioUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CadastrarUsuarioUseCase useCase;

    @Test
    void deveCadastrarUsuarioComSenhaEncodada() {
        Usuario salvo = Usuario.reconstituir("usr-1", "Maria", "maria@email.com", "hash123", Perfil.ATENDENTE, null);
        when(usuarioGateway.existePorEmail("maria@email.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("hash123");
        when(usuarioGateway.cadastrar(any())).thenReturn(salvo);

        Usuario resultado = useCase.executar("Maria", "maria@email.com", "123456", "ATENDENTE", null);

        assertThat(resultado.getId()).isEqualTo("usr-1");

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioGateway).cadastrar(captor.capture());
        assertThat(captor.getValue().getSenha()).isEqualTo("hash123");
        assertThat(captor.getValue().getPerfil()).isEqualTo(Perfil.ATENDENTE);
    }

    @Test
    void deveLancarExcecao_quandoEmailJaCadastrado() {
        when(usuarioGateway.existePorEmail("maria@email.com")).thenReturn(true);

        assertThatThrownBy(() ->
            useCase.executar("Maria", "maria@email.com", "123456", "ATENDENTE", null)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("Email já cadastrado");

        verify(usuarioGateway, never()).cadastrar(any());
    }

    @Test
    void deveLancarExcecao_quandoPerfilInvalido() {
        when(usuarioGateway.existePorEmail("maria@email.com")).thenReturn(false);

        assertThatThrownBy(() ->
            useCase.executar("Maria", "maria@email.com", "123456", "GERENTE", null)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("Perfil inválido");

        verify(usuarioGateway, never()).cadastrar(any());
    }

    @Test
    void deveLancarExcecao_quandoPerfilClienteSemClienteId() {
        when(usuarioGateway.existePorEmail("cliente@email.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("hash123");

        assertThatThrownBy(() ->
            useCase.executar("Cliente", "cliente@email.com", "123456", "CLIENTE", null)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("clienteId é obrigatório");

        verify(usuarioGateway, never()).cadastrar(any());
    }
}
