package com.mecanica.oficina_api.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mecanica.oficina_api.adapters.common.FieldError;
import com.mecanica.oficina_api.adapters.common.FileUtils;
import com.mecanica.oficina_api.adapters.common.ValidationErrorResponse;
import com.mecanica.oficina_api.adapters.security.WebhookTokenValidator;
import com.mecanica.oficina_api.adapters.web.GlobalExceptionHandler;
import com.mecanica.oficina_api.adapters.web.OrdemServicoController;
import com.mecanica.oficina_api.adapters.web.dto.request.CriarOrdemServicoRequest;
import com.mecanica.oficina_api.adapters.web.dto.request.GerarOrcamentoRequest;
import com.mecanica.oficina_api.adapters.web.dto.request.ItemOrcamentoRequest;
import com.mecanica.oficina_api.adapters.web.dto.request.ItemServicoRequest;
import com.mecanica.oficina_api.adapters.web.presenter.OrdemServicoPresenter;
import com.mecanica.oficina_api.application.ordemservico.input.GerarOrcamentoInput;
import com.mecanica.oficina_api.application.ordemservico.usecase.AguardarOrcamentoUseCase;
import com.mecanica.oficina_api.application.ordemservico.usecase.AprovarOrcamentoUseCase;
import com.mecanica.oficina_api.application.ordemservico.usecase.AtualizarOrcamentoUseCase;
import com.mecanica.oficina_api.application.ordemservico.usecase.ConsultarOrdemServicoUseCase;
import com.mecanica.oficina_api.application.ordemservico.usecase.CriarOrdemServicoUseCase;
import com.mecanica.oficina_api.application.ordemservico.usecase.EnviarOrcamentoUseCase;
import com.mecanica.oficina_api.application.ordemservico.usecase.EntregarOrdemServicoUseCase;
import com.mecanica.oficina_api.application.ordemservico.usecase.FinalizarOrdemServicoUseCase;
import com.mecanica.oficina_api.application.ordemservico.usecase.FinalizarServicoUseCase;
import com.mecanica.oficina_api.application.ordemservico.usecase.GerarOrcamentoUseCase;
import com.mecanica.oficina_api.application.ordemservico.usecase.IniciarDiagnosticoOrdemServicoUseCase;
import com.mecanica.oficina_api.application.ordemservico.usecase.IniciarExecucaoUseCase;
import com.mecanica.oficina_api.application.ordemservico.usecase.IniciarServicoUseCase;
import com.mecanica.oficina_api.application.ordemservico.usecase.ListarOrdensServicoUseCase;
import com.mecanica.oficina_api.application.ordemservico.usecase.ListarPorVeiculoOrdemServicoUseCase;
import com.mecanica.oficina_api.application.ordemservico.usecase.ListarServicosPorOSUseCase;
import com.mecanica.oficina_api.application.ordemservico.usecase.NegarOrcamentoUseCase;
import com.mecanica.oficina_api.domain.ordemservico.ItemOrcamento;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServicoStatus;
import com.mecanica.oficina_api.domain.ordemservico.StatusServico;

import java.math.BigDecimal;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrdemServicoControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FileUtils fileUtils = new FileUtils();

    @Mock private CriarOrdemServicoUseCase criarOrdemServicoUseCase;
    @Mock private ConsultarOrdemServicoUseCase consultarOrdemServicoUseCase;
    @Mock private ListarOrdensServicoUseCase listarOrdensServicoUseCase;
    @Mock private ListarPorVeiculoOrdemServicoUseCase listarPorVeiculoOrdemServicoUseCase;
    @Mock private IniciarDiagnosticoOrdemServicoUseCase iniciarDiagnosticoOrdemServicoUseCase;
    @Mock private GerarOrcamentoUseCase gerarOrcamentoUseCase;
    @Mock private AtualizarOrcamentoUseCase atualizarOrcamentoUseCase;
    @Mock private EnviarOrcamentoUseCase enviarOrcamentoUseCase;
    @Mock private AguardarOrcamentoUseCase aguardarOrcamentoUseCase;
    @Mock private AprovarOrcamentoUseCase aprovarOrcamentoUseCase;
    @Mock private NegarOrcamentoUseCase negarOrcamentoUseCase;
    @Mock private IniciarExecucaoUseCase iniciarExecucaoUseCase;
    @Mock private FinalizarOrdemServicoUseCase finalizarOrdemServicoUseCase;
    @Mock private EntregarOrdemServicoUseCase entregarOrdemServicoUseCase;
    @Mock private ListarServicosPorOSUseCase listarServicosPorOSUseCase;
    @Mock private IniciarServicoUseCase iniciarServicoUseCase;
    @Mock private FinalizarServicoUseCase finalizarServicoUseCase;

    private static final String OS_ID = "os-1";
    private static final String VEICULO_ID = "veiculo-1";
    private static final String CLIENTE_ID = "cliente-1";
    private static final String WEBHOOK_SECRET = "test-webhook-secret";

    @BeforeEach
    void setUp() {
        OrdemServicoController controller = new OrdemServicoController(
                criarOrdemServicoUseCase, consultarOrdemServicoUseCase, listarOrdensServicoUseCase,
                listarPorVeiculoOrdemServicoUseCase, iniciarDiagnosticoOrdemServicoUseCase,
                gerarOrcamentoUseCase, atualizarOrcamentoUseCase, enviarOrcamentoUseCase,
                aguardarOrcamentoUseCase, aprovarOrcamentoUseCase, negarOrcamentoUseCase,
                iniciarExecucaoUseCase, finalizarOrdemServicoUseCase, entregarOrdemServicoUseCase,
                listarServicosPorOSUseCase, iniciarServicoUseCase, finalizarServicoUseCase,
                new OrdemServicoPresenter(), new WebhookTokenValidator(WEBHOOK_SECRET));
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // --- helpers de construção de domínio ---

    private OrdemServico osRecebida() {
        return OrdemServico.reconstituir(OS_ID, VEICULO_ID, CLIENTE_ID,
                OrdemServicoStatus.RECEBIDA, null, null, null);
    }

    private OrdemServico osComOrcamento() {
        OrdemServico os = OrdemServico.criar(VEICULO_ID, CLIENTE_ID);
        os.iniciarDiagnostico();
        os.gerarOrcamento(List.of(
                new ItemOrcamento("insumo-1", null, "Óleo", 2, new BigDecimal("50.00"))),
                "obs");
        return OrdemServico.reconstituir(OS_ID, VEICULO_ID, CLIENTE_ID,
                OrdemServicoStatus.EM_DIAGNOSTICO, os.getOrcamento(), null, null);
    }

    private StatusServico statusServico() {
        return StatusServico.recriar("status-1",
                com.mecanica.oficina_api.domain.ordemservico.ServicoStatus.AGUARDANDO,
                OS_ID, "servico-1", null, null);
    }

    private CriarOrdemServicoRequest criarRequest() {
        CriarOrdemServicoRequest request = new CriarOrdemServicoRequest();
        request.setVeiculoId(VEICULO_ID);
        request.setClienteId(CLIENTE_ID);
        return request;
    }

    private GerarOrcamentoRequest orcamentoRequest() {
        ItemOrcamentoRequest insumo = new ItemOrcamentoRequest();
        insumo.setInsumoId("insumo-1");
        insumo.setQuantidade(2);
        ItemServicoRequest servico = new ItemServicoRequest();
        servico.setServicoId("servico-1");
        servico.setQuantidade(1);
        GerarOrcamentoRequest request = new GerarOrcamentoRequest();
        request.setInsumos(List.of(insumo));
        request.setServicos(List.of(servico));
        request.setObservacoes("obs");
        return request;
    }

    // --- listar ---

    @Test
    void deveListarOrdensERetornar200() throws Exception {
        when(listarOrdensServicoUseCase.executar(null)).thenReturn(List.of(osRecebida()));

        mockMvc.perform(get("/ordem-servico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(OS_ID))
                .andExpect(jsonPath("$[0].status").value("RECEBIDA"));
    }

    @Test
    void deveListarOrdensFiltradasPorStatusERetornar200() throws Exception {
        when(listarOrdensServicoUseCase.executar(OrdemServicoStatus.RECEBIDA))
                .thenReturn(List.of(osRecebida()));

        mockMvc.perform(get("/ordem-servico").param("status", "RECEBIDA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("RECEBIDA"));
    }

    // --- criar ---

    @Test
    void deveCriarOrdemERetornar201() throws Exception {
        when(criarOrdemServicoUseCase.executar(eq(VEICULO_ID), eq(CLIENTE_ID), any(GerarOrcamentoInput.class))).thenReturn(osRecebida());

        mockMvc.perform(post("/ordem-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(OS_ID))
                .andExpect(jsonPath("$.veiculoId").value(VEICULO_ID))
                .andExpect(jsonPath("$.clienteId").value(CLIENTE_ID))
                .andExpect(jsonPath("$.status").value("RECEBIDA"));
    }

    @Test
    void deveRetornar404AoCriar_quandoVeiculoNaoEncontrado() throws Exception {
        when(criarOrdemServicoUseCase.executar(eq(VEICULO_ID), eq(CLIENTE_ID), any(GerarOrcamentoInput.class)))
                .thenThrow(new IllegalArgumentException("Veículo não encontrado"));

        mockMvc.perform(post("/ordem-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarRequest())))
                .andExpect(status().isNotFound());
    }

    // --- buscar por id ---

    @Test
    void deveBuscarOrdemPorIdERetornar200() throws Exception {
        when(consultarOrdemServicoUseCase.executar(OS_ID)).thenReturn(osRecebida());

        mockMvc.perform(get("/ordem-servico/" + OS_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(OS_ID))
                .andExpect(jsonPath("$.status").value("RECEBIDA"));
    }

    @Test
    void deveRetornar404AoBuscar_quandoOrdemNaoEncontrada() throws Exception {
        when(consultarOrdemServicoUseCase.executar("inexistente"))
                .thenThrow(new IllegalArgumentException("OS não encontrada"));

        mockMvc.perform(get("/ordem-servico/inexistente"))
                .andExpect(status().isNotFound());
    }

    // --- listar por veiculo ---

    @Test
    void deveListarPorVeiculoERetornar200() throws Exception {
        when(listarPorVeiculoOrdemServicoUseCase.executar(VEICULO_ID))
                .thenReturn(List.of(osRecebida()));

        mockMvc.perform(get("/ordem-servico/veiculo/" + VEICULO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(OS_ID));
    }

    // --- transições de status ---

    @Test
    void deveIniciarDiagnosticoERetornar200() throws Exception {
        when(iniciarDiagnosticoOrdemServicoUseCase.executar(OS_ID))
                .thenReturn(OrdemServico.reconstituir(OS_ID, VEICULO_ID, CLIENTE_ID,
                        OrdemServicoStatus.EM_DIAGNOSTICO, null, null, null));

        mockMvc.perform(patch("/ordem-servico/" + OS_ID + "/iniciar-diagnostico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_DIAGNOSTICO"));
    }

    @Test
    void deveRetornar409AoIniciarDiagnostico_quandoEstadoInvalido() throws Exception {
        when(iniciarDiagnosticoOrdemServicoUseCase.executar(OS_ID))
                .thenThrow(new IllegalStateException("OS deve estar RECEBIDA"));

        mockMvc.perform(patch("/ordem-servico/" + OS_ID + "/iniciar-diagnostico"))
                .andExpect(status().isConflict());
    }

    @Test
    void deveFinalizarERetornar200() throws Exception {
        when(finalizarOrdemServicoUseCase.executar(OS_ID))
                .thenReturn(OrdemServico.reconstituir(OS_ID, VEICULO_ID, CLIENTE_ID,
                        OrdemServicoStatus.FINALIZADA, null, new BigDecimal("100.00"), null));

        mockMvc.perform(patch("/ordem-servico/" + OS_ID + "/finalizar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FINALIZADA"))
                .andExpect(jsonPath("$.valorFinal").value(100.00));
    }

    @Test
    void deveEntregarERetornar200() throws Exception {
        when(entregarOrdemServicoUseCase.executar(OS_ID))
                .thenReturn(OrdemServico.reconstituir(OS_ID, VEICULO_ID, CLIENTE_ID,
                        OrdemServicoStatus.ENTREGUE, null, null, null));

        mockMvc.perform(patch("/ordem-servico/" + OS_ID + "/entregar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENTREGUE"));
    }

    // --- orçamento ---

    @Test
    void deveGerarOrcamentoERetornar200() throws Exception {
        when(gerarOrcamentoUseCase.executar(eq(OS_ID), any(GerarOrcamentoInput.class)))
                .thenReturn(osComOrcamento());

        mockMvc.perform(post("/ordem-servico/" + OS_ID + "/orcamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orcamentoRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orcamento.status").value("PENDENTE"))
                .andExpect(jsonPath("$.orcamento.itens.length()").value(1))
                .andExpect(jsonPath("$.orcamento.valorTotal").value(100.00));
    }

    @Test
    void deveRetornar404AoGerarOrcamento_quandoInsumoNaoEncontrado() throws Exception {
        when(gerarOrcamentoUseCase.executar(eq(OS_ID), any(GerarOrcamentoInput.class)))
                .thenThrow(new IllegalArgumentException("Insumo não encontrado"));

        mockMvc.perform(post("/ordem-servico/" + OS_ID + "/orcamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orcamentoRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAtualizarOrcamentoERetornar200() throws Exception {
        when(atualizarOrcamentoUseCase.executar(eq(OS_ID), any(GerarOrcamentoInput.class)))
                .thenReturn(osComOrcamento());

        mockMvc.perform(put("/ordem-servico/" + OS_ID + "/orcamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orcamentoRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orcamento.itens.length()").value(1));
    }

    @Test
    void deveRetornar409AoAtualizarOrcamento_quandoEstadoInvalido() throws Exception {
        when(atualizarOrcamentoUseCase.executar(eq(OS_ID), any(GerarOrcamentoInput.class)))
                .thenThrow(new IllegalStateException("Orçamento deve estar AGUARDANDO"));

        mockMvc.perform(put("/ordem-servico/" + OS_ID + "/orcamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orcamentoRequest())))
                .andExpect(status().isConflict());
    }

    @Test
    void deveEnviarOrcamentoERetornar200() throws Exception {
        when(enviarOrcamentoUseCase.executar(OS_ID)).thenReturn(osComOrcamento());

        mockMvc.perform(patch("/ordem-servico/" + OS_ID + "/orcamento/enviar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(OS_ID));
    }

    @Test
    void deveAguardarOrcamentoERetornar200() throws Exception {
        when(aguardarOrcamentoUseCase.executar(OS_ID)).thenReturn(osComOrcamento());

        mockMvc.perform(patch("/ordem-servico/" + OS_ID + "/orcamento/aguardar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(OS_ID));
    }

    @Test
    void deveAprovarOrcamentoERetornar200() throws Exception {
        when(aprovarOrcamentoUseCase.executar(OS_ID)).thenReturn(osComOrcamento());

        mockMvc.perform(patch("/ordem-servico/" + OS_ID + "/orcamento/aprovar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(OS_ID));
    }

    @Test
    void deveRetornar409AoAprovarOrcamento_quandoEstadoInvalido() throws Exception {
        when(aprovarOrcamentoUseCase.executar(OS_ID))
                .thenThrow(new IllegalStateException("Orçamento em estado inválido"));

        mockMvc.perform(patch("/ordem-servico/" + OS_ID + "/orcamento/aprovar"))
                .andExpect(status().isConflict());
    }

    @Test
    void deveNegarOrcamentoERetornar200() throws Exception {
        when(negarOrcamentoUseCase.executar(OS_ID)).thenReturn(osComOrcamento());

        mockMvc.perform(patch("/ordem-servico/" + OS_ID + "/orcamento/negar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(OS_ID));
    }

    @Test
    void deveIniciarExecucaoERetornar200() throws Exception {
        when(iniciarExecucaoUseCase.executar(OS_ID))
                .thenReturn(OrdemServico.reconstituir(OS_ID, VEICULO_ID, CLIENTE_ID,
                        OrdemServicoStatus.EM_EXECUCAO, null, null, null));

        mockMvc.perform(patch("/ordem-servico/" + OS_ID + "/iniciar-execucao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_EXECUCAO"));
    }

    // --- serviços ---

    @Test
    void deveListarServicosERetornar200() throws Exception {
        when(listarServicosPorOSUseCase.executar(OS_ID)).thenReturn(List.of(statusServico()));

        mockMvc.perform(get("/ordem-servico/" + OS_ID + "/servico/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("status-1"))
                .andExpect(jsonPath("$[0].status").value("AGUARDANDO"))
                .andExpect(jsonPath("$[0].servicoId").value("servico-1"));
    }

    @Test
    void deveIniciarServicoERetornar200() throws Exception {
        StatusServico iniciado = StatusServico.recriar("status-1",
                com.mecanica.oficina_api.domain.ordemservico.ServicoStatus.INICIADO,
                OS_ID, "servico-1", null, null);
        when(iniciarServicoUseCase.executar("status-1")).thenReturn(iniciado);

        mockMvc.perform(patch("/ordem-servico/servico/status-1/iniciar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INICIADO"));
    }

    @Test
    void deveRetornar404AoIniciarServico_quandoNaoEncontrado() throws Exception {
        when(iniciarServicoUseCase.executar("inexistente"))
                .thenThrow(new IllegalArgumentException("Serviço não encontrado"));

        mockMvc.perform(patch("/ordem-servico/servico/inexistente/iniciar"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveFinalizarServicoERetornar200() throws Exception {
        StatusServico finalizado = StatusServico.recriar("status-1",
                com.mecanica.oficina_api.domain.ordemservico.ServicoStatus.FINALIZADO,
                OS_ID, "servico-1", null, null);
        when(finalizarServicoUseCase.executar("status-1")).thenReturn(finalizado);

        mockMvc.perform(patch("/ordem-servico/servico/status-1/finalizar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FINALIZADO"));
    }

    @ParameterizedTest
    @MethodSource("dadosCamposInvalidosOrdemServico")
    void deveRetornar400_quandoCamposEstaoInvalidosAoCadastrarOrdemServico(String filename, List<String> errors) throws Exception {
        var request = fileUtils.readResourceFile("/ordem_servicos/%s".formatted(filename));

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/ordem-servico")
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
    @MethodSource("dadosCamposInvalidosOrcamento")
    void deveRetornar400_quandoCamposEstaoInvalidosAoCadastrarOrcamento(String filename, List<String> errors) throws Exception {
        var request = fileUtils.readResourceFile("/ordem_servicos/%s".formatted(filename));

        var ordemServicoId = UUID.randomUUID();

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/ordem-servico/"+ordemServicoId+"/orcamento")
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
    @MethodSource("dadosCamposInvalidosOrcamentoAlterar")
    void deveRetornar400_quandoCamposEstaoInvalidosAoAlterarOrcamento(String filename, List<String> errors) throws Exception {
        var request = fileUtils.readResourceFile("/ordem_servicos/%s".formatted(filename));

        var ordemServicoId = UUID.randomUUID();

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/ordem-servico/"+ordemServicoId+"/orcamento")
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

    private static Stream<Arguments> dadosCamposInvalidosOrdemServico() {
        var erroVeiculoId = "O ID do veículo é obrigatório";
        var erroClienteId = "O ID do cliente é obrigatório";

        var errosComuns = List.of(erroClienteId, erroVeiculoId);

        return Stream.of(
            Arguments.of("cadastrar-ordem-servico-400-campos-vazios.json", errosComuns),
            Arguments.of("cadastrar-ordem-servico-400-campos-nulos.json", errosComuns)
        );
    }

    private static Stream<Arguments> dadosCamposInvalidosOrcamento() {
        var erroInsumoId = "O ID do insumo é obrigatório";
        var erroInsumoQuantidade = "A quantidade de insumo deve ser maior que zero";
        var erroServicoId = "O ID do serviço é obrigatório";
        var erroServicoQuantidade = "A quantidade de serviço deve ser maior que zero";

        return Stream.of(
            Arguments.of("cadastrar-orcamento-400-campos-nulos.json", List.of(erroInsumoId, erroServicoId)),
            Arguments.of("cadastrar-orcamento-400-quantidade-negativa.json", List.of(erroInsumoQuantidade, erroServicoQuantidade))
        );
    }

    private static Stream<Arguments> dadosCamposInvalidosOrcamentoAlterar() {
        var erroInsumoId = "O ID do insumo é obrigatório";
        var erroInsumoQuantidade = "A quantidade de insumo deve ser maior que zero";
        var erroServicoId = "O ID do serviço é obrigatório";
        var erroServicoQuantidade = "A quantidade de serviço deve ser maior que zero";

        return Stream.of(
            Arguments.of("alterar-orcamento-400-campos-nulos.json", List.of(erroInsumoId, erroServicoId)),
            Arguments.of("alterar-orcamento-400-quantidade-negativa.json", List.of(erroInsumoQuantidade, erroServicoQuantidade))
        );
    }
}
