package com.mecanica.oficina_api.adapters.web;

import com.mecanica.oficina_api.adapters.web.dto.request.AlterarClienteRequest;
import com.mecanica.oficina_api.adapters.web.dto.request.CadastrarClienteRequest;
import com.mecanica.oficina_api.adapters.web.dto.response.ConsultarClienteResponse;
import com.mecanica.oficina_api.application.cliente.ClienteService;
import com.mecanica.oficina_api.domain.cliente.Cliente;

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
    Cliente cliente = clienteService.cadastrar(
        request.getNome(), request.getDocumento(), request.getEmail(), request.getTelefone()
    );
    return ResponseEntity.status(201).body(toResponse(cliente));
}

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Listar todos os clientes",
        description = "Retorna todos os clientes ativos cadastrados na oficina")
    @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso")
    public ResponseEntity<List<ConsultarClienteResponse>> listar() {
        List<Cliente> clientes = clienteService.listar();
        return ResponseEntity.ok(clientes.stream().map(this::toResponse).toList());
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
        return clienteService.consultar(documento)
            .map(cliente -> ResponseEntity.status(200).body(toResponse(cliente)))
            .orElseGet(() -> ResponseEntity.status(404).build());
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
        clienteService.alterar(documento, request.getNome(), request.getEmail(), request.getTelefone());
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

    private ConsultarClienteResponse toResponse(Cliente c) {
    return new ConsultarClienteResponse(
        c.getId(), c.getNome(), c.getDocumento().getValue(),
        c.getEmail().getValue(), c.getTelefone().getValue()
    );
}
}
