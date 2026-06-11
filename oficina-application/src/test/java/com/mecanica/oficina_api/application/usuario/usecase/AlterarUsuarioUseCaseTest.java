package com.mecanica.oficina_api.application.usuario.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.usuario.gateway.UsuarioGateway;
import com.mecanica.oficina_api.domain.usuario.Perfil;
import com.mecanica.oficina_api.domain.usuario.Usuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AlterarUsuarioUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private AlterarUsuarioUseCase useCase;

    private Usuario existente;

    @BeforeEach
    void setUp() {
        existente = Usuario.reconstituir("usr-1", "Maria", "maria@email.com", "hash123", Perfil.ATENDENTE, null);
    }

    @Test
    void deveAlterarUsuarioMantendoSenhaExistente() {
        when(usuarioGateway.buscarOuFalhar("usr-1")).thenReturn(existente);
        when(usuarioGateway.alterar(eq("usr-1"), any())).thenAnswer(inv -> inv.getArgument(1));

        Usuario resultado = useCase.executar("usr-1", "Maria Souza", "maria.souza@email.com", "ADMIN", null);

        assertThat(resultado.getNome()).isEqualTo("Maria Souza");

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioGateway).alterar(eq("usr-1"), captor.capture());
        assertThat(captor.getValue().getSenha()).isEqualTo("hash123");
        assertThat(captor.getValue().getPerfil()).isEqualTo(Perfil.ADMIN);
    }

    @Test
    void deveLancarExcecao_quandoUsuarioNaoEncontrado() {
        when(usuarioGateway.buscarOuFalhar("inexistente"))
            .thenThrow(new IllegalArgumentException("Usuário não encontrado"));

        assertThatThrownBy(() ->
            useCase.executar("inexistente", "Maria", "maria@email.com", "ADMIN", null)
        ).isInstanceOf(IllegalArgumentException.class);

        verify(usuarioGateway, never()).alterar(anyString(), any());
    }

    @Test
    void deveLancarExcecao_quandoPerfilInvalido() {
        when(usuarioGateway.buscarOuFalhar("usr-1")).thenReturn(existente);

        assertThatThrownBy(() ->
            useCase.executar("usr-1", "Maria", "maria@email.com", "GERENTE", null)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("Perfil inválido");

        verify(usuarioGateway, never()).alterar(anyString(), any());
    }
}
