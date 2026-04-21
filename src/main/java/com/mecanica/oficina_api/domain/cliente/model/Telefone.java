package com.mecanica.oficina_api.domain.cliente.model;

import java.util.Objects;

public class Telefone {
    private final String value;

    public Telefone(String value) {
        if (value == null || value.replaceAll("[^0-9]", "").length() < 10) {
            throw new IllegalArgumentException("Telefone inválido: " + value);
        }
        this.value = value.replaceAll("[^0-9]", "");
    }

    public String getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Telefone tel)) return false;
        return value.equals(tel.value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }
}
