package com.mecanica.oficina_api.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mecanica.oficina_api.application.ordemservico.OrdemServicoService;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServicoStatus;
import com.mecanica.oficina_api.interfaces.dto.request.CriarOrdemServicoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.GerarOrcamentoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.ItemOrcamentoRequest;
import com.mecanica.oficina_api.interfaces.dto.response.ItemOrcamentoResponse;
import com.mecanica.oficina_api.interfaces.dto.response.OrcamentoResponse;
import com.mecanica.oficina_api.interfaces.dto.response.OrdemServicoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrdemServicoControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock private OrdemServicoService ordemServicoService;
    @InjectMocks private OrdemServicoController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void deveListarTodasAsOsERetornar200() throws Exception {
        when(ordemServicoService.listar(null))
                .thenReturn(List.of(osResponse("os-1", "RECEBIDA", null), osResponse("os-2", "EM_DIAGNOSTICO", null)));

        mockMvc.perform(get("/ordem-servico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deveListarOsComFiltroStatusERetornar200() throws Exception {
        when(ordemServicoService.listar(OrdemServicoStatus.RECEBIDA))
                .thenReturn(List.of(osResponse("os-1", "RECEBIDA", null)));

        mockMvc.perform(get("/ordem-servico").param("status", "RECEBIDA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("RECEBIDA"));
    }

    @Test
    void deveCriarOrdemServicoERetornar201() throws Exception {
        CriarOrdemServicoRequest req = new CriarOrdemServicoRequest();
        req.setVeiculoId("veiculo-1");
        req.setClienteId("cliente-1");

        when(ordemServicoService.criar(any(CriarOrdemServicoRequest.class)))
                .thenReturn(osResponse("os-1", "RECEBIDA", null));

        mockMvc.perform(post("/ordem-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("os-1"))
                .andExpect(jsonPath("$.status").value("RECEBIDA"));
    }

    @Test
    void deveBuscarPorIdERetornar200() throws Exception {
        OrdemServicoResponse resp = osResponse("os-1", "RECEBIDA", null);
        when(ordemServicoService.buscarPorId("os-1")).thenReturn(resp);

        mockMvc.perform(get("/ordem-servico/os-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("os-1"))
                .andExpect(jsonPath("$.status").value("RECEBIDA"))
                .andExpect(jsonPath("$.orcamento").doesNotExist());
    }

    @Test
    void deveListarPorVeiculoERetornar200() throws Exception {
        when(ordemServicoService.listarPorVeiculo("veiculo-1"))
                .thenReturn(List.of(osResponse("os-1", "RECEBIDA", null)));

        mockMvc.perform(get("/ordem-servico/veiculo/veiculo-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void deveGerarOrcamentoERetornar200() throws Exception {
        OrcamentoResponse orc = new OrcamentoResponse("PENDENTE",
                List.of(new ItemOrcamentoResponse("Troca de óleo", 1, BigDecimal.valueOf(100), BigDecimal.valueOf(100))),
                BigDecimal.valueOf(100), "obs", null);
        when(ordemServicoService.gerarOrcamento(eq("os-1"), any()))
                .thenReturn(osResponse("os-1", "EM_DIAGNOSTICO", orc));

        GerarOrcamentoRequest req = gerarOrcamentoRequest();

        mockMvc.perform(post("/ordem-servico/os-1/orcamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orcamento.status").value("PENDENTE"))
                .andExpect(jsonPath("$.orcamento.valorTotal").value(100));
    }

    @Test
    void deveEnviarOrcamentoERetornar200() throws Exception {
        OrcamentoResponse orc = new OrcamentoResponse("ENVIADO", List.of(), BigDecimal.ZERO, null, null);
        when(ordemServicoService.enviarOrcamento("os-1"))
                .thenReturn(osResponse("os-1", "AGUARDANDO_APROVACAO", orc));

        mockMvc.perform(patch("/ordem-servico/os-1/orcamento/enviar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orcamento.status").value("ENVIADO"));
    }

    @Test
    void deveAprovarOrcamentoERetornar200() throws Exception {
        OrcamentoResponse orc = new OrcamentoResponse("APROVADO", List.of(), BigDecimal.ZERO, null, null);
        when(ordemServicoService.aprovarOrcamento("os-1"))
                .thenReturn(osResponse("os-1", "AGUARDANDO_APROVACAO", orc));

        mockMvc.perform(patch("/ordem-servico/os-1/orcamento/aprovar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AGUARDANDO_APROVACAO"))
                .andExpect(jsonPath("$.orcamento.status").value("APROVADO"));
    }

    @Test
    void deveIniciarExecucaoERetornar200() throws Exception {
        OrcamentoResponse orc = new OrcamentoResponse("APROVADO", List.of(), BigDecimal.ZERO, null, null);
        when(ordemServicoService.iniciarExecucao("os-1"))
                .thenReturn(osResponse("os-1", "EM_EXECUCAO", orc));

        mockMvc.perform(patch("/ordem-servico/os-1/iniciar-execucao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_EXECUCAO"));
    }

    @Test
    void deveEntregarVeiculoERetornar200() throws Exception {
        OrcamentoResponse orc = new OrcamentoResponse("APROVADO", List.of(), BigDecimal.ZERO, null, null);
        when(ordemServicoService.entregar("os-1"))
                .thenReturn(osResponse("os-1", "ENTREGUE", orc));

        mockMvc.perform(patch("/ordem-servico/os-1/entregar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENTREGUE"));
    }

    // --- helpers ---

    private OrdemServicoResponse osResponse(String id, String status, OrcamentoResponse orcamento) {
        return new OrdemServicoResponse(id, "veiculo-1", "cliente-1", status, null, null, orcamento);
    }

    private GerarOrcamentoRequest gerarOrcamentoRequest() {
        ItemOrcamentoRequest item = new ItemOrcamentoRequest();
        item.setInsumoId("insumo-1");
        item.setQuantidade(1);

        GerarOrcamentoRequest req = new GerarOrcamentoRequest();
        req.setInsumos(List.of(item));
        req.setObservacoes("obs");
        return req;
    }
}
