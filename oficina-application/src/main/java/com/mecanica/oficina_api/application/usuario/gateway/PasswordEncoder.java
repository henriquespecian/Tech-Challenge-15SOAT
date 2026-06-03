package com.mecanica.oficina_api.application.usuario.gateway;

public interface PasswordEncoder {
    String encode(String senha);
}
