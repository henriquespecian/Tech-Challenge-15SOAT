package com.mecanica.oficina_api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Oficina API",
                version = "1.0",
                description = "API de gerenciamento da oficina mecânica. Para acessar os endpoints protegidos, " +
                              "faça login em POST /auth/login e cole o token no botão 'Authorize' acima."
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Token JWT obtido via POST /auth/login"
)
public class OpenApiConfig {}
