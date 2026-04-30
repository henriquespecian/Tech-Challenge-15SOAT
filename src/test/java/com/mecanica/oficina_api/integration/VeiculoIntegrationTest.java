package com.mecanica.oficina_api.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mecanica.oficina_api.infrastructure.persistence.ClienteJpaEntity;
import com.mecanica.oficina_api.interfaces.dto.request.AlterarVeiculoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarVeiculoRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class VeiculoIntegrationTest extends BaseIntegrationTest {

    private ClienteJpaEntity salvarCliente() {
        return clienteRepository.save(cliente("Ana Souza", "52998224725", "ana@teste.com"));
    }

    private CadastrarVeiculoRequest veiculo(String clienteId, String placa) {
        CadastrarVeiculoRequest req = new CadastrarVeiculoRequest();
        req.setClienteId(clienteId);
        req.setPlaca(placa);
        req.setMarca("Toyota");
        req.setModelo("Corolla");
        req.setAno(2022);
        req.setCor("Branco");
        return req;
    }

    private String idDoVeiculo(String clienteId) throws Exception {
        String json = mockMvc.perform(comToken(get("/veiculo/cliente/" + clienteId)))
                .andReturn().getResponse().getContentAsString();
        List<Map<String, Object>> lista = objectMapper.readValue(json, new TypeReference<>() {});
        return (String) lista.get(0).get("id");
    }

    @Test
    void cadastrar_paraClienteExistente_retorna201() throws Exception {
        ClienteJpaEntity c = salvarCliente();

        mockMvc.perform(comToken(post("/veiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(veiculo(c.getId(), "ABC1234")))))
                .andExpect(status().isCreated());
    }

    @Test
    void cadastrar_paraClienteInexistente_retornaErro() throws Exception {
        mockMvc.perform(comToken(post("/veiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(veiculo(UUID.randomUUID().toString(), "XYZ5678")))))
                .andExpect(status().isNotFound());
    }

    @Test
    void cadastrar_placaDuplicada_retornaErro() throws Exception {
        ClienteJpaEntity c = salvarCliente();

        mockMvc.perform(comToken(post("/veiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(veiculo(c.getId(), "ABC1234")))))
                .andExpect(status().isCreated());

        mockMvc.perform(comToken(post("/veiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(veiculo(c.getId(), "ABC1234")))))
                .andExpect(status().isConflict());
    }

    @Test
    void cadastrar_semToken_retorna403() throws Exception {
        mockMvc.perform(post("/veiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(veiculo(UUID.randomUUID().toString(), "ABC1234"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void buscarPorId_retorna200ComDados() throws Exception {
        ClienteJpaEntity c = salvarCliente();
        mockMvc.perform(comToken(post("/veiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(veiculo(c.getId(), "ABC1234")))));

        String veiculoId = idDoVeiculo(c.getId());

        mockMvc.perform(comToken(get("/veiculo/" + veiculoId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("ABC1234"))
                .andExpect(jsonPath("$.marca").value("Toyota"))
                .andExpect(jsonPath("$.clienteId").value(c.getId()));
    }

    @Test
    void buscarPorId_inexistente_retorna404() throws Exception {
        mockMvc.perform(comToken(get("/veiculo/" + UUID.randomUUID())))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarPorCliente_retorna200ComLista() throws Exception {
        ClienteJpaEntity c = salvarCliente();
        mockMvc.perform(comToken(post("/veiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(veiculo(c.getId(), "ABC1234")))));
        mockMvc.perform(comToken(post("/veiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(veiculo(c.getId(), "DEF5678")))));

        mockMvc.perform(comToken(get("/veiculo/cliente/" + c.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void listarPorCliente_semVeiculos_retornaListaVazia() throws Exception {
        ClienteJpaEntity c = salvarCliente();

        mockMvc.perform(comToken(get("/veiculo/cliente/" + c.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void alterar_retorna200ComDadosAtualizados() throws Exception {
        ClienteJpaEntity c = salvarCliente();
        mockMvc.perform(comToken(post("/veiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(veiculo(c.getId(), "ABC1234")))));

        String veiculoId = idDoVeiculo(c.getId());

        AlterarVeiculoRequest alteracao = new AlterarVeiculoRequest();
        alteracao.setPlaca("ABC1234");
        alteracao.setMarca("Honda");
        alteracao.setModelo("Civic");
        alteracao.setAno(2023);
        alteracao.setCor("Preto");

        mockMvc.perform(comToken(put("/veiculo/" + veiculoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(alteracao))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.marca").value("Honda"))
                .andExpect(jsonPath("$.modelo").value("Civic"))
                .andExpect(jsonPath("$.cor").value("Preto"));
    }

    @Test
    void deletar_retorna204() throws Exception {
        ClienteJpaEntity c = salvarCliente();
        mockMvc.perform(comToken(post("/veiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(veiculo(c.getId(), "ABC1234")))));

        String veiculoId = idDoVeiculo(c.getId());

        mockMvc.perform(comToken(delete("/veiculo/" + veiculoId)))
                .andExpect(status().isNoContent());
    }

    @Test
    void buscar_veiculoDeletado_retorna404() throws Exception {
        ClienteJpaEntity c = salvarCliente();
        mockMvc.perform(comToken(post("/veiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(veiculo(c.getId(), "ABC1234")))));

        String veiculoId = idDoVeiculo(c.getId());
        mockMvc.perform(comToken(delete("/veiculo/" + veiculoId)));

        mockMvc.perform(comToken(get("/veiculo/" + veiculoId)))
                .andExpect(status().isNotFound());
    }

    @Test
    void fluxoCompleto_cadastrarDoisVeiculos_removerUm_listarRestante() throws Exception {
        ClienteJpaEntity c = salvarCliente();

        mockMvc.perform(comToken(post("/veiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(veiculo(c.getId(), "GHI3456")))))
                .andExpect(status().isCreated());
        mockMvc.perform(comToken(post("/veiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(veiculo(c.getId(), "JKL7890")))))
                .andExpect(status().isCreated());

        mockMvc.perform(comToken(get("/veiculo/cliente/" + c.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        String primeiroId = idDoVeiculo(c.getId());
        mockMvc.perform(comToken(delete("/veiculo/" + primeiroId)))
                .andExpect(status().isNoContent());

        mockMvc.perform(comToken(get("/veiculo/cliente/" + c.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void veiculosDeClientesDiferentes_naoSeMisturam() throws Exception {
        ClienteJpaEntity ana   = clienteRepository.save(cliente("Ana Souza",  "52998224725", "ana@teste.com"));
        ClienteJpaEntity bruno = clienteRepository.save(cliente("Bruno Lima", "11144477735", "bruno@teste.com"));

        mockMvc.perform(comToken(post("/veiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(veiculo(ana.getId(), "ANA1111")))));
        mockMvc.perform(comToken(post("/veiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(veiculo(bruno.getId(), "BRU2222")))));

        String listaAna = mockMvc.perform(comToken(get("/veiculo/cliente/" + ana.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andReturn().getResponse().getContentAsString();

        List<Map<String, Object>> veiculosAna = objectMapper.readValue(listaAna, new TypeReference<>() {});
        assertEquals("ANA1111", veiculosAna.get(0).get("placa"));
    }
}
