package com.mecanica.oficina_api.adapters.web;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void deveMapearIllegalArgumentExceptionPara404() {
        ResponseEntity<Void> resposta = handler.handleNotFound(new IllegalArgumentException("não encontrado"));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deveMapearIllegalStateExceptionPara409() {
        ResponseEntity<Void> resposta = handler.handleConflict(new IllegalStateException("conflito de estado"));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}
