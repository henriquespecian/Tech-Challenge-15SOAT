package com.mecanica.oficina_api.domain.cliente.model;

public sealed interface Documento permits Cpf, Cnpj {

    String getValue();

    static Documento parse(String valor) {
        String limpo = valor.replaceAll("[^0-9]", "");
        if (limpo.length() == 11) return new Cpf(valor);
        if (limpo.length() == 14) return new Cnpj(valor);
        throw new IllegalArgumentException("Documento inválido: deve ser CPF (11 dígitos) ou CNPJ (14 dígitos)");
    }
}
