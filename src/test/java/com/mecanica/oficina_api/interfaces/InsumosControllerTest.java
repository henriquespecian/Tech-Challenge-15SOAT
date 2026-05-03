package com.mecanica.oficina_api.interfaces;

import com.mecanica.oficina_api.application.insumo.InsumosService;
import com.mecanica.oficina_api.interfaces.dto.response.InsumosResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class InsumosControllerTest {

    private MockMvc mockMvc;

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
        InsumosResponse insumo = new InsumosResponse("uuid-1", "Óleo", BigDecimal.valueOf(50), 10, 2, "LITRO", true);
        when(insumosService.listar()).thenReturn(List.of(insumo));

        mockMvc.perform(get("/insumos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("uuid-1"))
                .andExpect(jsonPath("$[0].nome").value("Óleo"));
    }

    @Test
    void deveBuscarInsumoPorIdERetornar200() throws Exception {
        InsumosResponse insumo = new InsumosResponse("uuid-1", "Óleo", BigDecimal.valueOf(50), 10, 2, "LITRO", true);
        when(insumosService.buscarPorId("uuid-1")).thenReturn(insumo);

        mockMvc.perform(get("/insumos/uuid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("uuid-1"))
                .andExpect(jsonPath("$.nome").value("Óleo"));
    }

    @Test
    void deveRetornar404QuandoInsumoNaoEncontrado() throws Exception {
        when(insumosService.buscarPorId("inexistente"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Insumo não encontrado"));

        mockMvc.perform(get("/insumos/inexistente"))
                .andExpect(status().isNotFound());
    }
}
