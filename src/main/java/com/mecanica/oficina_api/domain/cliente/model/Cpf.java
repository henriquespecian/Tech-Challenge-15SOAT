package com.mecanica.oficina_api.domain.cliente.model;

import java.util.Objects;

public class Cpf {
        private final String value;

    public Cpf(String value) {
        if (value == null || !isValid(value)) {
            throw new IllegalArgumentException("CPF inválido: " + value);
        }
        this.value = cleanCpf(value);
    }

    private String cleanCpf(String cpf) {
        return cpf.replaceAll("[^0-9]", "");
    }

    private boolean isValid(String cpf) {
        String cleaned = cleanCpf(cpf);
        if (cleaned.length() != 11) return false;
        if (cleaned.chars().distinct().count() == 1) return false;

        // Validação dos dígitos verificadores
        int[] weights1 = {10, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] weights2 = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};

        int sum1 = 0;
        for (int i = 0; i < 9; i++) {
            sum1 += Character.getNumericValue(cleaned.charAt(i)) * weights1[i];
        }
        int digit1 = 11 - (sum1 % 11);
        digit1 = digit1 >= 10 ? 0 : digit1;

        int sum2 = 0;
        for (int i = 0; i < 10; i++) {
            sum2 += Character.getNumericValue(cleaned.charAt(i)) * weights2[i];
        }
        int digit2 = 11 - (sum2 % 11);
        digit2 = digit2 >= 10 ? 0 : digit2;

        return Character.getNumericValue(cleaned.charAt(9)) == digit1
            && Character.getNumericValue(cleaned.charAt(10)) == digit2;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cpf cpf)) return false;
        return value.equals(cpf.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
