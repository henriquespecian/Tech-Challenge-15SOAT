package com.mecanica.oficina_api.interfaces;

import com.mecanica.oficina_api.application.cliente.ClienteService;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarClienteRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("cliente")
@Tag(name = "Cliente", description = "Gerenciamento de clientes da oficina")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
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

    @GetMapping("/{idCliente}")
    @Operation(summary = "Consultar cliente por ID", description = "Permite consultar os detalhes de um cliente específico usando seu ID")
    public ResponseEntity<Void> consultar(@PathVariable String idCliente) {
        return ResponseEntity.ok().build();
    }
}
