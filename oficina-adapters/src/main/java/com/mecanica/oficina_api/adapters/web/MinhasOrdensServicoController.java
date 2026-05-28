package com.mecanica.oficina_api.adapters.web;

import com.mecanica.oficina_api.adapters.security.UsuarioPrincipal;
import com.mecanica.oficina_api.adapters.web.dto.response.MinhaOrdemServicoResponse;
import com.mecanica.oficina_api.application.ordemservico.OrdemServicoService;
import com.mecanica.oficina_api.application.ordemservico.output.MinhaOrdemServicoOutput;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServicoStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("cliente/minhas-os")
@Tag(name = "Área do Cliente", description = "Consulta de ordens de serviço pelo próprio cliente")
@SecurityRequirement(name = "bearerAuth")
public class MinhasOrdensServicoController {

    private final OrdemServicoService ordemServicoService;

    public MinhasOrdensServicoController(OrdemServicoService ordemServicoService) {
        this.ordemServicoService = ordemServicoService;
    }

    @GetMapping
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Listar minhas ordens de serviço",
               description = "Retorna as OSs do cliente logado. Filtros opcionais: status da OS e placa do veículo.")
    @ApiResponse(responseCode = "200", description = "Lista de OSs do cliente")
    public ResponseEntity<List<MinhaOrdemServicoResponse>> listar(
            @AuthenticationPrincipal UsuarioPrincipal principal,
            @RequestParam(required = false) OrdemServicoStatus status,
            @RequestParam(required = false) String placa) {
        return ResponseEntity.ok(
                ordemServicoService.listarMinhasOs(principal.clienteId(), status, placa).stream()
                        .map(this::toResponse)
                        .toList());
    }

    private MinhaOrdemServicoResponse toResponse(MinhaOrdemServicoOutput o) {
        MinhaOrdemServicoResponse.VeiculoResumo veiculo = o.veiculo() == null ? null
                : new MinhaOrdemServicoResponse.VeiculoResumo(
                        o.veiculo().id(), o.veiculo().placa(), o.veiculo().marca(),
                        o.veiculo().modelo(), o.veiculo().ano(), o.veiculo().cor());
        return new MinhaOrdemServicoResponse(o.id(), o.status(), o.orcamentoStatus(), veiculo);
    }
}
