package com.mecanica.oficina_api.domain.cliente.model;

import java.util.Objects;
import java.util.regex.Pattern;

public class Email {
    
    private static final Pattern EMAIL_PATTERN =
    Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final String value;

    public Email(String value) {
        if (value == null || !EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Email inválido: " + value);
        }
        this.value = value.toLowerCase().trim();
    }

    public String getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        return value.equals(email.value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }
}
