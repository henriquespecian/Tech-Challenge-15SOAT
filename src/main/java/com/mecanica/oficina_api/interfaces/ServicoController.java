package com.mecanica.oficina_api.interfaces;

import com.mecanica.oficina_api.application.servico.ServicoService;
import com.mecanica.oficina_api.interfaces.dto.request.AlterarServicoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarServicoRequest;
import com.mecanica.oficina_api.interfaces.dto.response.ServicoResponse;
import com.mecanica.oficina_api.interfaces.dto.response.TempoMedioServicoResponse;
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
@RequestMapping("servico")
@Tag(name = "Serviço", description = "Catálogo de serviços oferecidos pela oficina")
@SecurityRequirement(name = "bearerAuth")
public class ServicoController {

    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cadastrar serviço", description = "Cadastra um novo serviço no catálogo da oficina")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Serviço cadastrado com sucesso",
                    content = @Content(schema = @Schema(implementation = ServicoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<ServicoResponse> cadastrar(@RequestBody CadastrarServicoRequest request) {
        return ResponseEntity.status(201).body(servicoService.cadastrar(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE', 'MECANICO')")
    @Operation(summary = "Buscar serviço por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço encontrado",
                    content = @Content(schema = @Schema(implementation = ServicoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<ServicoResponse> buscar(@PathVariable String id) {
        return ResponseEntity.ok(servicoService.buscar(id));
    }

    @GetMapping("/{id}/tempo-medio")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE', 'MECANICO')")
    @Operation(summary = "Buscar tempo médio de execução de um serviço por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Serviço encontrado",
            content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "404", description = "Serviço não encontrado",
            content = @Content(schema = @Schema()))
    })
    public ResponseEntity<TempoMedioServicoResponse> buscarTempoMedio(@PathVariable String id) {
        return ResponseEntity.ok(servicoService.buscarTempoMedio(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE', 'MECANICO')")
    @Operation(summary = "Listar serviços ativos", description = "Retorna todos os serviços ativos do catálogo")
    @ApiResponse(responseCode = "200", description = "Lista de serviços ativos")
    public ResponseEntity<List<ServicoResponse>> listar() {
        return ResponseEntity.ok(servicoService.listar());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Alterar serviço", description = "Atualiza os dados de um serviço existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço atualizado",
                    content = @Content(schema = @Schema(implementation = ServicoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<ServicoResponse> alterar(@PathVariable String id,
                                                   @RequestBody AlterarServicoRequest request) {
        return ResponseEntity.ok(servicoService.alterar(id, request));
    }

    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ativar serviço", description = "Reativa um serviço previamente inativado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço ativado",
                    content = @Content(schema = @Schema(implementation = ServicoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<ServicoResponse> ativar(@PathVariable String id) {
        return ResponseEntity.ok(servicoService.ativar(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Inativar serviço", description = "Inativa um serviço do catálogo (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Serviço inativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<Void> inativar(@PathVariable String id) {
        servicoService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
