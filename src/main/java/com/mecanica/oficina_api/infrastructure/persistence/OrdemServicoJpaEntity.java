package com.mecanica.oficina_api.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "valor_final", precision = 12, scale = 2)
    private BigDecimal valorFinal;

    @Column(name = "data_final")
    private LocalDateTime dataFinal;

    @Column(name = "orcamento_status")
    private String orcamentoStatus;

    @Column(name = "orcamento_observacoes", columnDefinition = "TEXT")
    private String orcamentoObservacoes;

    @Column(name = "orcamento_respondido_em")
    private LocalDateTime orcamentoRespondidoEm;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ordem_servico_id")
    private List<ItemOrcamentoJpaEntity> itensOrcamento = new ArrayList<>();

    public OrdemServicoJpaEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getVeiculoId() { return veiculoId; }
    public void setVeiculoId(String veiculoId) { this.veiculoId = veiculoId; }
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getValorFinal() { return valorFinal; }
    public void setValorFinal(BigDecimal valorFinal) { this.valorFinal = valorFinal; }
    public LocalDateTime getDataFinal() { return dataFinal; }
    public void setDataFinal(LocalDateTime dataFinal) { this.dataFinal = dataFinal; }
    public String getOrcamentoStatus() { return orcamentoStatus; }
    public void setOrcamentoStatus(String orcamentoStatus) { this.orcamentoStatus = orcamentoStatus; }
    public String getOrcamentoObservacoes() { return orcamentoObservacoes; }
    public void setOrcamentoObservacoes(String orcamentoObservacoes) { this.orcamentoObservacoes = orcamentoObservacoes; }
    public LocalDateTime getOrcamentoRespondidoEm() { return orcamentoRespondidoEm; }
    public void setOrcamentoRespondidoEm(LocalDateTime orcamentoRespondidoEm) { this.orcamentoRespondidoEm = orcamentoRespondidoEm; }
    public List<ItemOrcamentoJpaEntity> getItensOrcamento() { return itensOrcamento; }
    public void setItensOrcamento(List<ItemOrcamentoJpaEntity> itensOrcamento) { this.itensOrcamento = itensOrcamento; }
}
