package com.mecanica.oficina_api.interfaces;

import com.mecanica.oficina_api.infrastructure.persistence.repository.UsuarioSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.security.JwtService;
import com.mecanica.oficina_api.interfaces.dto.request.LoginRequest;
import com.mecanica.oficina_api.interfaces.dto.response.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação")
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
    @Operation(summary = "Autenticar usuário e obter token JWT")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
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
