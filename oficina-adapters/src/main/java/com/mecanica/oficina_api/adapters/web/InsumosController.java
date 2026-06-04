package com.mecanica.oficina_api.adapters.web;

import com.mecanica.oficina_api.adapters.web.dto.request.AlterarInsumosRequest;
import com.mecanica.oficina_api.adapters.web.dto.request.CadastrarInsumosRequest;
import com.mecanica.oficina_api.adapters.web.dto.request.ComprarInsumoSimuladoRequest;
import com.mecanica.oficina_api.adapters.web.dto.response.InsumosResponse;
import com.mecanica.oficina_api.application.insumo.usecase.AlterarInsumoUseCase;
import com.mecanica.oficina_api.application.insumo.usecase.AtivarInsumoUseCase;
import com.mecanica.oficina_api.application.insumo.usecase.CadastrarInsumoUseCase;
import com.mecanica.oficina_api.application.insumo.usecase.ConsultarInsumoUseCase;
import com.mecanica.oficina_api.application.insumo.usecase.InativarInsumoUseCase;
import com.mecanica.oficina_api.application.insumo.usecase.ListarInsumosUseCase;
import com.mecanica.oficina_api.application.insumo.usecase.RegistrarCompraUseCase;
import com.mecanica.oficina_api.domain.insumo.Insumos;

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

    private final CadastrarInsumoUseCase cadastrarInsumoUseCase;
    private final ConsultarInsumoUseCase consultarInsumoUseCase;
    private final ListarInsumosUseCase listarInsumosUseCase;
    private final AlterarInsumoUseCase alterarInsumoUseCase;
    private final AtivarInsumoUseCase ativarInsumoUseCase;
    private final InativarInsumoUseCase inativarInsumoUseCase;
    private final RegistrarCompraUseCase registrarCompraUseCase;

    public InsumosController(
            CadastrarInsumoUseCase cadastrarInsumoUseCase,
            ConsultarInsumoUseCase consultarInsumoUseCase,
            ListarInsumosUseCase listarInsumosUseCase,
            AlterarInsumoUseCase alterarInsumoUseCase,
            AtivarInsumoUseCase ativarInsumoUseCase,
            InativarInsumoUseCase inativarInsumoUseCase,
            RegistrarCompraUseCase registrarCompraUseCase) {
        this.cadastrarInsumoUseCase = cadastrarInsumoUseCase;
        this.consultarInsumoUseCase = consultarInsumoUseCase;
        this.listarInsumosUseCase = listarInsumosUseCase;
        this.alterarInsumoUseCase = alterarInsumoUseCase;
        this.ativarInsumoUseCase = ativarInsumoUseCase;
        this.inativarInsumoUseCase = inativarInsumoUseCase;
        this.registrarCompraUseCase = registrarCompraUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE', 'MECANICO')")
    @Operation(summary = "Listar insumos ativos", description = "Retorna todos os insumos e peças com ativo=true")
    @ApiResponse(responseCode = "200", description = "Lista de insumos ativos")
    public ResponseEntity<List<InsumosResponse>> listarInsumos() {
        return ResponseEntity.ok(listarInsumosUseCase.executar().stream().map(this::toResponse).toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE', 'MECANICO')")
    @Operation(summary = "Buscar insumo por ID", description = "Retorna os dados de um insumo ativo pelo seu identificador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Insumo encontrado",
            content = @Content(schema = @Schema(implementation = InsumosResponse.class))),
        @ApiResponse(responseCode = "404", description = "Insumo não encontrado ou inativo",
            content = @Content(schema = @Schema()))
    })
    public ResponseEntity<InsumosResponse> buscarInsumoPorId(@PathVariable String id) {
        return ResponseEntity.ok(toResponse(consultarInsumoUseCase.executar(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Cadastrar insumo", description = "Cadastra um novo insumo ou peça no estoque")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Insumo cadastrado com sucesso",
            content = @Content(schema = @Schema(implementation = InsumosResponse.class))),
        @ApiResponse(responseCode = "409", description = "Insumo com o mesmo nome já existe",
            content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "400", description = "Dados da solicitação inválidos",
            content = @Content(schema = @Schema()))
    })
    public ResponseEntity<InsumosResponse> cadastrarInsumo(@RequestBody CadastrarInsumosRequest request) {
        var insumo = cadastrarInsumoUseCase.executar(
            request.getNome(),
            request.getPrecoUnitario(),
            request.getEstoqueAtual(),
            request.getEstoqueMinimo(),
            request.getUnidade()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(insumo));
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
        alterarInsumoUseCase.executar(
            id,
            request.getNome(),
            request.getPrecoUnitario(),
            request.getEstoqueAtual(),
            request.getEstoqueMinimo(),
            request.getUnidade()
        );
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/compra-simulada")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Simular compra de insumo",
        description = "Registra uma entrega simulada: incrementa o estoque como se a compra tivesse sido concluída (sem fornecedor real)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estoque atualizado após simulação de compra",
            content = @Content(schema = @Schema(implementation = InsumosResponse.class))),
        @ApiResponse(responseCode = "404", description = "Insumo ativo não encontrado",
            content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "400", description = "Quantidade inválida",
            content = @Content(schema = @Schema()))
    })
    public ResponseEntity<InsumosResponse> compraSimulada(
            @PathVariable String id,
            @RequestBody ComprarInsumoSimuladoRequest request) {
        return ResponseEntity.ok(toResponse(registrarCompraUseCase.executar(id, request.getQuantidade())));
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
        return ResponseEntity.ok(toResponse(ativarInsumoUseCase.executar(id)));
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
        inativarInsumoUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }

    private InsumosResponse toResponse(Insumos insumo) {
        return new InsumosResponse(
            insumo.getId(),
            insumo.getNome(),
            insumo.getPrecoUnitario(),
            insumo.getEstoqueAtual(),
            insumo.getEstoqueMinimo(),
            insumo.getUnidade(),
            insumo.getAtivo()
        );
    }
}
