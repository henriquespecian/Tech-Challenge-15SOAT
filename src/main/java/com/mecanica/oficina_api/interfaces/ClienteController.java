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
import org.springframework.web.server.ResponseStatusException;

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
    @Operation(summary = "Cadastrar um novo cliente", description = "Permite cadastrar um novo cliente na oficina")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da solicitação inválidos",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<Void> cadastrar(@RequestBody CadastrarClienteRequest request) {
        clienteService.cadastrar(request);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/{cpf}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE', 'MECANICO')")
    @Operation(summary = "Consultar cliente por CPF", description = "Permite consultar os detalhes de um cliente específico usando seu CPF")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso",
        content = @Content(schema = @Schema(implementation = ConsultarClienteResponse.class))),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
        content =  @Content(schema = @Schema(implementation = ResponseStatusException.class))),
    })
    public ResponseEntity<ConsultarClienteResponse> consultar(@PathVariable String cpf) {
        var cliente_encontrado = clienteService.consultar(cpf);
        return ResponseEntity.status(200).body(cliente_encontrado);
    }

    @PutMapping("/{cpf}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Alterar um cliente por CPF", description = "Permite alterar os dados de um cliente específico usando seu CPF")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cliente encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
            content =  @Content(schema = @Schema(implementation = ResponseStatusException.class))),
    })
    public ResponseEntity<Void> alterar(@PathVariable String cpf, @RequestBody AlterarClienteRequest request) {
        clienteService.alterar(cpf, request);
        return ResponseEntity.status(204).build();
    }

    @DeleteMapping("/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desativar um cliente por CPF", description = "Permite desativar um cliente específico usando seu CPF")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cliente desativado com sucesso")
    })
    public ResponseEntity<Void> deletar(@PathVariable String cpf) {
        clienteService.deletar(cpf);
        return ResponseEntity.status(204).build();
    }

}
