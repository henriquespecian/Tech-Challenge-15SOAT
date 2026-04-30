package com.mecanica.oficina_api.application.usuario;

import com.mecanica.oficina_api.domain.usuario.Perfil;
import com.mecanica.oficina_api.infrastructure.persistence.ClienteJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.UsuarioJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.repository.ClienteSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.UsuarioSpringDataRepository;
import com.mecanica.oficina_api.interfaces.dto.request.AlterarUsuarioRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarUsuarioRequest;
import com.mecanica.oficina_api.interfaces.dto.response.UsuarioResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioSpringDataRepository usuarioRepository;

    @Mock
    private ClienteSpringDataRepository clienteRepository;

    @Spy
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    private UsuarioService usuarioService;

    private CadastrarUsuarioRequest cadastrarRequest;
    private ClienteJpaEntity clienteEntity;
    private UsuarioJpaEntity usuarioEntity;

    @BeforeEach
    void setUp() {
        cadastrarRequest = new CadastrarUsuarioRequest();
        cadastrarRequest.setNome("João Silva");
        cadastrarRequest.setEmail("joao@email.com");
        cadastrarRequest.setSenha("senha123");
        cadastrarRequest.setPerfil("ADMIN");

        clienteEntity = new ClienteJpaEntity();
        clienteEntity.setId("cliente-1");
        clienteEntity.setAtivo(true);

        usuarioEntity = new UsuarioJpaEntity();
        usuarioEntity.setId("usuario-1");
        usuarioEntity.setNome("João Silva");
        usuarioEntity.setEmail("joao@email.com");
        usuarioEntity.setSenha("$2a$10$hash");
        usuarioEntity.setPerfil(Perfil.ADMIN);
        usuarioEntity.setAtivo(true);
        usuarioEntity.setDataCadastro(LocalDateTime.now());
        usuarioEntity.setDataAtualizacao(LocalDateTime.now());
    }

    @Test
    void deveCadastrarUsuarioAdminComSucesso() {
        when(usuarioRepository.existsByEmail("joao@email.com")).thenReturn(false);

        usuarioService.cadastrar(cadastrarRequest);

        verify(usuarioRepository).save(any(UsuarioJpaEntity.class));
    }

    @Test
    void deveCadastrarUsuarioClienteComClienteIdValido() {
        cadastrarRequest.setPerfil("CLIENTE");
        cadastrarRequest.setClienteId("cliente-1");
        when(usuarioRepository.existsByEmail("joao@email.com")).thenReturn(false);
        when(clienteRepository.findById("cliente-1")).thenReturn(Optional.of(clienteEntity));

        usuarioService.cadastrar(cadastrarRequest);

        verify(usuarioRepository).save(any(UsuarioJpaEntity.class));
    }

    @Test
    void deveSalvarSenhaHasheadaAoCadastrar() {
        when(usuarioRepository.existsByEmail("joao@email.com")).thenReturn(false);

        usuarioService.cadastrar(cadastrarRequest);

        verify(usuarioRepository).save(argThat(entity ->
            !entity.getSenha().equals("senha123") && entity.getSenha().startsWith("$2a$")
        ));
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaExiste() {
        when(usuarioRepository.existsByEmail("joao@email.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.cadastrar(cadastrarRequest))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Email já cadastrado");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoPerfilClienteSemClienteId() {
        cadastrarRequest.setPerfil("CLIENTE");
        cadastrarRequest.setClienteId(null);
        when(usuarioRepository.existsByEmail("joao@email.com")).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.cadastrar(cadastrarRequest))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("clienteId é obrigatório");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoPerfilClienteComClienteIdInexistente() {
        cadastrarRequest.setPerfil("CLIENTE");
        cadastrarRequest.setClienteId("cliente-inexistente");
        when(usuarioRepository.existsByEmail("joao@email.com")).thenReturn(false);
        when(clienteRepository.findById("cliente-inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.cadastrar(cadastrarRequest))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Cliente não encontrado");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoPerfilInvalido() {
        cadastrarRequest.setPerfil("INVALIDO");
        when(usuarioRepository.existsByEmail("joao@email.com")).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.cadastrar(cadastrarRequest))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Perfil inválido");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveBuscarUsuarioComSucesso() {
        when(usuarioRepository.findByIdAndAtivoTrue("usuario-1")).thenReturn(Optional.of(usuarioEntity));

        UsuarioResponse response = usuarioService.buscar("usuario-1");

        assertThat(response.getId()).isEqualTo("usuario-1");
        assertThat(response.getNome()).isEqualTo("João Silva");
        assertThat(response.getEmail()).isEqualTo("joao@email.com");
        assertThat(response.getPerfil()).isEqualTo(Perfil.ADMIN);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoNaBusca() {
        when(usuarioRepository.findByIdAndAtivoTrue("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.buscar("inexistente"))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Usuário não encontrado");
    }

    @Test
    void deveAlterarUsuarioComSucesso() {
        AlterarUsuarioRequest request = new AlterarUsuarioRequest();
        request.setNome("João Alterado");
        request.setEmail("joao.novo@email.com");
        request.setPerfil("MECANICO");

        when(usuarioRepository.findByIdAndAtivoTrue("usuario-1")).thenReturn(Optional.of(usuarioEntity));

        UsuarioResponse response = usuarioService.alterar("usuario-1", request);

        assertThat(response.getNome()).isEqualTo("João Alterado");
        assertThat(response.getEmail()).isEqualTo("joao.novo@email.com");
        assertThat(response.getPerfil()).isEqualTo(Perfil.MECANICO);
        verify(usuarioRepository).save(any(UsuarioJpaEntity.class));
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoAoAlterar() {
        AlterarUsuarioRequest request = new AlterarUsuarioRequest();
        request.setNome("Qualquer");
        request.setEmail("qualquer@email.com");
        request.setPerfil("ADMIN");
        when(usuarioRepository.findByIdAndAtivoTrue("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.alterar("inexistente", request))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Usuário não encontrado");
    }

    @Test
    void deveDeletarUsuarioComSoftDelete() {
        when(usuarioRepository.findByIdAndAtivoTrue("usuario-1")).thenReturn(Optional.of(usuarioEntity));

        usuarioService.deletar("usuario-1");

        verify(usuarioRepository).save(argThat(entity -> !entity.getAtivo()));
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoAoDeletar() {
        when(usuarioRepository.findByIdAndAtivoTrue("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.deletar("inexistente"))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Usuário não encontrado");

        verify(usuarioRepository, never()).save(any());
    }
}
