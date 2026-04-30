package com.mecanica.oficina_api.infrastructure.security;

import com.mecanica.oficina_api.infrastructure.persistence.UsuarioJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.repository.UsuarioSpringDataRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioSpringDataRepository repository;

    public UserDetailsServiceImpl(UsuarioSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UsuarioJpaEntity usuario = repository.findByEmail(email)
                .filter(UsuarioJpaEntity::getAtivo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        return new User(
                usuario.getEmail(),
                usuario.getSenha(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getPerfil().name()))
        );
    }
}
