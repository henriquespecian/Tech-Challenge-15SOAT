package com.mecanica.oficina_api.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mecanica.oficina_api.infrastructure.persistence.ClienteJpaEntity;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarVeiculoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CriarOrdemServicoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.GerarOrcamentoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.ItemOrcamentoRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrdemServicoIntegrationTest extends BaseIntegrationTest {

    private ClienteJpaEntity salvarCliente() {
        return clienteRepository.save(cliente("Ana Souza", "52998224725", "ana@teste.com"));
    }

    private String cadastrarVeiculo(String clienteId) throws Exception {
        CadastrarVeiculoRequest req = new CadastrarVeiculoRequest();
        req.setClienteId(clienteId);
        req.setPlaca("ABC" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        req.setMarca("Toyota");
        req.setModelo("Corolla");
        req.setAno(2022);
        req.setCor("Branco");

        mockMvc.perform(comToken(post("/veiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(req))))
                .andExpect(status().isCreated());

        String json = mockMvc.perform(comToken(get("/veiculo/cliente/" + clienteId)))
                .andReturn().getResponse().getContentAsString();
        List<Map<String, Object>> lista = objectMapper.readValue(json, new TypeReference<>() {});
        return (String) lista.get(0).get("id");
    }

    private String criarOs(String veiculoId, String clienteId) throws Exception {
        CriarOrdemServicoRequest req = new CriarOrdemServicoRequest();
        req.setVeiculoId(veiculoId);
        req.setClienteId(clienteId);

        mockMvc.perform(comToken(post("/ordem-servico")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(req))))
                .andExpect(status().isCreated());

        String json = mockMvc.perform(comToken(get("/ordem-servico/veiculo/" + veiculoId)))
                .andReturn().getResponse().getContentAsString();
        List<Map<String, Object>> lista = objectMapper.readValue(json, new TypeReference<>() {});
        return (String) lista.get(0).get("id");
    }

    private GerarOrcamentoRequest orcamentoRequest() {
        ItemOrcamentoRequest item = new ItemOrcamentoRequest();
        item.setDescricao("Troca de óleo");
        item.setQuantidade(1);
        item.setPrecoUnitario(BigDecimal.valueOf(150.0));

        GerarOrcamentoRequest req = new GerarOrcamentoRequest();
        req.setItens(List.of(item));
        req.setObservacoes("Serviço padrão");
        return req;
    }

    @Test
    void fluxoCompleto_criarOsGerarOrcamentoAprovarRetirar() throws Exception {
        ClienteJpaEntity c = salvarCliente();
        String veiculoId = cadastrarVeiculo(c.getId());
        String osId = criarOs(veiculoId, c.getId());

        mockMvc.perform(comToken(get("/ordem-servico/" + osId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_TRIAGEM"))
                .andExpect(jsonPath("$.orcamento").doesNotExist());

        mockMvc.perform(comToken(post("/ordem-servico/" + osId + "/orcamento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(orcamentoRequest()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orcamento.status").value("PENDENTE"))
                .andExpect(jsonPath("$.orcamento.valorTotal").value(150.0));

        mockMvc.perform(comToken(patch("/ordem-servico/" + osId + "/orcamento/enviar")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orcamento.status").value("ENVIADO"));

        mockMvc.perform(comToken(patch("/ordem-servico/" + osId + "/orcamento/aprovar")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FINALIZADO"))
                .andExpect(jsonPath("$.orcamento.status").value("APROVADO"));

        mockMvc.perform(comToken(patch("/ordem-servico/" + osId + "/veiculo/retirar")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VEICULO_RETIRADO"));
    }

    @Test
    void fluxoNegociacao_enviarNegociarAtualizarAprovar() throws Exception {
        ClienteJpaEntity c = salvarCliente();
        String veiculoId = cadastrarVeiculo(c.getId());
        String osId = criarOs(veiculoId, c.getId());

        mockMvc.perform(comToken(post("/ordem-servico/" + osId + "/orcamento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(orcamentoRequest()))))
                .andExpect(status().isOk());

        mockMvc.perform(comToken(patch("/ordem-servico/" + osId + "/orcamento/enviar")))
                .andExpect(status().isOk());

        mockMvc.perform(comToken(patch("/ordem-servico/" + osId + "/orcamento/negociar")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orcamento.status").value("EM_NEGOCIACAO"));

        ItemOrcamentoRequest novoItem = new ItemOrcamentoRequest();
        novoItem.setDescricao("Troca de óleo + filtro");
        novoItem.setQuantidade(1);
        novoItem.setPrecoUnitario(BigDecimal.valueOf(200.0));
        GerarOrcamentoRequest novoOrc = new GerarOrcamentoRequest();
        novoOrc.setItens(List.of(novoItem));
        novoOrc.setObservacoes("Revisado após negociação");

        mockMvc.perform(comToken(put("/ordem-servico/" + osId + "/orcamento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(novoOrc))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orcamento.valorTotal").value(200.0));

        mockMvc.perform(comToken(patch("/ordem-servico/" + osId + "/orcamento/enviar")))
                .andExpect(status().isOk());

        mockMvc.perform(comToken(patch("/ordem-servico/" + osId + "/orcamento/aprovar")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FINALIZADO"));
    }

    @Test
    void criar_veiculoInexistente_retorna404() throws Exception {
        ClienteJpaEntity c = salvarCliente();
        CriarOrdemServicoRequest req = new CriarOrdemServicoRequest();
        req.setVeiculoId(UUID.randomUUID().toString());
        req.setClienteId(c.getId());

        mockMvc.perform(comToken(post("/ordem-servico")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(req))))
                .andExpect(status().isNotFound());
    }

    @Test
    void gerarOrcamento_osNaoExiste_retorna404() throws Exception {
        mockMvc.perform(comToken(post("/ordem-servico/" + UUID.randomUUID() + "/orcamento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(orcamentoRequest()))))
                .andExpect(status().isNotFound());
    }

    @Test
    void retirarVeiculo_osNaoFinalizada_retorna409() throws Exception {
        ClienteJpaEntity c = salvarCliente();
        String veiculoId = cadastrarVeiculo(c.getId());
        String osId = criarOs(veiculoId, c.getId());

        mockMvc.perform(comToken(patch("/ordem-servico/" + osId + "/veiculo/retirar")))
                .andExpect(status().isConflict());
    }

    @Test
    void criar_semToken_retorna403() throws Exception {
        CriarOrdemServicoRequest req = new CriarOrdemServicoRequest();
        req.setVeiculoId("v");
        req.setClienteId("c");

        mockMvc.perform(post("/ordem-servico")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarPorVeiculo_semOs_retornaListaVazia() throws Exception {
        mockMvc.perform(comToken(get("/ordem-servico/veiculo/" + UUID.randomUUID())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
