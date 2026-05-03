package com.mecanica.oficina_api.interfaces;

import com.mecanica.oficina_api.application.ordemservico.OrdemServicoService;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServicoStatus;
import com.mecanica.oficina_api.interfaces.dto.request.CriarOrdemServicoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.GerarOrcamentoRequest;
import com.mecanica.oficina_api.interfaces.dto.response.OrdemServicoResponse;
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

    private final OrdemServicoService ordemServicoService;

    public OrdemServicoController(OrdemServicoService ordemServicoService) {
        this.ordemServicoService = ordemServicoService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE', 'MECANICO')")
    @Operation(summary = "Listar todas as ordens de serviço",
               description = "Retorna todas as OSs. Use o parâmetro `status` para filtrar por situação.")
    @ApiResponse(responseCode = "200", description = "Lista de OSs")
    public ResponseEntity<List<OrdemServicoResponse>> listar(
            @RequestParam(required = false) OrdemServicoStatus status) {
        return ResponseEntity.ok(ordemServicoService.listar(status));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Criar ordem de serviço", description = "Cria uma nova OS com status RECEBIDA")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "OS criada com sucesso",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Veículo ou cliente não encontrado",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<OrdemServicoResponse> criar(@RequestBody CriarOrdemServicoRequest request) {
        return ResponseEntity.status(201).body(ordemServicoService.criar(request));
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
        return ResponseEntity.ok(ordemServicoService.buscarPorId(id));
    }

    @GetMapping("/veiculo/{veiculoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE', 'MECANICO')")
    @Operation(summary = "Listar OSs por veículo")
    @ApiResponse(responseCode = "200", description = "Lista de OSs do veículo")
    public ResponseEntity<List<OrdemServicoResponse>> listarPorVeiculo(@PathVariable String veiculoId) {
        return ResponseEntity.ok(ordemServicoService.listarPorVeiculo(veiculoId));
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
        return ResponseEntity.ok(ordemServicoService.iniciarDiagnostico(id));
    }

    @PatchMapping("/{id}/finalizar")
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO')")
    @Operation(summary = "Finalizar execução", description = "Transição: EM_EXECUCAO → FINALIZADA. Registra valorFinal, dataFinal e dá baixa nos insumos utilizados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OS finalizada",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponse.class))),
            @ApiResponse(responseCode = "409", description = "OS não está EM_EXECUCAO",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<OrdemServicoResponse> finalizar(@PathVariable String id) {
        return ResponseEntity.ok(ordemServicoService.finalizar(id));
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
        return ResponseEntity.ok(ordemServicoService.entregar(id));
    }

    // --- Orçamento ---

    @PostMapping("/{id}/orcamento")
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO')")
    @Operation(summary = "Gerar orçamento", description = "Gera o orçamento da OS com insumos e/ou serviços do catálogo. Pode incluir itens de ambas as listas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orçamento gerado",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Nenhum item informado",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "OS, insumo ou serviço não encontrado",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "OS em estado inválido para gerar orçamento",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<OrdemServicoResponse> gerarOrcamento(@PathVariable String id,
                                                                @RequestBody GerarOrcamentoRequest request) {
        return ResponseEntity.ok(ordemServicoService.gerarOrcamento(id, request));
    }

    @PutMapping("/{id}/orcamento")
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO')")
    @Operation(summary = "Atualizar orçamento", description = "Substitui o orçamento quando está com status AGUARDANDO (cliente pediu ajuste)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orçamento atualizado",
                    content = @Content(schema = @Schema(implementation = OrdemServicoResponse.class))),
            @ApiResponse(responseCode = "409", description = "Orçamento não está AGUARDANDO",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<OrdemServicoResponse> atualizarOrcamento(@PathVariable String id,
                                                                    @RequestBody GerarOrcamentoRequest request) {
        return ResponseEntity.ok(ordemServicoService.atualizarOrcamento(id, request));
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
        return ResponseEntity.ok(ordemServicoService.enviarOrcamento(id));
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
        return ResponseEntity.ok(ordemServicoService.aguardarOrcamento(id));
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
        return ResponseEntity.ok(ordemServicoService.aprovarOrcamento(id));
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
        return ResponseEntity.ok(ordemServicoService.negarOrcamento(id));
    }
}
