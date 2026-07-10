package com.mecanica.oficina_api.infrastructure.integration;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers(disabledWithoutDocker = true)
public abstract class PostgresTestcontainersSupport {

    @SuppressWarnings("resource")
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("oficina_test")
            .withUsername("postgres")
            .withPassword("postgres");

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void configurarDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
    }
}
