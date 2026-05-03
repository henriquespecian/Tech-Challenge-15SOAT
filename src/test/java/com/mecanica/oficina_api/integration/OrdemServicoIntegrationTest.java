package com.mecanica.oficina_api.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mecanica.oficina_api.infrastructure.persistence.ClienteJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.InsumosJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.ServicoJpaEntity;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarVeiculoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CriarOrdemServicoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.GerarOrcamentoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.ItemOrcamentoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.ItemServicoRequest;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    private InsumosJpaEntity salvarInsumo(String nome, double preco) {
        InsumosJpaEntity ie = new InsumosJpaEntity();
        ie.setNome(nome);
        ie.setPrecoUnitario(BigDecimal.valueOf(preco));
        ie.setEstoqueAtual(10);
        ie.setEstoqueMinimo(2);
        ie.setUnidade("Litro");
        ie.setAtivo(true);
        return insumosRepository.save(ie);
    }

    private ServicoJpaEntity salvarServico(String nome, double preco) {
        ServicoJpaEntity servico = new ServicoJpaEntity();
        servico.setId(UUID.randomUUID().toString());
        servico.setNome(nome);
        servico.setDescricao("Servico de teste");
        servico.setPreco(BigDecimal.valueOf(preco));
        servico.setTempoEstimadoHoras(Duration.ofHours(1));
        servico.setAtivo(true);
        return servicoRepository.save(servico);
    }

    private GerarOrcamentoRequest orcamentoRequest(String insumoId, String servicoId) {
        ItemOrcamentoRequest itemInsumo = new ItemOrcamentoRequest();
        itemInsumo.setInsumoId(insumoId);
        itemInsumo.setQuantidade(1);

        ItemServicoRequest itemServico = new ItemServicoRequest();
        itemServico.setServicoId(servicoId);
        itemServico.setQuantidade(1);

        GerarOrcamentoRequest req = new GerarOrcamentoRequest();
        req.setInsumos(List.of(itemInsumo));
        req.setServicos(List.of(itemServico));
        req.setObservacoes("Servico padrao");
        return req;
    }

    @Test
    void fluxoCompletoCriarOsGerarOrcamentoAprovarFinalizarEEntregar() throws Exception {
        ClienteJpaEntity c = salvarCliente();
        String veiculoId = cadastrarVeiculo(c.getId());
        String osId = criarOs(veiculoId, c.getId());
        String insumoId = salvarInsumo("Oleo de motor", 150.0).getId();
        String servicoId = salvarServico("Troca de oleo", 80.0).getId();

        mockMvc.perform(comToken(get("/ordem-servico/" + osId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("RECEBIDA"))
            .andExpect(jsonPath("$.orcamento").doesNotExist());

        mockMvc.perform(comToken(patch("/ordem-servico/" + osId + "/iniciar-diagnostico")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("EM_DIAGNOSTICO"));

        mockMvc.perform(comToken(post("/ordem-servico/" + osId + "/orcamento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(orcamentoRequest(insumoId, servicoId)))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orcamento.status").value("PENDENTE"))
            .andExpect(jsonPath("$.orcamento.valorTotal").value(230.0));

        mockMvc.perform(comToken(patch("/ordem-servico/" + osId + "/orcamento/enviar")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orcamento.status").value("ENVIADO"));

        mockMvc.perform(comToken(patch("/ordem-servico/" + osId + "/orcamento/aprovar")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("EM_EXECUCAO"))
            .andExpect(jsonPath("$.orcamento.status").value("APROVADO"));

        String servicosJson = mockMvc.perform(comToken(get("/ordem-servico/" + osId + "/servico/listar")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andReturn().getResponse().getContentAsString();
        List<Map<String, Object>> servicos = objectMapper.readValue(servicosJson, new TypeReference<>() {});
        String statusServicoId = (String) servicos.get(0).get("id");

        mockMvc.perform(comToken(patch("/ordem-servico/servico/" + statusServicoId + "/iniciar")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("INICIADO"));

        mockMvc.perform(comToken(patch("/ordem-servico/servico/" + statusServicoId + "/finalizar")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("FINALIZADO"));

        mockMvc.perform(comToken(patch("/ordem-servico/" + osId + "/finalizar")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("FINALIZADA"));

        mockMvc.perform(comToken(patch("/ordem-servico/" + osId + "/entregar")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ENTREGUE"));
    }

    @Test
    void fluxoAguardarAtualizarEReenviarOrcamento() throws Exception {
        ClienteJpaEntity c = salvarCliente();
        String veiculoId = cadastrarVeiculo(c.getId());
        String osId = criarOs(veiculoId, c.getId());
        String insumoId = salvarInsumo("Oleo de motor", 150.0).getId();
        String servicoId = salvarServico("Troca de oleo", 80.0).getId();
        String insumoAtualizadoId = salvarInsumo("Oleo e filtro", 200.0).getId();

        mockMvc.perform(comToken(patch("/ordem-servico/" + osId + "/iniciar-diagnostico")))
            .andExpect(status().isOk());

        mockMvc.perform(comToken(post("/ordem-servico/" + osId + "/orcamento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(orcamentoRequest(insumoId, servicoId)))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orcamento.status").value("PENDENTE"));

        mockMvc.perform(comToken(patch("/ordem-servico/" + osId + "/orcamento/enviar")))
            .andExpect(status().isOk());

        mockMvc.perform(comToken(patch("/ordem-servico/" + osId + "/orcamento/aguardar")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orcamento.status").value("AGUARDANDO"));

        mockMvc.perform(comToken(put("/ordem-servico/" + osId + "/orcamento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(orcamentoRequest(insumoAtualizadoId, servicoId)))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("AGUARDANDO_APROVACAO"))
            .andExpect(jsonPath("$.orcamento.status").value("PENDENTE"))
            .andExpect(jsonPath("$.orcamento.valorTotal").value(280.0));

        mockMvc.perform(comToken(patch("/ordem-servico/" + osId + "/orcamento/enviar")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orcamento.status").value("ENVIADO"));
    }

    @Test
    void criarVeiculoInexistenteRetorna404() throws Exception {
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
    void gerarOrcamentoOsNaoExisteRetorna404() throws Exception {
        String insumoId = salvarInsumo("Oleo de motor", 150.0).getId();
        String servicoId = salvarServico("Troca de oleo", 80.0).getId();

        mockMvc.perform(comToken(post("/ordem-servico/" + UUID.randomUUID() + "/orcamento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(orcamentoRequest(insumoId, servicoId)))))
            .andExpect(status().isNotFound());
    }

    @Test
    void entregarOsNaoFinalizadaRetorna409() throws Exception {
        ClienteJpaEntity c = salvarCliente();
        String veiculoId = cadastrarVeiculo(c.getId());
        String osId = criarOs(veiculoId, c.getId());

        mockMvc.perform(comToken(patch("/ordem-servico/" + osId + "/entregar")))
            .andExpect(status().isConflict());
    }

    @Test
    void criarSemTokenRetorna403() throws Exception {
        CriarOrdemServicoRequest req = new CriarOrdemServicoRequest();
        req.setVeiculoId("v");
        req.setClienteId("c");

        mockMvc.perform(post("/ordem-servico")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(req)))
            .andExpect(status().isForbidden());
    }

    @Test
    void listarPorVeiculoSemOsRetornaListaVazia() throws Exception {
        mockMvc.perform(comToken(get("/ordem-servico/veiculo/" + UUID.randomUUID())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }
}
