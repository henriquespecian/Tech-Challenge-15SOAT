package com.mecanica.oficina_api.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mecanica.oficina_api.application.ordemservico.OrdemServicoService;
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
    void deveCriarOrdemServicoERetornar201() throws Exception {
        CriarOrdemServicoRequest req = new CriarOrdemServicoRequest();
        req.setVeiculoId("veiculo-1");
        req.setClienteId("cliente-1");

        mockMvc.perform(post("/ordem-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void deveBuscarPorIdERetornar200() throws Exception {
        OrdemServicoResponse resp = osResponse("os-1", "EM_TRIAGEM", null);
        when(ordemServicoService.buscarPorId("os-1")).thenReturn(resp);

        mockMvc.perform(get("/ordem-servico/os-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("os-1"))
                .andExpect(jsonPath("$.status").value("EM_TRIAGEM"))
                .andExpect(jsonPath("$.orcamento").doesNotExist());
    }

    @Test
    void deveListarPorVeiculoERetornar200() throws Exception {
        when(ordemServicoService.listarPorVeiculo("veiculo-1"))
                .thenReturn(List.of(osResponse("os-1", "EM_TRIAGEM", null)));

        mockMvc.perform(get("/ordem-servico/veiculo/veiculo-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void deveGerarOrcamentoERetornar200() throws Exception {
        OrcamentoResponse orc = new OrcamentoResponse("PENDENTE",
                List.of(new ItemOrcamentoResponse("Troca de óleo", 1, BigDecimal.valueOf(100), BigDecimal.valueOf(100))),
                BigDecimal.valueOf(100), "obs");
        when(ordemServicoService.gerarOrcamento(eq("os-1"), any())).thenReturn(osResponse("os-1", "EM_TRIAGEM", orc));

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
        OrcamentoResponse orc = new OrcamentoResponse("ENVIADO", List.of(), BigDecimal.ZERO, null);
        when(ordemServicoService.enviarOrcamento("os-1")).thenReturn(osResponse("os-1", "EM_TRIAGEM", orc));

        mockMvc.perform(patch("/ordem-servico/os-1/orcamento/enviar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orcamento.status").value("ENVIADO"));
    }

    @Test
    void deveAprovarOrcamentoERetornar200() throws Exception {
        OrcamentoResponse orc = new OrcamentoResponse("APROVADO", List.of(), BigDecimal.ZERO, null);
        when(ordemServicoService.aprovarOrcamento("os-1")).thenReturn(osResponse("os-1", "FINALIZADO", orc));

        mockMvc.perform(patch("/ordem-servico/os-1/orcamento/aprovar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FINALIZADO"))
                .andExpect(jsonPath("$.orcamento.status").value("APROVADO"));
    }

    @Test
    void deveRetirarVeiculoERetornar200() throws Exception {
        OrcamentoResponse orc = new OrcamentoResponse("APROVADO", List.of(), BigDecimal.ZERO, null);
        when(ordemServicoService.retirarVeiculo("os-1")).thenReturn(osResponse("os-1", "VEICULO_RETIRADO", orc));

        mockMvc.perform(patch("/ordem-servico/os-1/veiculo/retirar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VEICULO_RETIRADO"));
    }

    // --- helpers ---

    private OrdemServicoResponse osResponse(String id, String status, OrcamentoResponse orcamento) {
        return new OrdemServicoResponse(id, "veiculo-1", "cliente-1", status, orcamento);
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
