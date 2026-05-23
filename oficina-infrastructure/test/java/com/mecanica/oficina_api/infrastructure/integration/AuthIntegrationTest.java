package com.mecanica.oficina_api.integration;

import com.mecanica.oficina_api.interfaces.dto.request.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthIntegrationTest extends BaseIntegrationTest {

    @Test
    void login_comCredenciaisValidas_retorna200EToken() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new LoginRequest("admin@integration.test", "admin123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.nome").value("Admin Testes"))
                .andExpect(jsonPath("$.perfil").value("ADMIN"));
    }

    @Test
    void login_comSenhaInvalida_retornaErro() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new LoginRequest("admin@integration.test", "senhaErrada"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void login_comEmailInexistente_retornaErro() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new LoginRequest("naoexiste@test.com", "senha123"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void acesso_semToken_emEndpointProtegido_retorna403() throws Exception {
        mockMvc.perform(get("/cliente/52998224725"))
                .andExpect(status().isForbidden());
    }

    @Test
    void acesso_comTokenInvalido_emEndpointProtegido_retorna403() throws Exception {
        mockMvc.perform(get("/cliente/52998224725")
                .header("Authorization", "Bearer tokeninvalido.qualquer.coisa"))
                .andExpect(status().isForbidden());
    }

    @Test
    void acesso_comTokenMalFormado_emEndpointProtegido_retorna403() throws Exception {
        mockMvc.perform(get("/usuario/qualquer-id")
                .header("Authorization", "SemBearer algumtoken"))
                .andExpect(status().isForbidden());
    }
}
