package com.mecanica.oficina_api.adapters.web.presenter;

import com.mecanica.oficina_api.adapters.web.dto.response.UsuarioResponse;
import com.mecanica.oficina_api.domain.usuario.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;

/** Converte a entidade de domínio {@link Usuario} em DTOs de resposta HTTP. */
@Component
public class UsuarioPresenter {

    public UsuarioResponse apresentar(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(), usuario.getNome(), usuario.getEmail(),
                usuario.getPerfil(), usuario.getClienteId(),
                usuario.getDataCadastro(), usuario.getDataAtualizacao());
    }

    public List<UsuarioResponse> apresentar(List<Usuario> usuarios) {
        return usuarios.stream().map(this::apresentar).toList();
    }
}
