package com.mecanica.oficina_api.interfaces;

import com.mecanica.oficina_api.application.usuario.UsuarioService;
import com.mecanica.oficina_api.interfaces.dto.request.AlterarUsuarioRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarUsuarioRequest;
import com.mecanica.oficina_api.interfaces.dto.response.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("usuario")
@Tag(name = "Usuario", description = "Gerenciamento de usuários do sistema")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @Operation(summary = "Cadastrar um novo usuário", description = "Perfis aceitos: ADMIN, MECANICO, CLIENTE, ATENDENTE. Para perfil CLIENTE, clienteId é obrigatório.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "409", description = "Email já cadastrado", content = @Content(schema = @Schema()))
    })
    public ResponseEntity<Void> cadastrar(@RequestBody CadastrarUsuarioRequest request) {
        usuarioService.cadastrar(request);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário encontrado",
            content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
            content = @Content(schema = @Schema(implementation = ResponseStatusException.class)))
    })
    public ResponseEntity<UsuarioResponse> buscar(@PathVariable String id) {
        return ResponseEntity.ok(usuarioService.buscar(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Alterar usuário por ID", description = "Não altera senha. Para mudar perfil para CLIENTE, clienteId é obrigatório.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário alterado com sucesso",
            content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
            content = @Content(schema = @Schema(implementation = ResponseStatusException.class)))
    })
    public ResponseEntity<UsuarioResponse> alterar(@PathVariable String id, @RequestBody AlterarUsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.alterar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar usuário por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuário desativado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
            content = @Content(schema = @Schema(implementation = ResponseStatusException.class)))
    })
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        usuarioService.deletar(id);
        return ResponseEntity.status(204).build();
    }
}
