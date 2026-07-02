package com.mecanica.oficina_api.adapters.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mecanica.oficina_api.adapters.persistence.repository.UsuarioSpringDataRepository;
import com.mecanica.oficina_api.adapters.security.JwtService;
import com.mecanica.oficina_api.adapters.web.dto.request.LoginRequest;
import com.mecanica.oficina_api.adapters.web.dto.response.LoginResponse;
import com.mecanica.oficina_api.adapters.web.dto.response.ValidationErrorResponse;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Autenticação e emissão de tokens JWT")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioSpringDataRepository usuarioRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UsuarioSpringDataRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário e obter token JWT",
               description = "Recebe email e senha, retorna token JWT para uso no header Authorization: Bearer {token}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autenticação realizada com sucesso",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "Erro de validação nos campos de entrada",
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas",
            content = @Content(schema = @Schema()))
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha())
        );

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = jwtService.gerarToken(userDetails);

        String nome = usuarioRepository.findByEmail(userDetails.getUsername())
                .map(u -> u.getNome())
                .orElse("");

        String perfil = userDetails.getAuthorities().iterator().next().getAuthority()
                .replace("ROLE_", "");

        return ResponseEntity.ok(new LoginResponse(token, nome, perfil));
    }
}
