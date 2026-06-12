package com.mecanica.oficina_api.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mecanica.oficina_api.adapters.web.GlobalExceptionHandler;
import com.mecanica.oficina_api.adapters.web.UsuarioController;
import com.mecanica.oficina_api.adapters.web.dto.request.AlterarUsuarioRequest;
import com.mecanica.oficina_api.adapters.web.dto.request.CadastrarUsuarioRequest;
import com.mecanica.oficina_api.adapters.web.presenter.UsuarioPresenter;
import com.mecanica.oficina_api.application.usuario.usecase.AlterarUsuarioUseCase;
import com.mecanica.oficina_api.application.usuario.usecase.CadastrarUsuarioUseCase;
import com.mecanica.oficina_api.application.usuario.usecase.ConsultarUsuarioUseCase;
import com.mecanica.oficina_api.application.usuario.usecase.InativarUsuarioUseCase;
import com.mecanica.oficina_api.domain.usuario.Perfil;
import com.mecanica.oficina_api.domain.usuario.Usuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CadastrarUsuarioUseCase cadastrarUsuarioUseCase;
    @Mock
    private ConsultarUsuarioUseCase consultarUsuarioUseCase;
    @Mock
    private AlterarUsuarioUseCase alterarUsuarioUseCase;
    @Mock
    private InativarUsuarioUseCase inativarUsuarioUseCase;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        UsuarioController controller = new UsuarioController(
                cadastrarUsuarioUseCase, consultarUsuarioUseCase,
                alterarUsuarioUseCase, inativarUsuarioUseCase, new UsuarioPresenter());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        usuario = Usuario.reconstituir(
                "usuario-1", "Ana Souza", "ana@email.com",
                "hash-secreto", Perfil.ATENDENTE, null);
    }

    @Test
    void deveCadastrarUsuarioERetornar201() throws Exception {
        when(cadastrarUsuarioUseCase.executar("Ana Souza", "ana@email.com", "123456", "ATENDENTE", null))
                .thenReturn(usuario);

        mockMvc.perform(post("/usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cadastrarRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("usuario-1"))
                .andExpect(jsonPath("$.nome").value("Ana Souza"))
                .andExpect(jsonPath("$.email").value("ana@email.com"))
                .andExpect(jsonPath("$.perfil").value("ATENDENTE"))
                .andExpect(jsonPath("$.senha").doesNotExist());
    }

    @Test
    void deveBuscarUsuarioERetornar200() throws Exception {
        when(consultarUsuarioUseCase.executar("usuario-1")).thenReturn(usuario);

        mockMvc.perform(get("/usuario/usuario-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("usuario-1"))
                .andExpect(jsonPath("$.nome").value("Ana Souza"))
                .andExpect(jsonPath("$.senha").doesNotExist());
    }

    @Test
    void deveRetornar404_quandoUsuarioNaoEncontrado() throws Exception {
        when(consultarUsuarioUseCase.executar("inexistente"))
                .thenThrow(new IllegalArgumentException("Usuário não encontrado"));

        mockMvc.perform(get("/usuario/inexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAlterarUsuarioERetornar200() throws Exception {
        Usuario alterado = Usuario.reconstituir(
                "usuario-1", "Ana Alterada", "ana.nova@email.com",
                "hash-secreto", Perfil.MECANICO, null);
        when(alterarUsuarioUseCase.executar("usuario-1", "Ana Alterada", "ana.nova@email.com", "MECANICO", null))
                .thenReturn(alterado);

        mockMvc.perform(put("/usuario/usuario-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alterarRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ana Alterada"))
                .andExpect(jsonPath("$.email").value("ana.nova@email.com"))
                .andExpect(jsonPath("$.perfil").value("MECANICO"))
                .andExpect(jsonPath("$.senha").doesNotExist());

        verify(alterarUsuarioUseCase).executar("usuario-1", "Ana Alterada", "ana.nova@email.com", "MECANICO", null);
    }

    @Test
    void deveRetornar404AoAlterar_quandoUseCaseLancaIllegalArgument() throws Exception {
        doThrow(new IllegalArgumentException("Usuário não encontrado"))
                .when(alterarUsuarioUseCase).executar(eq("inexistente"), any(), any(), any(), any());

        mockMvc.perform(put("/usuario/inexistente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alterarRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveDeletarUsuarioERetornar204() throws Exception {
        mockMvc.perform(delete("/usuario/usuario-1"))
                .andExpect(status().isNoContent());

        verify(inativarUsuarioUseCase).executar("usuario-1");
    }

    @Test
    void deveRetornar404AoDeletar_quandoUseCaseLancaIllegalArgument() throws Exception {
        doThrow(new IllegalArgumentException("Usuário não encontrado"))
                .when(inativarUsuarioUseCase).executar("inexistente");

        mockMvc.perform(delete("/usuario/inexistente"))
                .andExpect(status().isNotFound());

        verify(inativarUsuarioUseCase).executar("inexistente");
    }

    private CadastrarUsuarioRequest cadastrarRequest() {
        CadastrarUsuarioRequest request = new CadastrarUsuarioRequest();
        request.setNome("Ana Souza");
        request.setEmail("ana@email.com");
        request.setSenha("123456");
        request.setPerfil("ATENDENTE");
        return request;
    }

    private AlterarUsuarioRequest alterarRequest() {
        AlterarUsuarioRequest request = new AlterarUsuarioRequest();
        request.setNome("Ana Alterada");
        request.setEmail("ana.nova@email.com");
        request.setPerfil("MECANICO");
        return request;
    }
}
