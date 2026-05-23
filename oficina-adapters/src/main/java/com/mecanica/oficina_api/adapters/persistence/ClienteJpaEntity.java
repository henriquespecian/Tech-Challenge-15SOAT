package com.mecanica.oficina_api.adapters.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "clientes")
public class ClienteJpaEntity {

    @Id
    private String id;

    private String nome;
    @Column(unique = true, nullable = false)
    private String documento;
    private String email;
    private String telefone;
    @Column(updatable = false)
    private LocalDateTime dataCadastro;
    private Boolean ativo;
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
}
