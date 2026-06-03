package com.mecanica.oficina_api.domain.usuario;

public enum Perfil {
    ADMIN, MECANICO, CLIENTE, ATENDENTE;

    public static Perfil fromString(String perfil) {
        try {
            return Perfil.valueOf(perfil);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Perfil inválido. Valores aceitos: ADMIN, MECANICO, CLIENTE, ATENDENTE");
        }
    }
}

