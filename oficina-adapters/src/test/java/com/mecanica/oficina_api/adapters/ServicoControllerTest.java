package com.mecanica.oficina_api.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mecanica.oficina_api.application.servico.ServicoService;
import com.mecanica.oficina_api.interfaces.dto.request.AlterarServicoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarServicoRequest;
import com.mecanica.oficina_api.interfaces.dto.response.ServicoResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ServicoControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ServicoService servicoService;

    @InjectMocks
    private ServicoController servicoController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(servicoController).build();
    }

    @Test
    void deveCadastrarServicoERetornar201() throws Exception {
        CadastrarServicoRequest req = cadastrarRequest("Troca de óleo", "Troca completa do óleo do motor", 150.0, 1);
        ServicoResponse resp = servicoResponse("srv-1", "Troca de óleo", true);
        when(servicoService.cadastrar(any(CadastrarServicoRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("srv-1"))
                .andExpect(jsonPath("$.nome").value("Troca de óleo"));
    }

    @Test
    void deveBuscarServicoPorIdERetornar200() throws Exception {
        ServicoResponse resp = servicoResponse("srv-1", "Troca de óleo", true);
        when(servicoService.buscar("srv-1")).thenReturn(resp);

        mockMvc.perform(get("/servico/srv-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("srv-1"))
                .andExpect(jsonPath("$.nome").value("Troca de óleo"))
                .andExpect(jsonPath("$.preco").value(150))
                .andExpect(jsonPath("$.tempoEstimadoHoras").value(1))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void deveRetornar404QuandoServicoNaoEncontrado() throws Exception {
        when(servicoService.buscar("inexistente"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado"));

        mockMvc.perform(get("/servico/inexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveListarServicosERetornar200() throws Exception {
        when(servicoService.listar()).thenReturn(List.of(
                servicoResponse("srv-1", "Troca de óleo", true),
                servicoResponse("srv-2", "Alinhamento", true)));

        mockMvc.perform(get("/servico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("srv-1"))
                .andExpect(jsonPath("$[1].id").value("srv-2"));
    }

    @Test
    void deveListarServicosVazioERetornar200() throws Exception {
        when(servicoService.listar()).thenReturn(List.of());

        mockMvc.perform(get("/servico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void deveAlterarServicoERetornar200() throws Exception {
        AlterarServicoRequest req = alterarRequest("Troca de óleo sintético", "Óleo sintético 5W30", 180.0, 2);
        ServicoResponse resp = servicoResponse("srv-1", "Troca de óleo sintético", true);
        when(servicoService.alterar(eq("srv-1"), any(AlterarServicoRequest.class))).thenReturn(resp);

        mockMvc.perform(put("/servico/srv-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("srv-1"))
                .andExpect(jsonPath("$.nome").value("Troca de óleo sintético"));
    }

    @Test
    void deveRetornar404AoAlterarServicoInexistente() throws Exception {
        AlterarServicoRequest req = alterarRequest("Troca de óleo", "Desc", 150.0, 1);
        when(servicoService.alterar(eq("inexistente"), any(AlterarServicoRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado"));

        mockMvc.perform(put("/servico/inexistente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAtivarServicoERetornar200() throws Exception {
        ServicoResponse resp = servicoResponse("srv-1", "Troca de óleo", true);
        when(servicoService.ativar("srv-1")).thenReturn(resp);

        mockMvc.perform(patch("/servico/srv-1/ativar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("srv-1"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void deveInativarServicoERetornar204() throws Exception {
        doNothing().when(servicoService).inativar("srv-1");

        mockMvc.perform(delete("/servico/srv-1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar404AoInativarServicoInexistente() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado"))
                .when(servicoService).inativar("inexistente");

        mockMvc.perform(delete("/servico/inexistente"))
                .andExpect(status().isNotFound());
    }

    // --- helpers ---

    private ServicoResponse servicoResponse(String id, String nome, boolean ativo) {
        return new ServicoResponse(id, nome, "Descrição padrão", BigDecimal.valueOf(150), 1, ativo);
    }

    private CadastrarServicoRequest cadastrarRequest(String nome, String descricao, double preco, int horas) {
        CadastrarServicoRequest req = new CadastrarServicoRequest();
        req.setNome(nome);
        req.setDescricao(descricao);
        req.setPreco(BigDecimal.valueOf(preco));
        req.setTempoEstimadoHoras(horas);
        return req;
    }

    private AlterarServicoRequest alterarRequest(String nome, String descricao, double preco, int horas) {
        AlterarServicoRequest req = new AlterarServicoRequest();
        req.setNome(nome);
        req.setDescricao(descricao);
        req.setPreco(BigDecimal.valueOf(preco));
        req.setTempoEstimadoHoras(horas);
        return req;
    }
}
