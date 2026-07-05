package com.mecanica.oficina_api.adapters.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ordens_servico")
public class OrdemServicoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "veiculo_id", nullable = false)
    private String veiculoId;

    @Column(name = "cliente_id", nullable = false)
    private String clienteId;

    @Column(nullable = false)
    private String status;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(name = "orcamento_status")
    private String orcamentoStatus;

    @Column(name = "orcamento_observacoes", columnDefinition = "TEXT")
    private String orcamentoObservacoes;

    @Column(name = "orcamento_respondido_em")
    private LocalDateTime orcamentoRespondidoEm;

    @Column(name = "valor_final", precision = 12, scale = 2)
    private BigDecimal valorFinal;

    @Column(name = "data_final")
    private LocalDateTime dataFinal;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ordem_servico_id")
    private List<ItemOrcamentoJpaEntity> itensOrcamento = new ArrayList<>();
}
