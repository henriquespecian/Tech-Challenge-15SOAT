package com.mecanica.oficina_api.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mecanica.oficina_api.application.insumo.InsumosService;
import com.mecanica.oficina_api.interfaces.dto.request.AlterarInsumosRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarInsumosRequest;
import com.mecanica.oficina_api.interfaces.dto.request.ComprarInsumoSimuladoRequest;
import com.mecanica.oficina_api.interfaces.dto.response.InsumosResponse;

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
class InsumosControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private InsumosService insumosService;

    @InjectMocks
    private InsumosController insumosController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(insumosController).build();
    }

    @Test
    void deveListarInsumosERetornar200() throws Exception {
        InsumosResponse insumo = insumoResponse("uuid-1", "Óleo", true);
        when(insumosService.listar()).thenReturn(List.of(insumo));

        mockMvc.perform(get("/insumos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("uuid-1"))
                .andExpect(jsonPath("$[0].nome").value("Óleo"));
    }

    @Test
    void deveListarInsumosVazioERetornar200() throws Exception {
        when(insumosService.listar()).thenReturn(List.of());

        mockMvc.perform(get("/insumos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void deveBuscarInsumoPorIdERetornar200() throws Exception {
        InsumosResponse insumo = insumoResponse("uuid-1", "Óleo", true);
        when(insumosService.buscarPorId("uuid-1")).thenReturn(insumo);

        mockMvc.perform(get("/insumos/uuid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("uuid-1"))
                .andExpect(jsonPath("$.nome").value("Óleo"))
                .andExpect(jsonPath("$.precoUnitario").value(50))
                .andExpect(jsonPath("$.estoqueAtual").value(10))
                .andExpect(jsonPath("$.unidade").value("LITRO"));
    }

    @Test
    void deveRetornar404QuandoInsumoNaoEncontrado() throws Exception {
        when(insumosService.buscarPorId("inexistente"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Insumo não encontrado"));

        mockMvc.perform(get("/insumos/inexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveCadastrarInsumoERetornar201() throws Exception {
        CadastrarInsumosRequest req = new CadastrarInsumosRequest(
                "Filtro de óleo", BigDecimal.valueOf(30), 5, 1, "UNIDADE", null);
        InsumosResponse resp = insumoResponse("uuid-2", "Filtro de óleo", true);
        when(insumosService.cadastrar(any(CadastrarInsumosRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/insumos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("uuid-2"))
                .andExpect(jsonPath("$.nome").value("Filtro de óleo"));
    }

    @Test
    void deveAlterarInsumoERetornar204() throws Exception {
        AlterarInsumosRequest req = new AlterarInsumosRequest(
                "Óleo atualizado", BigDecimal.valueOf(55), 12, 3, "LITRO", null);
        doNothing().when(insumosService).atualizar(eq("uuid-1"), any(AlterarInsumosRequest.class));

        mockMvc.perform(put("/insumos/uuid-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar404AoAlterarInsumoInexistente() throws Exception {
        AlterarInsumosRequest req = new AlterarInsumosRequest(
                "Óleo", BigDecimal.valueOf(50), 10, 2, "LITRO", null);
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Insumo não encontrado"))
                .when(insumosService).atualizar(eq("inexistente"), any(AlterarInsumosRequest.class));

        mockMvc.perform(put("/insumos/inexistente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRegistrarCompraSimuladaERetornar200() throws Exception {
        ComprarInsumoSimuladoRequest req = new ComprarInsumoSimuladoRequest(5);
        InsumosResponse resp = insumoResponse("uuid-1", "Óleo", true);
        when(insumosService.registrarCompraSimulada(eq("uuid-1"), any(ComprarInsumoSimuladoRequest.class)))
                .thenReturn(resp);

        mockMvc.perform(post("/insumos/uuid-1/compra-simulada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("uuid-1"));
    }

    @Test
    void deveAtivarInsumoERetornar200() throws Exception {
        InsumosResponse resp = insumoResponse("uuid-1", "Óleo", true);
        when(insumosService.ativar("uuid-1")).thenReturn(resp);

        mockMvc.perform(patch("/insumos/uuid-1/ativar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("uuid-1"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void deveDesativarInsumoERetornar204() throws Exception {
        doNothing().when(insumosService).deletar("uuid-1");

        mockMvc.perform(delete("/insumos/uuid-1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar404AoDesativarInsumoInexistente() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Insumo não encontrado"))
                .when(insumosService).deletar("inexistente");

        mockMvc.perform(delete("/insumos/inexistente"))
                .andExpect(status().isNotFound());
    }

    // --- helpers ---

    private InsumosResponse insumoResponse(String id, String nome, boolean ativo) {
        return new InsumosResponse(id, nome, BigDecimal.valueOf(50), 10, 2, "LITRO", ativo);
    }
}
