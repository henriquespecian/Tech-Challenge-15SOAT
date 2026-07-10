package com.mecanica.oficina_api.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mecanica.oficina_api.adapters.persistence.UsuarioJpaEntity;
import com.mecanica.oficina_api.adapters.persistence.repository.UsuarioSpringDataRepository;
import com.mecanica.oficina_api.adapters.security.JwtService;
import com.mecanica.oficina_api.adapters.web.AuthController;
import com.mecanica.oficina_api.adapters.web.dto.request.LoginRequest;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private UsuarioSpringDataRepository usuarioRepository;

    private static final String EMAIL = "admin@email.com";

    @BeforeEach
    void setUp() {
        AuthController controller = new AuthController(authenticationManager, jwtService, usuarioRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void deveAutenticarERetornarToken_quandoCredenciaisValidas() throws Exception {
        UserDetails userDetails = User.withUsername(EMAIL).password("x").roles("ADMIN").build();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtService.gerarToken(any())).thenReturn("jwt-token");

        UsuarioJpaEntity entity = new UsuarioJpaEntity();
        entity.setNome("Administrador");
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(entity));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(EMAIL, "123456"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.nome").value("Administrador"))
                .andExpect(jsonPath("$.perfil").value("ADMIN"));
    }

    @Test
    void deveRetornarNomeVazio_quandoUsuarioNaoEstaNoRepositorio() throws Exception {
        UserDetails userDetails = User.withUsername(EMAIL).password("x").roles("ATENDENTE").build();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtService.gerarToken(any())).thenReturn("jwt-token");
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(EMAIL, "123456"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.nome").value(""))
                .andExpect(jsonPath("$.perfil").value("ATENDENTE"));
    }
}