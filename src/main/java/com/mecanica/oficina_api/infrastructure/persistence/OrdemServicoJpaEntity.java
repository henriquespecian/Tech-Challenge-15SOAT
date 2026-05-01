package com.mecanica.oficina_api.infrastructure.persistence;

import jakarta.persistence.*;
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

    @Column(name = "orcamento_status")
    private String orcamentoStatus;

    @Column(name = "orcamento_observacoes", columnDefinition = "TEXT")
    private String orcamentoObservacoes;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ordem_servico_id")
    private List<ItemOrcamentoJpaEntity> itensOrcamento = new ArrayList<>();
}
