package com.mecanica.oficina_api.application.usuario;

public interface PasswordEncoder {
    String encode(String senha);
}
