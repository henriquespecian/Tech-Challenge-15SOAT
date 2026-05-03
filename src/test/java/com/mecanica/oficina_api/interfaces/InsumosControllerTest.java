package com.mecanica.oficina_api.interfaces;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mecanica.oficina_api.application.insumo.InsumosService;
import com.mecanica.oficina_api.interfaces.dto.request.AlterarInsumosRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarInsumosRequest;
import com.mecanica.oficina_api.interfaces.dto.response.InsumosResponse;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        mockMvc = MockMvcBuilders.standaloneSetup(insumosController).build();
    }

    @Test
    void deveListarInsumosERetornar200() throws Exception {
        when(insumosService.listar()).thenReturn(List.of(
            insumoResponse("insumo-1", "Oleo 5W30", true),
            insumoResponse("insumo-2", "Filtro de Ar", true)
        ));

        mockMvc.perform(get("/insumos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value("insumo-1"))
            .andExpect(jsonPath("$[0].nome").value("Oleo 5W30"))
            .andExpect(jsonPath("$[1].nome").value("Filtro de Ar"));
    }

    @Test
    void deveCadastrarInsumoERetornar201() throws Exception {
        CadastrarInsumosRequest request = new CadastrarInsumosRequest(
            "Oleo 5W30",
            BigDecimal.valueOf(39.90),
            20,
            5,
            "UN",
            true
        );

        when(insumosService.cadastrar(any(CadastrarInsumosRequest.class)))
            .thenReturn(insumoResponse("insumo-1", "Oleo 5W30", true));

        mockMvc.perform(post("/insumos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("insumo-1"))
            .andExpect(jsonPath("$.nome").value("Oleo 5W30"))
            .andExpect(jsonPath("$.precoUnitario").value(39.9))
            .andExpect(jsonPath("$.estoqueAtual").value(20))
            .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void deveAlterarInsumoERetornar204() throws Exception {
        AlterarInsumosRequest request = new AlterarInsumosRequest(
            "Filtro de Oleo",
            BigDecimal.valueOf(25.50),
            15,
            4,
            "UN",
            true
        );

        mockMvc.perform(put("/insumos/insumo-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar404AoAlterarInsumoInexistente() throws Exception {
        AlterarInsumosRequest request = new AlterarInsumosRequest(
            "Filtro de Oleo",
            BigDecimal.valueOf(25.50),
            15,
            4,
            "UN",
            true
        );

        doThrow(new ResponseStatusException(NOT_FOUND, "Insumo não encontrado"))
            .when(insumosService).atualizar(eq("inexistente"), any(AlterarInsumosRequest.class));

        mockMvc.perform(put("/insumos/inexistente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deveAtivarInsumoERetornar200() throws Exception {
        when(insumosService.ativar("insumo-1"))
            .thenReturn(insumoResponse("insumo-1", "Pastilha de Freio", true));

        mockMvc.perform(patch("/insumos/insumo-1/ativar"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("insumo-1"))
            .andExpect(jsonPath("$.nome").value("Pastilha de Freio"))
            .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void deveDesativarInsumoERetornar204() throws Exception {
        mockMvc.perform(delete("/insumos/insumo-1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar404AoDesativarInsumoInexistente() throws Exception {
        doThrow(new ResponseStatusException(NOT_FOUND, "Insumo não encontrado"))
            .when(insumosService).deletar("inexistente");

        mockMvc.perform(delete("/insumos/inexistente"))
            .andExpect(status().isNotFound());
    }

    private InsumosResponse insumoResponse(String id, String nome, boolean ativo) {
        return new InsumosResponse(
            id,
            nome,
            BigDecimal.valueOf(39.90),
            20,
            5,
            "UN",
            ativo
        );
    }
}
