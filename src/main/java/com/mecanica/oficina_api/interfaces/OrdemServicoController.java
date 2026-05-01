package com.mecanica.oficina_api.interfaces;

import com.mecanica.oficina_api.application.ordemservico.OrdemServicoService;
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
@Tag(name = "Ordem de Serviço", description = "Gerenciamento de ordens de serviço da oficina")
@SecurityRequirement(name = "bearerAuth")
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;

    public OrdemServicoController(OrdemServicoService ordemServicoService) {
        this.ordemServicoService = ordemServicoService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Criar ordem de serviço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ordem de serviço criada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Veículo ou cliente não encontrado",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<Void> criar(@RequestBody CriarOrdemServicoRequest request) {
        ordemServicoService.criar(request);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE', 'MECANICO')")
    @Operation(summary = "Buscar ordem de serviço por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ordem de serviço encontrada"),
            @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<OrdemServicoResponse> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(ordemServicoService.buscarPorId(id));
    }

    @GetMapping("/veiculo/{veiculoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE', 'MECANICO')")
    @Operation(summary = "Listar ordens de serviço por veículo")
    public ResponseEntity<List<OrdemServicoResponse>> listarPorVeiculo(@PathVariable String veiculoId) {
        return ResponseEntity.ok(ordemServicoService.listarPorVeiculo(veiculoId));
    }

    @PostMapping("/{id}/orcamento")
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO')")
    @Operation(summary = "Gerar orçamento para a ordem de serviço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orçamento gerado"),
            @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "Estado inválido para gerar orçamento",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<OrdemServicoResponse> gerarOrcamento(@PathVariable String id,
                                                                @RequestBody GerarOrcamentoRequest request) {
        return ResponseEntity.ok(ordemServicoService.gerarOrcamento(id, request));
    }

    @PutMapping("/{id}/orcamento")
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO')")
    @Operation(summary = "Atualizar orçamento (apenas quando EM_NEGOCIACAO)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orçamento atualizado"),
            @ApiResponse(responseCode = "409", description = "Estado inválido para atualizar orçamento",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<OrdemServicoResponse> atualizarOrcamento(@PathVariable String id,
                                                                    @RequestBody GerarOrcamentoRequest request) {
        return ResponseEntity.ok(ordemServicoService.atualizarOrcamento(id, request));
    }

    @PatchMapping("/{id}/orcamento/enviar")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Enviar orçamento para aprovação do cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orçamento enviado"),
            @ApiResponse(responseCode = "409", description = "Estado inválido",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<OrdemServicoResponse> enviarOrcamento(@PathVariable String id) {
        return ResponseEntity.ok(ordemServicoService.enviarOrcamento(id));
    }

    @PatchMapping("/{id}/orcamento/negociar")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Colocar orçamento em negociação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orçamento em negociação"),
            @ApiResponse(responseCode = "409", description = "Estado inválido",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<OrdemServicoResponse> negociarOrcamento(@PathVariable String id) {
        return ResponseEntity.ok(ordemServicoService.negociarOrcamento(id));
    }

    @PatchMapping("/{id}/orcamento/aprovar")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Aprovar orçamento e finalizar ordem de serviço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orçamento aprovado, OS finalizada"),
            @ApiResponse(responseCode = "409", description = "Estado inválido",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<OrdemServicoResponse> aprovarOrcamento(@PathVariable String id) {
        return ResponseEntity.ok(ordemServicoService.aprovarOrcamento(id));
    }

    @PatchMapping("/{id}/orcamento/negar")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Negar orçamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orçamento negado"),
            @ApiResponse(responseCode = "409", description = "Estado inválido",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<OrdemServicoResponse> negarOrcamento(@PathVariable String id) {
        return ResponseEntity.ok(ordemServicoService.negarOrcamento(id));
    }

    @PatchMapping("/{id}/veiculo/retirar")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Registrar retirada do veículo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Veículo retirado"),
            @ApiResponse(responseCode = "409", description = "OS não está finalizada",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<OrdemServicoResponse> retirarVeiculo(@PathVariable String id) {
        return ResponseEntity.ok(ordemServicoService.retirarVeiculo(id));
    }
}
