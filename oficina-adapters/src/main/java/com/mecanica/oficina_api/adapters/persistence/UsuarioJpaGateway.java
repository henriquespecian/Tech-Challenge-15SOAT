package com.mecanica.oficina_api.adapters.persistence;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.mecanica.oficina_api.adapters.persistence.repository.UsuarioSpringDataRepository;
import com.mecanica.oficina_api.application.usuario.gateway.UsuarioGateway;
import com.mecanica.oficina_api.domain.usuario.Usuario;

@Repository
public class UsuarioJpaGateway implements UsuarioGateway {

    private final UsuarioSpringDataRepository repo;

    public UsuarioJpaGateway(UsuarioSpringDataRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<Usuario> buscar(String id) {
        return repo.findByIdAndAtivoTrue(id).map(this::toDomain);
    }

    @Override
    public boolean existePorEmail(String email) {
        return repo.existsByEmail(email);
    }

    @Override
    public Usuario cadastrar(Usuario usuario) {
        UsuarioJpaEntity entity = toEntity(usuario);
        return toDomain(repo.save(entity));
    }

    @Override
    public void inativar(String id) {
        repo.findByIdAndAtivoTrue(id).ifPresent(e -> {
            e.setAtivo(false);
            e.setDataAtualizacao(LocalDateTime.now());
            repo.save(e);
        });
    }

    @Override
    public Usuario alterar(String id, Usuario usuario) {
        UsuarioJpaEntity entity = repo.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + id));
        entity.setNome(usuario.getNome());
        entity.setEmail(usuario.getEmail());
        entity.setSenha(usuario.getSenha());
        entity.setPerfil(usuario.getPerfil());
        entity.setClienteId(usuario.getClienteId());
        entity.setDataAtualizacao(LocalDateTime.now());
        return toDomain(repo.save(entity));
    }

    private Usuario toDomain(UsuarioJpaEntity e) {
        return Usuario.reconstituir(e.getId(), e.getNome(), e.getEmail(), e.getSenha(), e.getPerfil());
    }

    private UsuarioJpaEntity toEntity(Usuario u) {
        UsuarioJpaEntity e = new UsuarioJpaEntity();
        e.setId(u.getId() != null ? u.getId() : UUID.randomUUID().toString());
        e.setNome(u.getNome());
        e.setEmail(u.getEmail());
        e.setSenha(u.getSenha());
        e.setPerfil(u.getPerfil());
        e.setClienteId(u.getClienteId());
        e.setDataCadastro(u.getDataCadastro() != null ? u.getDataCadastro() : LocalDateTime.now());
        e.setDataAtualizacao(u.getDataAtualizacao() != null ? u.getDataAtualizacao() : LocalDateTime.now());
        e.setAtivo(true);
        return e;
    }
}
