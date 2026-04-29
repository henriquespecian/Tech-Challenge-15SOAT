package com.mecanica.oficina_api.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mecanica.oficina_api.application.usuario.UsuarioService;
import com.mecanica.oficina_api.domain.usuario.Perfil;
import com.mecanica.oficina_api.interfaces.dto.request.AlterarUsuarioRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarUsuarioRequest;
import com.mecanica.oficina_api.interfaces.dto.response.UsuarioResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private UsuarioResponse usuarioResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();

        usuarioResponse = new UsuarioResponse(
            "usuario-1", "João Silva", "joao@email.com",
            Perfil.ADMIN, null, LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    void deveCadastrarUsuarioERetornar201() throws Exception {
        CadastrarUsuarioRequest request = new CadastrarUsuarioRequest();
        request.setNome("João Silva");
        request.setEmail("joao@email.com");
        request.setSenha("senha123");
        request.setPerfil("ADMIN");

        mockMvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void deveBuscarUsuarioERetornar200() throws Exception {
        when(usuarioService.buscar("usuario-1")).thenReturn(usuarioResponse);

        mockMvc.perform(get("/usuario/usuario-1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("usuario-1"))
            .andExpect(jsonPath("$.nome").value("João Silva"))
            .andExpect(jsonPath("$.email").value("joao@email.com"))
            .andExpect(jsonPath("$.perfil").value("ADMIN"))
            .andExpect(jsonPath("$.senha").doesNotExist());
    }

    @Test
    void deveRetornar404QuandoUsuarioNaoEncontrado() throws Exception {
        when(usuarioService.buscar("inexistente"))
            .thenThrow(new ResponseStatusException(NOT_FOUND, "Usuário não encontrado"));

        mockMvc.perform(get("/usuario/inexistente"))
            .andExpect(status().isNotFound());
    }

    @Test
    void deveAlterarUsuarioERetornar200() throws Exception {
        AlterarUsuarioRequest request = new AlterarUsuarioRequest();
        request.setNome("João Alterado");
        request.setEmail("joao.novo@email.com");
        request.setPerfil("MECANICO");

        UsuarioResponse alterado = new UsuarioResponse(
            "usuario-1", "João Alterado", "joao.novo@email.com",
            Perfil.MECANICO, null, LocalDateTime.now(), LocalDateTime.now()
        );
        when(usuarioService.alterar(eq("usuario-1"), any(AlterarUsuarioRequest.class))).thenReturn(alterado);

        mockMvc.perform(put("/usuario/usuario-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome").value("João Alterado"))
            .andExpect(jsonPath("$.perfil").value("MECANICO"));
    }

    @Test
    void deveDeletarUsuarioERetornar204() throws Exception {
        mockMvc.perform(delete("/usuario/usuario-1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar404AoDeletarUsuarioInexistente() throws Exception {
        doThrow(new ResponseStatusException(NOT_FOUND, "Usuário não encontrado"))
            .when(usuarioService).deletar("inexistente");

        mockMvc.perform(delete("/usuario/inexistente"))
            .andExpect(status().isNotFound());
    }
}
