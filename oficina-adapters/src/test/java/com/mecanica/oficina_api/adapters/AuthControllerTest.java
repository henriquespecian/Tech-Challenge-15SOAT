package com.mecanica.oficina_api.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mecanica.oficina_api.adapters.common.FieldError;
import com.mecanica.oficina_api.adapters.common.FileUtils;
import com.mecanica.oficina_api.adapters.common.ValidationErrorResponse;
import com.mecanica.oficina_api.adapters.persistence.UsuarioJpaEntity;
import com.mecanica.oficina_api.adapters.persistence.repository.UsuarioSpringDataRepository;
import com.mecanica.oficina_api.adapters.security.JwtService;
import com.mecanica.oficina_api.adapters.web.AuthController;
import com.mecanica.oficina_api.adapters.web.dto.request.LoginRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
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
    private final FileUtils fileUtils = new FileUtils();

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

//    @ParameterizedTest
//    @MethodSource("dadosCamposInvalidos")
//    void deveRetornar400_quandoCamposEstaoInvalidos(String filename, List<String> errors) throws Exception {
//        var request = fileUtils.readResourceFile("/auth/%s".formatted(filename));
//
//        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
//                .content(request)
//                .contentType(MediaType.APPLICATION_JSON)
//            )
//            .andDo(MockMvcResultHandlers.print())
//            .andExpect(MockMvcResultMatchers.status().isBadRequest())
//            .andReturn()
//            .getResponse()
//            .getContentAsString();
//
//        var responseError = objectMapper.readValue(mvcResult, ValidationErrorResponse.class);
//
//        Assertions.assertThat(responseError.erros())
//            .extracting(FieldError::mensagem)
//            .containsExactlyInAnyOrderElementsOf(errors);
//    }

    private static Stream<Arguments> dadosCamposInvalidos() {
        var errosComuns = errosComuns();

        var erroCampoEmailInvalido = new ArrayList<>(List.of("E-mail inválido"));

        return Stream.of(
            Arguments.of("login-400-campos-nulos.json", errosComuns),
            Arguments.of("login-400-campos-vazios.json", errosComuns),
            Arguments.of("login-400-email-invalido.json", erroCampoEmailInvalido)
        );
    }

    private static List<String> errosComuns() {
        var erroEmail = "O e-mail é obrigatório";
        var erroSenha = "A senha é obrigatória";

        return new ArrayList<>(List.of(erroEmail, erroSenha));
    }
}
