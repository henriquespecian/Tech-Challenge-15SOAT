package com.mecanica.oficina_api.domain.cliente.model;

import java.util.Objects;

public final class Cnpj implements Documento {

    private final String value;

    public Cnpj(String value) {
        if (value == null || !isValid(value)) {
            throw new IllegalArgumentException("CNPJ inválido: " + value);
        }
        this.value = value.replaceAll("[^0-9]", "");
    }

    private boolean isValid(String cnpj) {
        String cleaned = cnpj.replaceAll("[^0-9]", "");
        if (cleaned.length() != 14) return false;
        if (cleaned.chars().distinct().count() == 1) return false;

        int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int sum1 = 0;
        for (int i = 0; i < 12; i++) {
            sum1 += Character.getNumericValue(cleaned.charAt(i)) * weights1[i];
        }
        int digit1 = sum1 % 11 < 2 ? 0 : 11 - (sum1 % 11);

        int sum2 = 0;
        for (int i = 0; i < 13; i++) {
            sum2 += Character.getNumericValue(cleaned.charAt(i)) * weights2[i];
        }
        int digit2 = sum2 % 11 < 2 ? 0 : 11 - (sum2 % 11);

        return Character.getNumericValue(cleaned.charAt(12)) == digit1
            && Character.getNumericValue(cleaned.charAt(13)) == digit2;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cnpj cnpj)) return false;
        return value.equals(cnpj.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
