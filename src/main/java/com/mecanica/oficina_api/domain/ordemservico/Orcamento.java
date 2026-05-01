package com.mecanica.oficina_api.domain.ordemservico;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Orcamento {

    private final List<ItemOrcamento> itens;
    private final BigDecimal valorTotal;
    private final OrcamentoStatus status;
    private final String observacoes;
    private final LocalDateTime respondidoEm;

    public Orcamento(List<ItemOrcamento> itens, String observacoes) {
        Objects.requireNonNull(itens, "Itens são obrigatórios");
        if (itens.isEmpty()) throw new IllegalArgumentException("Orçamento deve ter ao menos um item");
        this.itens = List.copyOf(itens);
        this.valorTotal = calcularTotal(itens);
        this.status = OrcamentoStatus.PENDENTE;
        this.observacoes = observacoes;
        this.respondidoEm = null;
    }

    private Orcamento(List<ItemOrcamento> itens, OrcamentoStatus status, String observacoes, LocalDateTime respondidoEm) {
        this.itens = List.copyOf(itens);
        this.valorTotal = calcularTotal(itens);
        this.status = status;
        this.observacoes = observacoes;
        this.respondidoEm = respondidoEm;
    }

    public static Orcamento reconstituir(List<ItemOrcamento> itens, OrcamentoStatus status,
                                         String observacoes, LocalDateTime respondidoEm) {
        return new Orcamento(itens, status, observacoes, respondidoEm);
    }

    public Orcamento enviar() {
        if (status != OrcamentoStatus.PENDENTE)
            throw new IllegalStateException("Orçamento deve estar PENDENTE para ser enviado");
        return new Orcamento(itens, OrcamentoStatus.ENVIADO, observacoes, null);
    }

    public Orcamento aguardar() {
        if (status != OrcamentoStatus.ENVIADO)
            throw new IllegalStateException("Orçamento deve estar ENVIADO para entrar em aguardo");
        return new Orcamento(itens, OrcamentoStatus.AGUARDANDO, observacoes, null);
    }

    public Orcamento aprovar() {
        if (status != OrcamentoStatus.ENVIADO && status != OrcamentoStatus.AGUARDANDO)
            throw new IllegalStateException("Orçamento deve estar ENVIADO ou AGUARDANDO para ser aprovado");
        return new Orcamento(itens, OrcamentoStatus.APROVADO, observacoes, LocalDateTime.now());
    }

    public Orcamento negar() {
        if (status != OrcamentoStatus.ENVIADO && status != OrcamentoStatus.AGUARDANDO)
            throw new IllegalStateException("Orçamento deve estar ENVIADO ou AGUARDANDO para ser negado");
        return new Orcamento(itens, OrcamentoStatus.NEGADO, observacoes, LocalDateTime.now());
    }

    private static BigDecimal calcularTotal(List<ItemOrcamento> itens) {
        return itens.stream().map(ItemOrcamento::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<ItemOrcamento> getItens() { return itens; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public OrcamentoStatus getStatus() { return status; }
    public String getObservacoes() { return observacoes; }
    public LocalDateTime getRespondidoEm() { return respondidoEm; }
}
