package com.mecanica.oficina_api.interfaces;

import com.mecanica.oficina_api.application.insumo.InsumosService;
import com.mecanica.oficina_api.interfaces.dto.request.AlterarInsumosRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarInsumosRequest;
import com.mecanica.oficina_api.interfaces.dto.response.InsumosResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("insumos")
@Tag(name = "Insumos", description = "Gerenciamento de insumos e peças do estoque da oficina")
@SecurityRequirement(name = "bearerAuth")
public class InsumosController {

    private final InsumosService insumosService;

    public InsumosController(InsumosService insumosService) {
        this.insumosService = insumosService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE', 'MECANICO')")
    @Operation(summary = "Listar insumos ativos", description = "Retorna todos os insumos e peças com ativo=true")
    @ApiResponse(responseCode = "200", description = "Lista de insumos ativos")
    public ResponseEntity<List<InsumosResponse>> listarInsumos() {
        return ResponseEntity.ok(insumosService.listar());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Cadastrar insumo", description = "Cadastra um novo insumo ou peça no estoque")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Insumo cadastrado com sucesso"),
        @ApiResponse(responseCode = "409", description = "Insumo com o mesmo nome já existe",
            content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "400", description = "Dados da solicitação inválidos",
            content = @Content(schema = @Schema()))
    })
    public ResponseEntity<Void> cadastrarInsumo(@RequestBody CadastrarInsumosRequest request) {
        insumosService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Alterar insumo", description = "Atualiza os dados de um insumo ativo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Insumo alterado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Insumo não encontrado",
            content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "400", description = "Dados da solicitação inválidos",
            content = @Content(schema = @Schema()))
    })
    public ResponseEntity<Void> alterarInsumo(@PathVariable String id, @RequestBody AlterarInsumosRequest request) {
        insumosService.atualizar(id, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reativar insumo", description = "Reativa um insumo previamente desativado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Insumo reativado",
            content = @Content(schema = @Schema(implementation = InsumosResponse.class))),
        @ApiResponse(responseCode = "404", description = "Insumo não encontrado",
            content = @Content(schema = @Schema()))
    })
    public ResponseEntity<InsumosResponse> ativarInsumo(@PathVariable String id) {
        return ResponseEntity.ok(insumosService.ativar(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desativar insumo", description = "Inativa um insumo do estoque (soft delete)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Insumo desativado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Insumo não encontrado",
            content = @Content(schema = @Schema()))
    })
    public ResponseEntity<Void> desativarInsumo(@PathVariable String id) {
        insumosService.deletar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
