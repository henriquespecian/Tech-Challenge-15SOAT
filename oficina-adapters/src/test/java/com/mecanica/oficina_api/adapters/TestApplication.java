package com.mecanica.oficina_api.adapters;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

/**
 * Configuração de boot exclusiva para testes do módulo adapters.
 *
 * <p>O {@code @SpringBootApplication} real vive em {@code oficina-infrastructure}, que está
 * abaixo deste módulo na cadeia de dependências e portanto não está no classpath aqui. Os
 * slices {@code @WebMvcTest} e {@code @DataJpaTest} precisam de um {@code @SpringBootConfiguration}
 * para ancorar o contexto — esta classe cumpre esse papel apenas no escopo de teste.
 */
@SpringBootConfiguration
@EnableAutoConfiguration
public class TestApplication {
}
