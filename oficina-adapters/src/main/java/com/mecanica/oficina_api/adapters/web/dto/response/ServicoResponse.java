package com.mecanica.oficina_api.adapters.web.dto.response;

import java.math.BigDecimal;

public class ServicoResponse {
    private final String id;
    private final String nome;
    private final String descricao;
    private final BigDecimal preco;
    private final int tempoEstimadoHoras;
    private final boolean ativo;

    public ServicoResponse(String id, String nome, String descricao, BigDecimal preco,
                           int tempoEstimadoHoras, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.tempoEstimadoHoras = tempoEstimadoHoras;
        this.ativo = ativo;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public BigDecimal getPreco() { return preco; }
    public int getTempoEstimadoHoras() { return tempoEstimadoHoras; }
    public boolean isAtivo() { return ativo; }
}
