package com.mecanica.oficina_api.adapters.persistence;

import java.math.BigDecimal;
import java.time.Duration;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "servicos")
@Getter
@Setter
public class ServicoJpaEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false, name = "tempo_estimado_horas")
    private Duration tempoEstimadoHoras;

    @Column(nullable = false)
    private boolean ativo;
}
