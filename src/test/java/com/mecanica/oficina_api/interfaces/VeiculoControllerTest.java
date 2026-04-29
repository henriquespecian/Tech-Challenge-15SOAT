package com.mecanica.oficina_api.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mecanica.oficina_api.application.veiculo.VeiculoService;
import com.mecanica.oficina_api.interfaces.dto.request.AlterarVeiculoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarVeiculoRequest;
import com.mecanica.oficina_api.interfaces.dto.response.VeiculoResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class VeiculoControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private VeiculoService veiculoService;

    @InjectMocks
    private VeiculoController veiculoController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(veiculoController).build();
    }

    @Test
    void deveCadastrarVeiculoERetornar201() throws Exception {
        CadastrarVeiculoRequest request = new CadastrarVeiculoRequest();
        request.setClienteId("cliente-1");
        request.setPlaca("ABC1234");
        request.setMarca("Toyota");
        request.setModelo("Corolla");
        request.setAno(2020);
        request.setCor("Branco");

        mockMvc.perform(post("/veiculo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void deveBuscarVeiculoPorIdERetornar200() throws Exception {
        VeiculoResponse response = new VeiculoResponse("veiculo-1", "cliente-1", "ABC1234", "Toyota", "Corolla", 2020, "Branco");
        when(veiculoService.buscarPorId("veiculo-1")).thenReturn(response);

        mockMvc.perform(get("/veiculo/veiculo-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("veiculo-1"))
                .andExpect(jsonPath("$.placa").value("ABC1234"));
    }

    @Test
    void deveListarVeiculosPorClienteERetornar200() throws Exception {
        List<VeiculoResponse> responses = List.of(
                new VeiculoResponse("veiculo-1", "cliente-1", "ABC1234", "Toyota", "Corolla", 2020, "Branco"),
                new VeiculoResponse("veiculo-2", "cliente-1", "XYZ5678", "Honda", "Civic", 2022, "Preto")
        );
        when(veiculoService.listarPorCliente("cliente-1")).thenReturn(responses);

        mockMvc.perform(get("/veiculo/cliente/cliente-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].placa").value("ABC1234"))
                .andExpect(jsonPath("$[1].placa").value("XYZ5678"));
    }

    @Test
    void deveAlterarVeiculoERetornar200() throws Exception {
        AlterarVeiculoRequest request = new AlterarVeiculoRequest();
        request.setPlaca("XYZ9999");
        request.setMarca("Honda");
        request.setModelo("Civic");
        request.setAno(2023);
        request.setCor("Preto");

        VeiculoResponse response = new VeiculoResponse("veiculo-1", "cliente-1", "XYZ9999", "Honda", "Civic", 2023, "Preto");
        when(veiculoService.alterar("veiculo-1", request)).thenReturn(response);

        mockMvc.perform(put("/veiculo/veiculo-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("veiculo-1"))
                .andExpect(jsonPath("$.placa").value("XYZ9999"))
                .andExpect(jsonPath("$.marca").value("Honda"));
    }

    @Test
    void deveRetornarListaVaziaQuandoClienteNaoTemVeiculos() throws Exception {
        when(veiculoService.listarPorCliente(anyString())).thenReturn(List.of());

        mockMvc.perform(get("/veiculo/cliente/cliente-sem-veiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
