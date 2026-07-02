package com.mecanica.oficina_api.adapters.web;

import com.mecanica.oficina_api.adapters.web.dto.request.AlterarClienteRequest;
import com.mecanica.oficina_api.adapters.web.dto.request.CadastrarClienteRequest;
import com.mecanica.oficina_api.adapters.web.dto.response.ConsultarClienteResponse;
import com.mecanica.oficina_api.adapters.web.dto.response.ValidationErrorResponse;
import com.mecanica.oficina_api.adapters.web.presenter.ClientePresenter;
import com.mecanica.oficina_api.application.cliente.usecase.AlterarClienteUseCase;
import com.mecanica.oficina_api.application.cliente.usecase.CadastrarClienteUseCase;
import com.mecanica.oficina_api.application.cliente.usecase.ConsultarClienteUseCase;
import com.mecanica.oficina_api.application.cliente.usecase.DeletarClienteUseCase;
import com.mecanica.oficina_api.application.cliente.usecase.ListarClientesUseCase;
import com.mecanica.oficina_api.domain.cliente.Cliente;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("cliente")
@Tag(name = "Cliente", description = "Gerenciamento de clientes da oficina")
@SecurityRequirement(name = "bearerAuth")
public class ClienteController {

    private final CadastrarClienteUseCase cadastrarClienteUseCase;
    private final ConsultarClienteUseCase consultarClienteUseCase;
    private final ListarClientesUseCase listarClientesUseCase;
    private final AlterarClienteUseCase alterarClienteUseCase;
    private final DeletarClienteUseCase deletarClienteUseCase;
    private final ClientePresenter presenter;

    public ClienteController(CadastrarClienteUseCase cadastrarClienteUseCase,
                             ConsultarClienteUseCase consultarClienteUseCase,
                             ListarClientesUseCase listarClientesUseCase,
                             AlterarClienteUseCase alterarClienteUseCase,
                             DeletarClienteUseCase deletarClienteUseCase,
                             ClientePresenter presenter) {
        this.cadastrarClienteUseCase = cadastrarClienteUseCase;
        this.consultarClienteUseCase = consultarClienteUseCase;
        this.listarClientesUseCase = listarClientesUseCase;
        this.alterarClienteUseCase = alterarClienteUseCase;
        this.deletarClienteUseCase = deletarClienteUseCase;
        this.presenter = presenter;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Cadastrar um novo cliente", description = "Permite cadastrar um novo cliente na oficina. Aceita CPF (11 dígitos) ou CNPJ (14 dígitos)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso",
                    content = @Content(schema = @Schema(implementation = ConsultarClienteResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados da solicitação inválidos ou erro de validação",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
    })
    public ResponseEntity<ConsultarClienteResponse> cadastrar(@Valid @RequestBody CadastrarClienteRequest request) {
    Cliente cliente = cadastrarClienteUseCase.executar(
        request.getNome(), request.getDocumento(), request.getEmail(), request.getTelefone()
    );
    return ResponseEntity.status(201).body(presenter.apresentar(cliente));
}

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Listar todos os clientes",
        description = "Retorna todos os clientes ativos cadastrados na oficina")
    @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso")
    public ResponseEntity<List<ConsultarClienteResponse>> listar() {
        List<Cliente> clientes = listarClientesUseCase.executar();
        return ResponseEntity.ok(presenter.apresentar(clientes));
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
        return consultarClienteUseCase.executar(documento)
            .map(cliente -> ResponseEntity.status(200).body(presenter.apresentar(cliente)))
            .orElseGet(() -> ResponseEntity.status(404).build());
    }

    @PutMapping("/{documento}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Alterar um cliente por CPF ou CNPJ", description = "Permite alterar os dados de um cliente específico usando seu CPF ou CNPJ")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cliente alterado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro de validação nos campos de entrada",
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
            content = @Content(schema = @Schema()))
    })
    public ResponseEntity<Void> alterar(@PathVariable String documento, @Valid @RequestBody AlterarClienteRequest request) {
        alterarClienteUseCase.executar(documento, request.getNome(), request.getEmail(), request.getTelefone());
        return ResponseEntity.status(204).build();
    }

    @DeleteMapping("/{documento}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desativar um cliente por CPF ou CNPJ", description = "Permite desativar um cliente específico usando seu CPF ou CNPJ")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cliente desativado com sucesso")
    })
    public ResponseEntity<Void> deletar(@PathVariable String documento) {
        deletarClienteUseCase.executar(documento);
        return ResponseEntity.status(204).build();
    }
}
