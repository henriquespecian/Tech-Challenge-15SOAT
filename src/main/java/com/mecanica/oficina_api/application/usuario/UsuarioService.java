package com.mecanica.oficina_api.application.usuario;

import com.mecanica.oficina_api.domain.usuario.Perfil;
import com.mecanica.oficina_api.domain.usuario.Usuario;
import com.mecanica.oficina_api.infrastructure.persistence.UsuarioJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.repository.ClienteSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.UsuarioSpringDataRepository;
import com.mecanica.oficina_api.interfaces.dto.request.AlterarUsuarioRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarUsuarioRequest;
import com.mecanica.oficina_api.interfaces.dto.response.UsuarioResponse;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

    private final UsuarioSpringDataRepository usuarioRepository;
    private final ClienteSpringDataRepository clienteRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioSpringDataRepository usuarioRepository,
                          ClienteSpringDataRepository clienteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public void cadastrar(CadastrarUsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
        }

        Perfil perfil = parsePerfil(request.getPerfil());
        validarClienteId(perfil, request.getClienteId());

        String senhaHash = passwordEncoder.encode(request.getSenha());
        Usuario usuario = Usuario.criar(request.getNome(), request.getEmail(), senhaHash, perfil, request.getClienteId());

        UsuarioJpaEntity entity = new UsuarioJpaEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setNome(usuario.getNome());
        entity.setEmail(usuario.getEmail());
        entity.setSenha(usuario.getSenha());
        entity.setPerfil(usuario.getPerfil());
        entity.setClienteId(usuario.getClienteId());
        entity.setDataCadastro(usuario.getDataCadastro());
        entity.setDataAtualizacao(usuario.getDataAtualizacao());
        entity.setAtivo(true);

        usuarioRepository.save(entity);
    }

    public UsuarioResponse buscar(String id) {
        UsuarioJpaEntity entity = usuarioRepository.findByIdAndAtivoTrue(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        return toResponse(entity);
    }

    public UsuarioResponse alterar(String id, AlterarUsuarioRequest request) {
        UsuarioJpaEntity entity = usuarioRepository.findByIdAndAtivoTrue(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        Perfil perfil = parsePerfil(request.getPerfil());
        validarClienteId(perfil, request.getClienteId());

        entity.setNome(request.getNome());
        entity.setEmail(request.getEmail());
        entity.setPerfil(perfil);
        entity.setClienteId(request.getClienteId());
        entity.setDataAtualizacao(LocalDateTime.now());

        usuarioRepository.save(entity);
        return toResponse(entity);
    }

    public void deletar(String id) {
        UsuarioJpaEntity entity = usuarioRepository.findByIdAndAtivoTrue(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        entity.setAtivo(false);
        usuarioRepository.save(entity);
    }

    private Perfil parsePerfil(String perfil) {
        try {
            return Perfil.valueOf(perfil);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Perfil inválido. Valores aceitos: ADMIN, MECANICO, CLIENTE, ATENDENTE");
        }
    }

    private void validarClienteId(Perfil perfil, String clienteId) {
        if (perfil != Perfil.CLIENTE) return;
        if (clienteId == null || clienteId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "clienteId é obrigatório para perfil CLIENTE");
        }
        clienteRepository.findById(clienteId)
            .filter(c -> c.getAtivo())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    private UsuarioResponse toResponse(UsuarioJpaEntity entity) {
        return new UsuarioResponse(
            entity.getId(),
            entity.getNome(),
            entity.getEmail(),
            entity.getPerfil(),
            entity.getClienteId(),
            entity.getDataCadastro(),
            entity.getDataAtualizacao()
        );
    }
}
