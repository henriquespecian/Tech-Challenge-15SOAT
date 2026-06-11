package com.mecanica.oficina_api.application.usuario.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.usuario.gateway.UsuarioGateway;
import com.mecanica.oficina_api.domain.usuario.Perfil;
import com.mecanica.oficina_api.domain.usuario.Usuario;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InativarUsuarioUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private InativarUsuarioUseCase useCase;

    @Test
    void deveInativarUsuarioComSucesso() {
        Usuario usuario = Usuario.reconstituir("usr-1", "Maria", "maria@email.com", "hash123", Perfil.ATENDENTE, null);
        when(usuarioGateway.buscarOuFalhar("usr-1")).thenReturn(usuario);

        assertThatNoException().isThrownBy(() -> useCase.executar("usr-1"));

        verify(usuarioGateway).inativar("usr-1");
    }

    @Test
    void deveLancarExcecao_quandoUsuarioNaoEncontrado() {
        when(usuarioGateway.buscarOuFalhar("inexistente"))
            .thenThrow(new IllegalArgumentException("Usuário não encontrado"));

        assertThatThrownBy(() -> useCase.executar("inexistente"))
            .isInstanceOf(IllegalArgumentException.class);

        verify(usuarioGateway, never()).inativar(anyString());
    }
}
