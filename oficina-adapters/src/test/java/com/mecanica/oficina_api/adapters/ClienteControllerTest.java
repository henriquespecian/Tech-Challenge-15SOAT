package com.mecanica.oficina_api.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mecanica.oficina_api.adapters.web.ClienteController;
import com.mecanica.oficina_api.adapters.web.dto.request.CadastrarClienteRequest;
import com.mecanica.oficina_api.adapters.web.dto.response.ConsultarClienteResponse;
import com.mecanica.oficina_api.application.cliente.ClienteService;
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

import java.util.List;

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

    // CPF matematicamente válido
    private static final String CPF_VALIDO = "52998224725";

    private ConsultarClienteResponse clienteResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clienteController).build();

        clienteResponse = new ConsultarClienteResponse(
            "cliente-1", "João Silva", CPF_VALIDO, "joao@email.com", "11999999999"
        );
    }

    @Test
    void deveCadastrarClienteERetornar201() throws Exception {
        CadastrarClienteRequest request = new CadastrarClienteRequest();
        request.setNome("João Silva");
        request.setDocumento(CPF_VALIDO);
        request.setEmail("joao@email.com");
        request.setTelefone("11999999999");

        when(clienteService.cadastrar("João Silva", CPF_VALIDO, "joao@email.com", "11999999999")).thenReturn(clienteResponse);

        mockMvc.perform(post("/cliente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("cliente-1"))
            .andExpect(jsonPath("$.nome").value("João Silva"))
            .andExpect(jsonPath("$.documento").value(CPF_VALIDO))
            .andExpect(jsonPath("$.email").value("joao@email.com"))
            .andExpect(jsonPath("$.telefone").value("11999999999"));
    }

    @Test
    void deveConsultarClienteERetornar200ComId() throws Exception {
        when(clienteService.consultar(CPF_VALIDO)).thenReturn(clienteResponse);

        mockMvc.perform(get("/cliente/" + CPF_VALIDO))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("cliente-1"))
            .andExpect(jsonPath("$.nome").value("João Silva"))
            .andExpect(jsonPath("$.documento").value(CPF_VALIDO))
            .andExpect(jsonPath("$.email").value("joao@email.com"))
            .andExpect(jsonPath("$.telefone").value("11999999999"));
    }

    @Test
    void deveListarClientesERetornar200() throws Exception {
        when(clienteService.listar()).thenReturn(List.of(clienteResponse));

        mockMvc.perform(get("/cliente"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("cliente-1"))
            .andExpect(jsonPath("$[0].nome").value("João Silva"))
            .andExpect(jsonPath("$[0].documento").value(CPF_VALIDO))
            .andExpect(jsonPath("$[0].email").value("joao@email.com"))
            .andExpect(jsonPath("$[0].telefone").value("11999999999"));
    }

    @Test
    void deveRetornar404QuandoClienteNaoEncontrado() throws Exception {
        when(clienteService.consultar("99999999999"))
            .thenThrow(new ResponseStatusException(NOT_FOUND, "Cliente não encontrado"));

        mockMvc.perform(get("/cliente/99999999999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void deveAlterarClienteERetornar204() throws Exception {
        AlterarClienteRequest request = new AlterarClienteRequest();
        request.setNome("João Alterado");
        request.setEmail("joao.novo@email.com");
        request.setTelefone("11888888888");

        mockMvc.perform(put("/cliente/" + CPF_VALIDO)
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

        doThrow(new ResponseStatusException(NOT_FOUND, "Cliente não encontrado"))
            .when(clienteService).alterar(eq("99999999999"), any(AlterarClienteRequest.class));

        mockMvc.perform(put("/cliente/99999999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deveDeletarClienteERetornar204() throws Exception {
        mockMvc.perform(delete("/cliente/" + CPF_VALIDO))
            .andExpect(status().isNoContent());
    }
}
