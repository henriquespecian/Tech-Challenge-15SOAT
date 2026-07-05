package com.mecanica.oficina_api.adapters.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class WebhookTokenValidator {

    private final String webhookSecret;

    public WebhookTokenValidator(@Value("${webhook.secret}") String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    public void validate(String token) {
        if (token == null || token.isBlank() || !webhookSecret.equals(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Webhook token inválido");
        }
    }
}
