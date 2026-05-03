package com.mecanica.oficina_api.interfaces;

import com.mecanica.oficina_api.application.cliente.ClienteService;
import com.mecanica.oficina_api.interfaces.dto.request.AlterarClienteRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarClienteRequest;
import com.mecanica.oficina_api.interfaces.dto.response.ConsultarClienteResponse;
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

@RestController
@RequestMapping("cliente")
@Tag(name = "Cliente", description = "Gerenciamento de clientes da oficina")
@SecurityRequirement(name = "bearerAuth")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Cadastrar um novo cliente", description = "Permite cadastrar um novo cliente na oficina. Aceita CPF (11 dígitos) ou CNPJ (14 dígitos)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso",
                    content = @Content(schema = @Schema(implementation = ConsultarClienteResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados da solicitação inválidos",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<ConsultarClienteResponse> cadastrar(@RequestBody CadastrarClienteRequest request) {
        return ResponseEntity.status(201).body(clienteService.cadastrar(request));
    }

    @GetMapping("/{documento}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE', 'MECANICO')")
    @Operation(summary = "Consultar cliente por CPF ou CNPJ", description = "Permite consultar os detalhes de um cliente específico usando seu CPF ou CNPJ")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso",
            content = @Content(schema = @Schema(implementation = ConsultarClienteResponse.class))),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
            content = @Content(schema = @Schema()))
    })
    public ResponseEntity<ConsultarClienteResponse> consultar(@PathVariable String documento) {
        return ResponseEntity.status(200).body(clienteService.consultar(documento));
    }

    @PutMapping("/{documento}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Alterar um cliente por CPF ou CNPJ", description = "Permite alterar os dados de um cliente específico usando seu CPF ou CNPJ")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cliente alterado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
            content = @Content(schema = @Schema()))
    })
    public ResponseEntity<Void> alterar(@PathVariable String documento, @RequestBody AlterarClienteRequest request) {
        clienteService.alterar(documento, request);
        return ResponseEntity.status(204).build();
    }

    @DeleteMapping("/{documento}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desativar um cliente por CPF ou CNPJ", description = "Permite desativar um cliente específico usando seu CPF ou CNPJ")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cliente desativado com sucesso")
    })
    public ResponseEntity<Void> deletar(@PathVariable String documento) {
        clienteService.deletar(documento);
        return ResponseEntity.status(204).build();
    }
}
