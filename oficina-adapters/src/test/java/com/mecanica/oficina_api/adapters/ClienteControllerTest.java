package com.mecanica.oficina_api.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mecanica.oficina_api.adapters.common.FieldError;
import com.mecanica.oficina_api.adapters.common.FileUtils;
import com.mecanica.oficina_api.adapters.common.ValidationErrorResponse;
import com.mecanica.oficina_api.adapters.web.ClienteController;
import com.mecanica.oficina_api.adapters.web.GlobalExceptionHandler;
import com.mecanica.oficina_api.adapters.web.dto.request.AlterarClienteRequest;
import com.mecanica.oficina_api.adapters.web.dto.request.CadastrarClienteRequest;
import com.mecanica.oficina_api.adapters.web.presenter.ClientePresenter;
import com.mecanica.oficina_api.application.cliente.usecase.AlterarClienteUseCase;
import com.mecanica.oficina_api.application.cliente.usecase.CadastrarClienteUseCase;
import com.mecanica.oficina_api.application.cliente.usecase.ConsultarClienteUseCase;
import com.mecanica.oficina_api.application.cliente.usecase.DeletarClienteUseCase;
import com.mecanica.oficina_api.application.cliente.usecase.ListarClientesUseCase;
import com.mecanica.oficina_api.domain.cliente.Cliente;
import com.mecanica.oficina_api.domain.cliente.Cpf;
import com.mecanica.oficina_api.domain.cliente.Email;
import com.mecanica.oficina_api.domain.cliente.Telefone;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FileUtils fileUtils = new FileUtils();

    @Mock
    private CadastrarClienteUseCase cadastrarClienteUseCase;
    @Mock
    private ConsultarClienteUseCase consultarClienteUseCase;
    @Mock
    private ListarClientesUseCase listarClientesUseCase;
    @Mock
    private AlterarClienteUseCase alterarClienteUseCase;
    @Mock
    private DeletarClienteUseCase deletarClienteUseCase;



    private static final String CPF_VALIDO = "52998224725";

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        ClienteController controller = new ClienteController(
                cadastrarClienteUseCase, consultarClienteUseCase, listarClientesUseCase,
                alterarClienteUseCase, deletarClienteUseCase, new ClientePresenter());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        cliente = Cliente.reconstituir(
                "cliente-1", "João Silva",
                new Cpf(CPF_VALIDO), new Email("joao@email.com"), new Telefone("11999999999"),
                LocalDateTime.now(), null);
    }

    @Test
    void deveCadastrarClienteERetornar201() throws Exception {
        when(cadastrarClienteUseCase.executar("João Silva", CPF_VALIDO, "joao@email.com", "11999999999"))
                .thenReturn(cliente);

        mockMvc.perform(post("/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cadastrarRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("cliente-1"))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.documento").value(CPF_VALIDO))
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.telefone").value("11999999999"));
    }

    @Test
    void deveListarClientesERetornar200() throws Exception {
        when(listarClientesUseCase.executar()).thenReturn(List.of(cliente));

        mockMvc.perform(get("/cliente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("cliente-1"))
                .andExpect(jsonPath("$[0].documento").value(CPF_VALIDO));
    }

    @Test
    void deveConsultarClienteERetornar200() throws Exception {
        when(consultarClienteUseCase.executar(CPF_VALIDO)).thenReturn(Optional.of(cliente));

        mockMvc.perform(get("/cliente/" + CPF_VALIDO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("cliente-1"))
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @ParameterizedTest
    @MethodSource("dadosCamposInvalidos")
    void deveRetornar400_quandoCamposEstaoInvalidos(String filename, List<String> errors) throws Exception {
        var request = fileUtils.readResourceFile("/clientes/%s".formatted(filename));

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/cliente")
                .content(request)
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();

        var responseError = objectMapper.readValue(mvcResult, ValidationErrorResponse.class);

        Assertions.assertThat(responseError.erros())
            .extracting(FieldError::mensagem)
            .containsExactlyInAnyOrderElementsOf(errors);
    }

    @Test
    void deveRetornar404_quandoClienteNaoEncontrado() throws Exception {
        when(consultarClienteUseCase.executar("99999999999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/cliente/99999999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAlterarClienteERetornar204() throws Exception {
        mockMvc.perform(put("/cliente/" + CPF_VALIDO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alterarRequest())))
                .andExpect(status().isNoContent());

        verify(alterarClienteUseCase).executar(CPF_VALIDO, "João Alterado", "joao.novo@email.com", "11888888888");
    }

    @Test
    void deveRetornar404AoAlterar_quandoUseCaseLancaIllegalArgument() throws Exception {
        doThrow(new IllegalArgumentException("Cliente não encontrado"))
                .when(alterarClienteUseCase).executar(eq("99999999999"), any(), any(), any());

        mockMvc.perform(put("/cliente/99999999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alterarRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveDeletarClienteERetornar204() throws Exception {
        mockMvc.perform(delete("/cliente/" + CPF_VALIDO))
                .andExpect(status().isNoContent());

        verify(deletarClienteUseCase).executar(CPF_VALIDO);
    }

    private CadastrarClienteRequest cadastrarRequest() {
        CadastrarClienteRequest request = new CadastrarClienteRequest();
        request.setNome("João Silva");
        request.setDocumento(CPF_VALIDO);
        request.setEmail("joao@email.com");
        request.setTelefone("11999999999");
        return request;
    }

    private AlterarClienteRequest alterarRequest() {
        AlterarClienteRequest request = new AlterarClienteRequest();
        request.setNome("João Alterado");
        request.setEmail("joao.novo@email.com");
        request.setTelefone("11888888888");
        return request;
    }

    private static Stream<Arguments> dadosCamposInvalidos() {
        var errosComuns = errosComuns();

        var erroCampoEmailInvalido = new ArrayList<>(List.of("E-mail inválido"));

        return Stream.of(
            Arguments.of("cadastrar-clientes-400-campos-nulos.json", errosComuns),
            Arguments.of("cadastrar-clientes-400-campos-vazios.json", errosComuns),
            Arguments.of("cadastrar-clientes-400-email-invalido.json", erroCampoEmailInvalido)
        );
    }

    private static List<String> errosComuns() {
        var erroCampoNome = "O nome é obrigatório";
        var erroCampoEmail = "O e-mail é obrigatório";
        var erroDocumento = "O documento é obrigatório";
        var erroTelefone = "O telefone é obrigatório";

        return new ArrayList<>(List.of(erroCampoNome, erroCampoEmail, erroDocumento, erroTelefone));
    }
}
