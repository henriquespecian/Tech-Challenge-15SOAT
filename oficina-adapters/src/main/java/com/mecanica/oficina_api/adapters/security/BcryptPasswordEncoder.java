package com.mecanica.oficina_api.adapters.security;

import com.mecanica.oficina_api.application.usuario.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BcryptPasswordEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    @Override
    public String encode(String senha) {
        return bcrypt.encode(senha);
    }
}
