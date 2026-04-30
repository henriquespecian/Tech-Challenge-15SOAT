package com.mecanica.oficina_api.integration;

import com.mecanica.oficina_api.domain.usuario.Perfil;
import com.mecanica.oficina_api.interfaces.dto.request.AlterarClienteRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarClienteRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ClienteIntegrationTest extends BaseIntegrationTest {

    // CPFs matematicamente válidos (mesmos usados no DevDataLoader)
    private static final String CPF_ANA   = "52998224725";
    private static final String CPF_BRUNO = "11144477735";
    private static final String CPF_CARLA = "45317828791";

    private CadastrarClienteRequest cadastroValido(String nome, String cpf, String email) {
        CadastrarClienteRequest req = new CadastrarClienteRequest();
        req.setNome(nome);
        req.setCpf(cpf);
        req.setEmail(email);
        req.setTelefone("11999990000");
        return req;
    }

    @Test
    void cadastrar_comAdmin_retorna201() throws Exception {
        mockMvc.perform(comToken(post("/cliente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cadastroValido("Ana Souza", CPF_ANA, "ana@teste.com")))))
                .andExpect(status().isCreated());
    }

    @Test
    void cadastrar_semToken_retorna403() throws Exception {
        mockMvc.perform(post("/cliente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cadastroValido("Ana Souza", CPF_ANA, "ana@teste.com"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void cadastrar_comPerfilMecanico_retorna403() throws Exception {
        usuarioRepository.save(usuario("Mecânico", "mecanico@integration.test", "mecanico123", Perfil.MECANICO, null));
        String mecanicoToken = login("mecanico@integration.test", "mecanico123");

        mockMvc.perform(comToken(post("/cliente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cadastroValido("Ana Souza", CPF_ANA, "ana@teste.com"))), mecanicoToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void consultar_clienteExistente_retorna200ComDados() throws Exception {
        mockMvc.perform(comToken(post("/cliente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cadastroValido("Ana Souza", CPF_ANA, "ana@teste.com")))));

        mockMvc.perform(comToken(get("/cliente/" + CPF_ANA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value(CPF_ANA))
                .andExpect(jsonPath("$.nome").value("Ana Souza"))
                .andExpect(jsonPath("$.email").value("ana@teste.com"));
    }

    @Test
    void consultar_clienteInexistente_retorna404() throws Exception {
        mockMvc.perform(comToken(get("/cliente/" + CPF_ANA)))
                .andExpect(status().isNotFound());
    }

    @Test
    void alterar_clienteExistente_retorna204() throws Exception {
        mockMvc.perform(comToken(post("/cliente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cadastroValido("Ana Souza", CPF_ANA, "ana@teste.com")))));

        AlterarClienteRequest alteracao = new AlterarClienteRequest();
        alteracao.setNome("Ana Oliveira");
        alteracao.setEmail("ana.oliveira@teste.com");
        alteracao.setTelefone("11988880000");

        mockMvc.perform(comToken(put("/cliente/" + CPF_ANA)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(alteracao))))
                .andExpect(status().isNoContent());
    }

    @Test
    void alterar_verificaDadosAtualizados() throws Exception {
        mockMvc.perform(comToken(post("/cliente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cadastroValido("Ana Souza", CPF_ANA, "ana@teste.com")))));

        AlterarClienteRequest alteracao = new AlterarClienteRequest();
        alteracao.setNome("Ana Oliveira");
        alteracao.setEmail("ana.oliveira@teste.com");
        alteracao.setTelefone("11988880000");

        mockMvc.perform(comToken(put("/cliente/" + CPF_ANA)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(alteracao))));

        mockMvc.perform(comToken(get("/cliente/" + CPF_ANA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ana Oliveira"))
                .andExpect(jsonPath("$.email").value("ana.oliveira@teste.com"));
    }

    @Test
    void deletar_retorna204() throws Exception {
        mockMvc.perform(comToken(post("/cliente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cadastroValido("Ana Souza", CPF_ANA, "ana@teste.com")))));

        mockMvc.perform(comToken(delete("/cliente/" + CPF_ANA)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletar_comAtendente_retorna403() throws Exception {
        usuarioRepository.save(usuario("Atendente", "atendente@integration.test", "atendente123", Perfil.ATENDENTE, null));
        String atendenteToken = login("atendente@integration.test", "atendente123");

        mockMvc.perform(comToken(post("/cliente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cadastroValido("Ana Souza", CPF_ANA, "ana@teste.com")))));

        // DELETE exige ADMIN; atendente deve receber 403
        mockMvc.perform(comToken(delete("/cliente/" + CPF_ANA), atendenteToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void consultar_clienteAposDelete_retorna404() throws Exception {
        mockMvc.perform(comToken(post("/cliente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cadastroValido("Ana Souza", CPF_ANA, "ana@teste.com")))));

        mockMvc.perform(comToken(delete("/cliente/" + CPF_ANA)));

        mockMvc.perform(comToken(get("/cliente/" + CPF_ANA)))
                .andExpect(status().isNotFound());
    }

    @Test
    void cadastrar_cpfDuplicado_retornaErro() throws Exception {
        mockMvc.perform(comToken(post("/cliente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cadastroValido("Ana Souza", CPF_ANA, "ana@teste.com")))))
                .andExpect(status().isCreated());

        mockMvc.perform(comToken(post("/cliente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cadastroValido("Ana Duplicada", CPF_ANA, "ana2@teste.com")))))
                .andExpect(status().isConflict());
    }

    @Test
    void cadastrar_doisClientesDiferentes_retorna201ParaAmbos() throws Exception {
        mockMvc.perform(comToken(post("/cliente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cadastroValido("Ana Souza", CPF_ANA, "ana@teste.com")))))
                .andExpect(status().isCreated());

        mockMvc.perform(comToken(post("/cliente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cadastroValido("Bruno Lima", CPF_BRUNO, "bruno@teste.com")))))
                .andExpect(status().isCreated());
    }

    @Test
    void alterar_clienteInexistente_retorna404() throws Exception {
        AlterarClienteRequest alteracao = new AlterarClienteRequest();
        alteracao.setNome("Fantasma");
        alteracao.setEmail("fantasma@teste.com");
        alteracao.setTelefone("11900000000");

        mockMvc.perform(comToken(put("/cliente/" + CPF_CARLA)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(alteracao))))
                .andExpect(status().isNotFound());
    }
}
