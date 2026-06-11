package com.mecanica.oficina_api.adapters.web;

import com.mecanica.oficina_api.adapters.web.dto.request.AlterarUsuarioRequest;
import com.mecanica.oficina_api.adapters.web.dto.request.CadastrarUsuarioRequest;
import com.mecanica.oficina_api.adapters.web.dto.response.UsuarioResponse;
import com.mecanica.oficina_api.adapters.web.presenter.UsuarioPresenter;
import com.mecanica.oficina_api.application.usuario.usecase.AlterarUsuarioUseCase;
import com.mecanica.oficina_api.application.usuario.usecase.CadastrarUsuarioUseCase;
import com.mecanica.oficina_api.application.usuario.usecase.ConsultarUsuarioUseCase;
import com.mecanica.oficina_api.application.usuario.usecase.InativarUsuarioUseCase;
import com.mecanica.oficina_api.domain.usuario.Usuario;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final CadastrarUsuarioUseCase cadastrarUsuarioUseCase;
    private final ConsultarUsuarioUseCase consultarUsuarioUseCase;
    private final AlterarUsuarioUseCase alterarUsuarioUseCase;
    private final InativarUsuarioUseCase inativarUsuarioUseCase;
    private final UsuarioPresenter presenter;

    public UsuarioController(CadastrarUsuarioUseCase cadastrarUsuarioUseCase,
                             ConsultarUsuarioUseCase consultarUsuarioUseCase,
                             AlterarUsuarioUseCase alterarUsuarioUseCase,
                             InativarUsuarioUseCase inativarUsuarioUseCase,
                             UsuarioPresenter presenter) {
        this.cadastrarUsuarioUseCase = cadastrarUsuarioUseCase;
        this.consultarUsuarioUseCase = consultarUsuarioUseCase;
        this.alterarUsuarioUseCase = alterarUsuarioUseCase;
        this.inativarUsuarioUseCase = inativarUsuarioUseCase;
        this.presenter = presenter;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cadastrar um novo usuário", description = "Perfis aceitos: ADMIN, MECANICO, CLIENTE, ATENDENTE. Para perfil CLIENTE, clienteId é obrigatório.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
            content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "409", description = "Email já cadastrado", content = @Content(schema = @Schema()))
    })
    public ResponseEntity<UsuarioResponse> cadastrar(@RequestBody CadastrarUsuarioRequest request) {
        Usuario usuario = cadastrarUsuarioUseCase.executar(
            request.getNome(), request.getEmail(), request.getSenha(),
            request.getPerfil(), request.getClienteId());
        return ResponseEntity.status(201).body(presenter.apresentar(usuario));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Buscar usuário por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário encontrado",
            content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
            content = @Content(schema = @Schema(implementation = ResponseStatusException.class)))
    })
    public ResponseEntity<UsuarioResponse> buscar(@PathVariable String id) {
        return ResponseEntity.ok(presenter.apresentar(consultarUsuarioUseCase.executar(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Alterar usuário por ID", description = "Não altera senha. Para mudar perfil para CLIENTE, clienteId é obrigatório.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário alterado com sucesso",
            content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
            content = @Content(schema = @Schema(implementation = ResponseStatusException.class)))
    })
    public ResponseEntity<UsuarioResponse> alterar(@PathVariable String id, @RequestBody AlterarUsuarioRequest request) {
        Usuario usuario = alterarUsuarioUseCase.executar(
            id, request.getNome(), request.getEmail(), request.getPerfil(), request.getClienteId());
        return ResponseEntity.ok(presenter.apresentar(usuario));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desativar usuário por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuário desativado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
            content = @Content(schema = @Schema(implementation = ResponseStatusException.class)))
    })
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        inativarUsuarioUseCase.executar(id);
        return ResponseEntity.status(204).build();
    }
}
