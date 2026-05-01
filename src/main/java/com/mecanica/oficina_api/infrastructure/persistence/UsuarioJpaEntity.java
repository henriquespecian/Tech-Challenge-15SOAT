package com.mecanica.oficina_api.infrastructure.persistence;

import com.mecanica.oficina_api.domain.usuario.Perfil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "usuarios")
public class UsuarioJpaEntity {

    @Id
    private String id;

    private String nome;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Perfil perfil;

    private String clienteId;

    @Column(updatable = false)
    private LocalDateTime dataCadastro;

    private LocalDateTime dataAtualizacao;

    private Boolean ativo;
}
