package com.mecanica.oficina_api.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mecanica.oficina_api.application.cliente.ClienteService;
import com.mecanica.oficina_api.interfaces.dto.request.AlterarClienteRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarClienteRequest;
import com.mecanica.oficina_api.interfaces.dto.response.ConsultarClienteResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    private ConsultarClienteResponse clienteResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clienteController).build();

        clienteResponse = new ConsultarClienteResponse(
            "cliente-1", "João Silva", "12345678900", "joao@email.com", "11999999999"
        );
    }

    @Test
    void deveCadastrarClienteERetornar201() throws Exception {
        CadastrarClienteRequest request = new CadastrarClienteRequest();
        request.setNome("João Silva");
        request.setCpf("12345678900");
        request.setEmail("joao@email.com");
        request.setTelefone("11999999999");

        mockMvc.perform(post("/cliente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void deveConsultarClienteERetornar200ComId() throws Exception {
        when(clienteService.consultar("12345678900")).thenReturn(clienteResponse);

        mockMvc.perform(get("/cliente/12345678900"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("cliente-1"))
            .andExpect(jsonPath("$.nome").value("João Silva"))
            .andExpect(jsonPath("$.cpf").value("12345678900"))
            .andExpect(jsonPath("$.email").value("joao@email.com"))
            .andExpect(jsonPath("$.telefone").value("11999999999"));
    }

    @Test
    void deveRetornar404QuandoClienteNaoEncontrado() throws Exception {
        when(clienteService.consultar("00000000000"))
            .thenThrow(new ResponseStatusException(NOT_FOUND, "CPF inexistente"));

        mockMvc.perform(get("/cliente/00000000000"))
            .andExpect(status().isNotFound());
    }

    @Test
    void deveAlterarClienteERetornar204() throws Exception {
        AlterarClienteRequest request = new AlterarClienteRequest();
        request.setNome("João Alterado");
        request.setEmail("joao.novo@email.com");
        request.setTelefone("11888888888");

        mockMvc.perform(put("/cliente/12345678900")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar404AoAlterarClienteInexistente() throws Exception {
        AlterarClienteRequest request = new AlterarClienteRequest();
        request.setNome("João Alterado");
        request.setEmail("joao.novo@email.com");
        request.setTelefone("11888888888");

        doThrow(new ResponseStatusException(NOT_FOUND, "CPF inexistente"))
            .when(clienteService).alterar(eq("00000000000"), any(AlterarClienteRequest.class));

        mockMvc.perform(put("/cliente/00000000000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deveDeletarClienteERetornar204() throws Exception {
        mockMvc.perform(delete("/cliente/12345678900"))
            .andExpect(status().isNoContent());
    }
}
