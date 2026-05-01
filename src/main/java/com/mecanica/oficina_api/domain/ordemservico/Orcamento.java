package com.mecanica.oficina_api.domain.ordemservico;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class Orcamento {

    private final List<ItemOrcamento> itens;
    private final BigDecimal valorTotal;
    private OrcamentoStatus status;
    private final String observacoes;

    public Orcamento(List<ItemOrcamento> itens, String observacoes) {
        Objects.requireNonNull(itens, "Itens são obrigatórios");
        if (itens.isEmpty()) throw new IllegalArgumentException("Orçamento deve ter ao menos um item");
        this.itens = List.copyOf(itens);
        this.valorTotal = itens.stream().map(ItemOrcamento::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        this.status = OrcamentoStatus.PENDENTE;
        this.observacoes = observacoes;
    }

    public static Orcamento reconstituir(List<ItemOrcamento> itens, OrcamentoStatus status, String observacoes) {
        Orcamento o = new Orcamento(itens, observacoes);
        o.status = status;
        return o;
    }

    public void enviar() {
        if (status != OrcamentoStatus.PENDENTE)
            throw new IllegalStateException("Orçamento deve estar PENDENTE para ser enviado");
        status = OrcamentoStatus.ENVIADO;
    }

    public void negociar() {
        if (status != OrcamentoStatus.ENVIADO)
            throw new IllegalStateException("Orçamento deve estar ENVIADO para entrar em negociação");
        status = OrcamentoStatus.EM_NEGOCIACAO;
    }

    public void aprovar() {
        if (status != OrcamentoStatus.ENVIADO && status != OrcamentoStatus.EM_NEGOCIACAO)
            throw new IllegalStateException("Orçamento deve estar ENVIADO ou EM_NEGOCIACAO para ser aprovado");
        status = OrcamentoStatus.APROVADO;
    }

    public void negar() {
        if (status != OrcamentoStatus.ENVIADO && status != OrcamentoStatus.EM_NEGOCIACAO)
            throw new IllegalStateException("Orçamento deve estar ENVIADO ou EM_NEGOCIACAO para ser negado");
        status = OrcamentoStatus.NEGADO;
    }

    public List<ItemOrcamento> getItens() { return itens; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public OrcamentoStatus getStatus() { return status; }
    public String getObservacoes() { return observacoes; }
}
