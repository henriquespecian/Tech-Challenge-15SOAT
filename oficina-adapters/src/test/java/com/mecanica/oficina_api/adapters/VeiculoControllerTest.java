package com.mecanica.oficina_api.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mecanica.oficina_api.adapters.common.FieldError;
import com.mecanica.oficina_api.adapters.common.FileUtils;
import com.mecanica.oficina_api.adapters.common.ValidationErrorResponse;
import com.mecanica.oficina_api.adapters.web.VeiculoController;
import com.mecanica.oficina_api.adapters.web.GlobalExceptionHandler;
import com.mecanica.oficina_api.adapters.web.dto.request.AlterarVeiculoRequest;
import com.mecanica.oficina_api.adapters.web.dto.request.CadastrarVeiculoRequest;
import com.mecanica.oficina_api.adapters.web.presenter.VeiculoPresenter;
import com.mecanica.oficina_api.application.veiculo.usecase.AlterarVeiculoUseCase;
import com.mecanica.oficina_api.application.veiculo.usecase.CadastrarVeiculoUseCase;
import com.mecanica.oficina_api.application.veiculo.usecase.ConsultarVeiculoUseCase;
import com.mecanica.oficina_api.application.veiculo.usecase.InativarVeiculoUseCase;
import com.mecanica.oficina_api.application.veiculo.usecase.ListarVeiculosPorClienteUseCase;
import com.mecanica.oficina_api.domain.veiculo.Veiculo;

import java.util.List;

import java.util.UUID;
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

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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
class VeiculoControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FileUtils fileUtils = new FileUtils();

    @Mock
    private CadastrarVeiculoUseCase cadastrarVeiculoUseCase;
    @Mock
    private ConsultarVeiculoUseCase consultarVeiculoUseCase;
    @Mock
    private ListarVeiculosPorClienteUseCase listarVeiculosPorClienteUseCase;
    @Mock
    private AlterarVeiculoUseCase alterarVeiculoUseCase;
    @Mock
    private InativarVeiculoUseCase inativarVeiculoUseCase;

    private static final String PLACA_VALIDA = "ABC1D23";

    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        VeiculoController controller = new VeiculoController(
                cadastrarVeiculoUseCase, consultarVeiculoUseCase, listarVeiculosPorClienteUseCase,
                alterarVeiculoUseCase, inativarVeiculoUseCase, new VeiculoPresenter());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        veiculo = Veiculo.reconstituir(
                "veiculo-1", "cliente-1", PLACA_VALIDA, "Ford", "Ka", 2020, "Prata", true);
    }

    @Test
    void deveCadastrarVeiculoERetornar201() throws Exception {
        when(cadastrarVeiculoUseCase.executar("cliente-1", PLACA_VALIDA, "Ford", "Ka", 2020, "Prata"))
                .thenReturn(veiculo);

        mockMvc.perform(post("/veiculo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cadastrarRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("veiculo-1"))
                .andExpect(jsonPath("$.clienteId").value("cliente-1"))
                .andExpect(jsonPath("$.placa").value(PLACA_VALIDA))
                .andExpect(jsonPath("$.marca").value("Ford"))
                .andExpect(jsonPath("$.modelo").value("Ka"))
                .andExpect(jsonPath("$.ano").value(2020))
                .andExpect(jsonPath("$.cor").value("Prata"));

        verify(cadastrarVeiculoUseCase).executar("cliente-1", PLACA_VALIDA, "Ford", "Ka", 2020, "Prata");
    }

    @Test
    void deveBuscarVeiculoPorIdERetornar200() throws Exception {
        when(consultarVeiculoUseCase.executar("veiculo-1")).thenReturn(veiculo);

        mockMvc.perform(get("/veiculo/veiculo-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("veiculo-1"))
                .andExpect(jsonPath("$.placa").value(PLACA_VALIDA));
    }

    @Test
    void deveRetornar404_quandoVeiculoNaoEncontrado() throws Exception {
        when(consultarVeiculoUseCase.executar("inexistente"))
                .thenThrow(new IllegalArgumentException("Veículo não encontrado: inexistente"));

        mockMvc.perform(get("/veiculo/inexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveListarVeiculosPorClienteERetornar200() throws Exception {
        when(listarVeiculosPorClienteUseCase.executar("cliente-1")).thenReturn(List.of(veiculo));

        mockMvc.perform(get("/veiculo/cliente/cliente-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("veiculo-1"))
                .andExpect(jsonPath("$[0].clienteId").value("cliente-1"));
    }

    @Test
    void deveAlterarVeiculoERetornar200() throws Exception {
        Veiculo alterado = Veiculo.reconstituir(
                "veiculo-1", "cliente-1", "XYZ9K88", "Fiat", "Uno", 2021, "Branco", true);
        when(alterarVeiculoUseCase.executar("veiculo-1", "XYZ9K88", "Fiat", "Uno", 2021, "Branco"))
                .thenReturn(alterado);

        mockMvc.perform(put("/veiculo/veiculo-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alterarRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("XYZ9K88"))
                .andExpect(jsonPath("$.marca").value("Fiat"));

        verify(alterarVeiculoUseCase).executar("veiculo-1", "XYZ9K88", "Fiat", "Uno", 2021, "Branco");
    }

    @Test
    void deveRetornar404AoAlterar_quandoUseCaseLancaIllegalArgument() throws Exception {
        doThrow(new IllegalArgumentException("Veículo não encontrado"))
                .when(alterarVeiculoUseCase).executar(anyString(), anyString(), anyString(), anyString(), anyInt(), anyString());

        mockMvc.perform(put("/veiculo/inexistente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alterarRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveDeletarVeiculoERetornar204() throws Exception {
        mockMvc.perform(delete("/veiculo/veiculo-1"))
                .andExpect(status().isNoContent());

        verify(inativarVeiculoUseCase).executar("veiculo-1");
    }

    @Test
    void deveRetornar404AoDeletar_quandoUseCaseLancaIllegalArgument() throws Exception {
        doThrow(new IllegalArgumentException("Veículo não encontrado"))
                .when(inativarVeiculoUseCase).executar("inexistente");

        mockMvc.perform(delete("/veiculo/inexistente"))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("dadosCamposInvalidos")
    void deveRetornar400_quandoCamposEstaoInvalidosAoCriar(String filename, List<String> errors) throws Exception {
        var request = fileUtils.readResourceFile("/veiculos/%s".formatted(filename));

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/veiculo")
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

    @ParameterizedTest
    @MethodSource("dadosCamposInvalidos")
    void deveRetornar400_quandoCamposEstaoInvalidosAoAlterar(String filename, List<String> errors) throws Exception {
        var request = fileUtils.readResourceFile("/veiculos/%s".formatted(filename));

        var veiculoId = UUID.randomUUID();

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/veiculo/"+veiculoId)
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

    private CadastrarVeiculoRequest cadastrarRequest() {
        CadastrarVeiculoRequest request = new CadastrarVeiculoRequest();
        request.setClienteId("cliente-1");
        request.setPlaca(PLACA_VALIDA);
        request.setMarca("Ford");
        request.setModelo("Ka");
        request.setAno(2020);
        request.setCor("Prata");
        return request;
    }

    private AlterarVeiculoRequest alterarRequest() {
        AlterarVeiculoRequest request = new AlterarVeiculoRequest();
        request.setPlaca("XYZ9K88");
        request.setMarca("Fiat");
        request.setModelo("Uno");
        request.setAno(2021);
        request.setCor("Branco");
        return request;
    }

    private static Stream<Arguments> dadosCamposInvalidos() {
        var erroAnoInvalido = "O ano do veículo deve ser igual ou superior a 1886";

        return Stream.of(
            //Arguments.of("veiculos-400-campos-vazios.json", errosComuns),
            Arguments.of("veiculos-400-ano-invalido.json", List.of(erroAnoInvalido))
        );
    }
}
