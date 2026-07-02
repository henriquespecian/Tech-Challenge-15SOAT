package com.mecanica.oficina_api.adapters.web;

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
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServicoStatus;
import com.mecanica.oficina_api.adapters.web.dto.request.CriarOrdemServicoRequest;
import com.mecanica.oficina_api.adapters.web.dto.request.GerarOrcamentoRequest;
import com.mecanica.oficina_api.adapters.web.dto.response.OrdemServicoResponse;
import com.mecanica.oficina_api.adapters.web.dto.response.ValidationErrorResponse;
import jakarta.validation.Valid;
import com.mecanica.oficina_api.adapters.web.dto.response.ServicoStatusResponse;
import com.mecanica.oficina_api.adapters.web.presenter.OrdemServicoPresenter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("ordem-servico")
@Tag(name = "Ordem de Serviço", description = "Gerenciamento de ordens de serviço. Fluxo: RECEBIDA → EM_DIAGNOSTICO → (orçamento gerado) → AGUARDANDO_APROVACAO → EM_EXECUCAO → FINALIZADA → ENTREGUE")
@SecurityRequirement(name = "bearerAuth")
public class OrdemServicoController {

    private final CriarOrdemServicoUseCase criarOrdemServicoUseCase;
    private final ConsultarOrdemServicoUseCase consultarOrdemServicoUseCase;
    private final ListarOrdensServicoUseCase listarOrdensServicoUseCase;
    private final ListarPorVeiculoOrdemServicoUseCase listarPorVeiculoOrdemServicoUseCase;
    private final IniciarDiagnosticoOrdemServicoUseCase iniciarDiagnosticoOrdemServicoUseCase;
    private final GerarOrcamentoUseCase gerarOrcamentoUseCase;
    private final AtualizarOrcamentoUseCase atualizarOrcamentoUseCase;
    private final EnviarOrcamentoUseCase enviarOrcamentoUseCase;
    private final AguardarOrcamentoUseCase aguardarOrcamentoUseCase;
    private final AprovarOrcamentoUseCase aprovarOrcamentoUseCase;
    private final NegarOrcamentoUseCase negarOrcamentoUseCase;
    private final IniciarExecucaoUseCase iniciarExecucaoUseCase;
    private final FinalizarOrdemServicoUseCase finalizarOrdemServicoUseCase;
    private final EntregarOrdemServicoUseCase entregarOrdemServicoUseCase;
    private final ListarServicosPorOSUseCase listarServicosPorOSUseCase;
    private final IniciarServicoUseCase iniciarServicoUseCase;
    private final FinalizarServicoUseCase finalizarServicoUseCase;
    private final OrdemServicoPresenter presenter;

    public OrdemServicoController(CriarOrdemServicoUseCase criarOrdemServicoUseCase,
            ConsultarOrdemServicoUseCase consultarOrdemServicoUseCase,
            ListarOrdensServicoUseCase listarOrdensServicoUseCase,
            ListarPorVeiculoOrdemServicoUseCase listarPorVeiculoOrdemServicoUseCase,
            IniciarDiagnosticoOrdemServicoUseCase iniciarDiagnosticoOrdemServicoUseCase,
            GerarOrcamentoUseCase gerarOrcamentoUseCase,
            AtualizarOrcamentoUseCase atualizarOrcamentoUseCase,
            EnviarOrcamentoUseCase enviarOrcamentoUseCase,
            AguardarOrcamentoUseCase aguardarOrcamentoUseCase,
            AprovarOrcamentoUseCase aprovarOrcamentoUseCase,
            NegarOrcamentoUseCase negarOrcamentoUseCase,
            IniciarExecucaoUseCase iniciarExecucaoUseCase,
            FinalizarOrdemServicoUseCase finalizarOrdemServicoUseCase,
            EntregarOrdemServicoUseCase entregarOrdemServicoUseCase,
            ListarServicosPorOSUseCase listarServicosPorOSUseCase,
            IniciarServicoUseCase iniciarServicoUseCase,
            FinalizarServicoUseCase finalizarServicoUseCase,
            OrdemServicoPresenter presenter) {
        this.criarOrdemServicoUseCase = criarOrdemServicoUseCase;
        this.consultarOrdemServicoUseCase = consultarOrdemServicoUseCase;
        this.listarOrdensServicoUseCase = listarOrdensServicoUseCase;
        this.listarPorVeiculoOrdemServicoUseCase = listarPorVeiculoOrdemServicoUseCase;
        this.iniciarDiagnosticoOrdemServicoUseCase = iniciarDiagnosticoOrdemServicoUseCase;
        this.gerarOrcamentoUseCase = gerarOrcamentoUseCase;
        this.atualizarOrcamentoUseCase = atualizarOrcamentoUseCase;
        this.enviarOrcamentoUseCase = enviarOrcamentoUseCase;
        this.aguardarOrcamentoUseCase = aguardarOrcamentoUseCase;
        this.aprovarOrcamentoUseCase = aprovarOrcamentoUseCase;
        this.negarOrcamentoUseCase = negarOrcamentoUseCase;
        this.iniciarExecucaoUseCase = iniciarExecucaoUseCase;
        this.finalizarOrdemServicoUseCase = finalizarOrdemServicoUseCase;
        this.entregarOrdemServicoUseCase = entregarOrdemServicoUseCase;
        this.listarServicosPorOSUseCase = listarServicosPorOSUseCase;
        this.iniciarServicoUseCase = iniciarServicoUseCase;
        this.finalizarServicoUseCase = finalizarServicoUseCase;
        this.presenter = presenter;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE', 'MECANICO')")
    @Operation(summary = "Listar todas as ordens de serviço",
        description = "Retorna todas as OSs. Use o parâmetro `status` para filtrar por situação.")
    @ApiResponse(responseCode = "200", description = "Lista de OSs")
    public ResponseEntity<List<OrdemServicoResponse>> listar(
        @RequestParam(required = false) OrdemServicoStatus status) {
        return ResponseEntity.ok(presenter.apresentar(listarOrdensServicoUseCase.executar(status)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Criar ordem de serviço", description = "Cria uma nova OS com status RECEBIDA")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "OS criada com sucesso",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos de entrada",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Veículo ou cliente não encontrado",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<OrdemServicoResponse> criar(@Valid @RequestBody CriarOrdemServicoRequest request) {
        OrdemServico os = criarOrdemServicoUseCase.executar(request.getVeiculoId(), request.getClienteId());
        return ResponseEntity.status(201).body(presenter.apresentar(os));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE', 'MECANICO')")
    @Operation(summary = "Buscar OS por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OS encontrada",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponse.class))),
            @ApiResponse(responseCode = "404", description = "OS não encontrada",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<OrdemServicoResponse> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(presenter.apresentar(consultarOrdemServicoUseCase.executar(id)));
    }

    @GetMapping("/veiculo/{veiculoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE', 'MECANICO')")
    @Operation(summary = "Listar OSs por veículo")
    @ApiResponse(responseCode = "200", description = "Lista de OSs do veículo")
    public ResponseEntity<List<OrdemServicoResponse>> listarPorVeiculo(@PathVariable String veiculoId) {
        return ResponseEntity.ok(presenter.apresentar(listarPorVeiculoOrdemServicoUseCase.executar(veiculoId)));
    }

    // --- Transições de status da OS ---

    @PatchMapping("/{id}/iniciar-diagnostico")
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO')")
    @Operation(summary = "Iniciar diagnóstico", description = "Transição: RECEBIDA → EM_DIAGNOSTICO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diagnóstico iniciado",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponse.class))),
            @ApiResponse(responseCode = "409", description = "OS não está RECEBIDA",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<OrdemServicoResponse> iniciarDiagnostico(@PathVariable String id) {
        return ResponseEntity.ok(presenter.apresentar(iniciarDiagnosticoOrdemServicoUseCase.executar(id)));
    }

    @PatchMapping("/{id}/finalizar")
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO')")
    @Operation(summary = "Finalizar execução", description = "Transição: EM_EXECUCAO → FINALIZADA. Registra valorFinal, dataFinal e dá baixa nos insumos utilizados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OS finalizada",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Serviços atrelados à OS não estão FINALIZADOS",
                content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "OS não está EM_EXECUCAO",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<OrdemServicoResponse> finalizar(@PathVariable String id) {
        return ResponseEntity.ok(presenter.apresentar(finalizarOrdemServicoUseCase.executar(id)));
    }

    @PatchMapping("/{id}/entregar")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Registrar entrega do veículo", description = "Transição: FINALIZADA → ENTREGUE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Veículo entregue ao cliente",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponse.class))),
            @ApiResponse(responseCode = "409", description = "OS não está FINALIZADA",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<OrdemServicoResponse> entregar(@PathVariable String id) {
        return ResponseEntity.ok(presenter.apresentar(entregarOrdemServicoUseCase.executar(id)));
    }

    // --- Orçamento ---

    @PostMapping("/{id}/orcamento")
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO')")
    @Operation(summary = "Gerar orçamento", description = "Gera o orçamento da OS com insumos e/ou serviços do catálogo. Pode incluir itens de ambas as listas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orçamento gerado",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou nenhum item informado",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "OS, insumo ou serviço não encontrado",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "OS em estado inválido para gerar orçamento",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<OrdemServicoResponse> gerarOrcamento(@PathVariable String id,
                                                                @Valid @RequestBody GerarOrcamentoRequest request) {
        return ResponseEntity.ok(presenter.apresentar(gerarOrcamentoUseCase.executar(id, toInput(request))));
    }

    @PutMapping("/{id}/orcamento")
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO')")
    @Operation(summary = "Atualizar orçamento", description = "Substitui o orçamento quando está com status AGUARDANDO (cliente pediu ajuste)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orçamento atualizado",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos de entrada",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Orçamento não está AGUARDANDO",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<OrdemServicoResponse> atualizarOrcamento(@PathVariable String id,
                                                                    @Valid @RequestBody GerarOrcamentoRequest request) {
        return ResponseEntity.ok(presenter.apresentar(atualizarOrcamentoUseCase.executar(id, toInput(request))));
    }

    @PatchMapping("/{id}/orcamento/enviar")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Enviar orçamento ao cliente", description = "Transição do orçamento: PENDENTE → ENVIADO. OS passa a AGUARDANDO_APROVACAO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orçamento enviado",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponse.class))),
            @ApiResponse(responseCode = "409", description = "Orçamento não está PENDENTE",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<OrdemServicoResponse> enviarOrcamento(@PathVariable String id) {
        return ResponseEntity.ok(presenter.apresentar(enviarOrcamentoUseCase.executar(id)));
    }

    @PatchMapping("/{id}/orcamento/aguardar")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Aguardar resposta do cliente", description = "Transição do orçamento: ENVIADO → AGUARDANDO (cliente solicitou ajustes ou prazo extra)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orçamento em aguardo",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponse.class))),
            @ApiResponse(responseCode = "409", description = "Orçamento não está ENVIADO",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<OrdemServicoResponse> aguardarOrcamento(@PathVariable String id) {
        return ResponseEntity.ok(presenter.apresentar(aguardarOrcamentoUseCase.executar(id)));
    }

    @PatchMapping("/{id}/orcamento/aprovar")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Aprovar orçamento", description = "Transição do orçamento: ENVIADO|AGUARDANDO → APROVADO. OS passa a EM_EXECUCAO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orçamento aprovado, OS em execução",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponse.class))),
            @ApiResponse(responseCode = "409", description = "Orçamento em estado inválido para aprovação",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<OrdemServicoResponse> aprovarOrcamento(@PathVariable String id) {
        return ResponseEntity.ok(presenter.apresentar(aprovarOrcamentoUseCase.executar(id)));
    }

    @PatchMapping("/{id}/iniciar-execucao")
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO')")
    @Operation(summary = "Iniciar execução", description = "Transição: AGUARDANDO_APROVACAO → EM_EXECUCAO. Requer orçamento aprovado. Cria os serviços com status AGUARDANDO")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Execução iniciada",
            content = @Content(schema = @Schema(implementation = OrdemServicoResponse.class))),
        @ApiResponse(responseCode = "409", description = "OS não está aguardando aprovação ou orçamento não aprovado",
            content = @Content(schema = @Schema()))
    })
    public ResponseEntity<OrdemServicoResponse> iniciarExecucao(@PathVariable String id) {
        return ResponseEntity.ok(presenter.apresentar(iniciarExecucaoUseCase.executar(id)));
    }

    @PatchMapping("/{id}/orcamento/negar")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Negar orçamento", description = "Transição do orçamento: ENVIADO|AGUARDANDO → NEGADO. OS retorna a EM_DIAGNOSTICO para novo orçamento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orçamento negado",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponse.class))),
            @ApiResponse(responseCode = "409", description = "Orçamento em estado inválido para negar",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<OrdemServicoResponse> negarOrcamento(@PathVariable String id) {
        return ResponseEntity.ok(presenter.apresentar(negarOrcamentoUseCase.executar(id)));
    }

    // --- Serviços ---
    @GetMapping("/{id}/servico/listar")
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO')")
    @Operation(summary = "Listar todos os serviços atrelados à OS", description = "Mostra todos os serviços atrelados à uma OS e seus status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado com sucesso", content = @Content(schema = @Schema()))
    })
    public ResponseEntity<List<ServicoStatusResponse>> listarServicos(@PathVariable String id) {
        return ResponseEntity.ok(presenter.apresentarServicos(listarServicosPorOSUseCase.executar(id)));
    }


    @PatchMapping("/servico/{servico_id}/iniciar")
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO')")
    @Operation(summary = "Iniciar um serviço por ID", description = "Altera o status do serviço específicado para: INICIADO")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Serviço iniciado com sucesso", content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "404", description = "Serviço não encontrado", content = @Content(schema = @Schema()))
    })
    public ResponseEntity<ServicoStatusResponse> iniciarServico(@PathVariable String servico_id) {
        return ResponseEntity.ok(presenter.apresentarServico(iniciarServicoUseCase.executar(servico_id)));
    }

    @PatchMapping("/servico/{servico_id}/finalizar")
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO')")
    @Operation(summary = "Finalizar um serviço por ID", description = "Altera o status do serviço específicado para: FINALIZADO")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Serviço finalizado com sucesso", content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "404", description = "Serviço não encontrado", content = @Content(schema = @Schema()))
    })
    public ResponseEntity<ServicoStatusResponse> finalizarServico(@PathVariable String servico_id) {
        return ResponseEntity.ok(presenter.apresentarServico(finalizarServicoUseCase.executar(servico_id)));
    }

    // --- mapeamento request → input ---

    private GerarOrcamentoInput toInput(GerarOrcamentoRequest request) {
        List<GerarOrcamentoInput.ItemInsumoInput> insumos = request.getInsumos() == null ? List.of()
                : request.getInsumos().stream()
                        .map(i -> new GerarOrcamentoInput.ItemInsumoInput(i.getInsumoId(), i.getQuantidade()))
                        .toList();
        List<GerarOrcamentoInput.ItemServicoInput> servicos = request.getServicos() == null ? List.of()
                : request.getServicos().stream()
                        .map(s -> new GerarOrcamentoInput.ItemServicoInput(s.getServicoId(), s.getQuantidade()))
                        .toList();
        return new GerarOrcamentoInput(insumos, servicos, request.getObservacoes());
    }

}
