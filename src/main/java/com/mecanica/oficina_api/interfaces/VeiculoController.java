package com.mecanica.oficina_api.interfaces;

import com.mecanica.oficina_api.application.veiculo.VeiculoService;
import com.mecanica.oficina_api.interfaces.dto.request.AlterarVeiculoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarVeiculoRequest;
import com.mecanica.oficina_api.interfaces.dto.response.VeiculoResponse;
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
@RequestMapping("veiculo")
@Tag(name = "Veículo", description = "Gerenciamento de veículos dos clientes da oficina")
@SecurityRequirement(name = "bearerAuth")
public class VeiculoController {

    private final VeiculoService veiculoService;

    public VeiculoController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Cadastrar um novo veículo", description = "Permite cadastrar um novo veículo para um cliente na oficina")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Veículo criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da solicitação inválidos",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<Void> cadastrar(@RequestBody CadastrarVeiculoRequest request) {
        veiculoService.cadastrar(request);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE', 'MECANICO')")
    @Operation(summary = "Consultar veículo por ID", description = "Retorna os detalhes de um veículo específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Veículo encontrado"),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<VeiculoResponse> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(veiculoService.buscarPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE', 'MECANICO')")
    @Operation(summary = "Listar veículos de um cliente", description = "Retorna todos os veículos associados a um cliente")
    public ResponseEntity<List<VeiculoResponse>> listarPorCliente(@PathVariable String clienteId) {
        return ResponseEntity.ok(veiculoService.listarPorCliente(clienteId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE', 'MECANICO')")
    @Operation(summary = "Alterar um veículo", description = "Permite atualizar os dados de um veículo existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Veículo atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da solicitação inválidos",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<VeiculoResponse> alterar(@PathVariable String id, @RequestBody AlterarVeiculoRequest request) {
        return ResponseEntity.ok(veiculoService.alterar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Deletar um veículo por ID", description = "Permite deletar um veículo específico usando seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Veículo deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        veiculoService.deletar(id);
        return ResponseEntity.status(204).build();
    }
}
