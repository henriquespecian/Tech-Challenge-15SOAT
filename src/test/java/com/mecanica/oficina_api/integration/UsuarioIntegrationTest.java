package com.mecanica.oficina_api.integration;

import com.mecanica.oficina_api.domain.usuario.Perfil;
import com.mecanica.oficina_api.infrastructure.persistence.ClienteJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.UsuarioJpaEntity;
import com.mecanica.oficina_api.interfaces.dto.request.AlterarUsuarioRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarUsuarioRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UsuarioIntegrationTest extends BaseIntegrationTest {

    private CadastrarUsuarioRequest cadastroMecanico(String email) {
        CadastrarUsuarioRequest req = new CadastrarUsuarioRequest();
        req.setNome("Mecânico Teste");
        req.setEmail(email);
        req.setSenha("mecanico123");
        req.setPerfil("MECANICO");
        return req;
    }

    private String idDoUsuario(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow().getId();
    }

    @Test
    void cadastrar_comAdmin_retorna201() throws Exception {
        mockMvc.perform(comToken(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cadastroMecanico("mecanico@teste.com")))))
                .andExpect(status().isCreated());
    }

    @Test
    void cadastrar_semToken_retorna403() throws Exception {
        mockMvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cadastroMecanico("mecanico@teste.com"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void cadastrar_comAtendente_retorna403() throws Exception {
        usuarioRepository.save(usuario("Atendente", "atendente@integration.test", "atendente123", Perfil.ATENDENTE, null));
        String atendenteToken = login("atendente@integration.test", "atendente123");

        mockMvc.perform(comToken(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cadastroMecanico("mecanico@teste.com"))), atendenteToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void cadastrar_emailDuplicado_retorna409() throws Exception {
        mockMvc.perform(comToken(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cadastroMecanico("mecanico@teste.com")))))
                .andExpect(status().isCreated());

        mockMvc.perform(comToken(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cadastroMecanico("mecanico@teste.com")))))
                .andExpect(status().isConflict());
    }

    @Test
    void buscar_usuarioExistente_retorna200ComDados() throws Exception {
        mockMvc.perform(comToken(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cadastroMecanico("mecanico@teste.com"))))).andExpect(status().isCreated());

        String id = idDoUsuario("mecanico@teste.com");

        mockMvc.perform(comToken(get("/usuario/" + id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("mecanico@teste.com"))
                .andExpect(jsonPath("$.perfil").value("MECANICO"))
                .andExpect(jsonPath("$.nome").value("Mecânico Teste"));
    }

    @Test
    void buscar_usuarioInexistente_retorna404() throws Exception {
        mockMvc.perform(comToken(get("/usuario/" + UUID.randomUUID())))
                .andExpect(status().isNotFound());
    }

    @Test
    void alterar_retorna200ComDadosAtualizados() throws Exception {
        mockMvc.perform(comToken(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cadastroMecanico("mecanico@teste.com")))));

        String id = idDoUsuario("mecanico@teste.com");

        AlterarUsuarioRequest alteracao = new AlterarUsuarioRequest();
        alteracao.setNome("Mecânico Sênior");
        alteracao.setEmail("mecanico.senior@teste.com");
        alteracao.setPerfil("MECANICO");

        mockMvc.perform(comToken(put("/usuario/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(alteracao))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Mecânico Sênior"))
                .andExpect(jsonPath("$.email").value("mecanico.senior@teste.com"));
    }

    @Test
    void deletar_retorna204() throws Exception {
        mockMvc.perform(comToken(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cadastroMecanico("mecanico@teste.com")))));

        String id = idDoUsuario("mecanico@teste.com");

        mockMvc.perform(comToken(delete("/usuario/" + id)))
                .andExpect(status().isNoContent());
    }

    @Test
    void buscar_usuarioDeletado_retorna404() throws Exception {
        mockMvc.perform(comToken(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cadastroMecanico("mecanico@teste.com")))));

        String id = idDoUsuario("mecanico@teste.com");
        mockMvc.perform(comToken(delete("/usuario/" + id)));

        mockMvc.perform(comToken(get("/usuario/" + id)))
                .andExpect(status().isNotFound());
    }

    @Test
    void cadastrar_perfilInvalido_retorna400() throws Exception {
        CadastrarUsuarioRequest req = new CadastrarUsuarioRequest();
        req.setNome("Teste");
        req.setEmail("teste@teste.com");
        req.setSenha("senha123");
        req.setPerfil("PERFIL_INVALIDO");

        mockMvc.perform(comToken(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(req))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cadastrar_tipoCliente_semClienteId_retorna400() throws Exception {
        CadastrarUsuarioRequest req = new CadastrarUsuarioRequest();
        req.setNome("Cliente Portal");
        req.setEmail("cliente@teste.com");
        req.setSenha("cliente123");
        req.setPerfil("CLIENTE");

        mockMvc.perform(comToken(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(req))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cadastrar_tipoCliente_comClienteValido_retorna201() throws Exception {
        ClienteJpaEntity clienteCadastrado = clienteRepository.save(
                cliente("Ana Souza", "52998224725", "ana@teste.com"));

        CadastrarUsuarioRequest req = new CadastrarUsuarioRequest();
        req.setNome("Ana Portal");
        req.setEmail("ana.portal@teste.com");
        req.setSenha("ana123");
        req.setPerfil("CLIENTE");
        req.setClienteId(clienteCadastrado.getId());

        mockMvc.perform(comToken(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(req))))
                .andExpect(status().isCreated());
    }

    @Test
    void cadastrar_tipoCliente_comClienteInexistente_retorna404() throws Exception {
        CadastrarUsuarioRequest req = new CadastrarUsuarioRequest();
        req.setNome("Portal Fantasma");
        req.setEmail("fantasma@teste.com");
        req.setSenha("senha123");
        req.setPerfil("CLIENTE");
        req.setClienteId(UUID.randomUUID().toString());

        mockMvc.perform(comToken(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(req))))
                .andExpect(status().isNotFound());
    }

    @Test
    void fluxoCompleto_cadastrarAtivarEDesativarUsuario() throws Exception {
        // Cadastra
        mockMvc.perform(comToken(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cadastroMecanico("mecanico@teste.com")))))
                .andExpect(status().isCreated());

        String id = idDoUsuario("mecanico@teste.com");

        // Confirma que existe
        mockMvc.perform(comToken(get("/usuario/" + id)))
                .andExpect(status().isOk());

        // Desativa
        mockMvc.perform(comToken(delete("/usuario/" + id)))
                .andExpect(status().isNoContent());

        // Confirma que não existe mais
        mockMvc.perform(comToken(get("/usuario/" + id)))
                .andExpect(status().isNotFound());
    }

    @Test
    void usuarioDesativado_naoConsegueLogar() throws Exception {
        mockMvc.perform(comToken(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cadastroMecanico("mecanico@teste.com")))));

        UsuarioJpaEntity mecanico = usuarioRepository.findByEmail("mecanico@teste.com").orElseThrow();
        mecanico.setAtivo(false);
        usuarioRepository.save(mecanico);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new com.mecanica.oficina_api.interfaces.dto.request.LoginRequest(
                        "mecanico@teste.com", "mecanico123"))))
                .andExpect(status().isForbidden());
    }
}
