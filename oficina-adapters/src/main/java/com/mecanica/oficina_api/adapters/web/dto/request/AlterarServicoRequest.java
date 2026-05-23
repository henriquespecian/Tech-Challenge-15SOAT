package com.mecanica.oficina_api.adapters.web.dto.request;

import java.math.BigDecimal;

public class AlterarServicoRequest {
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private int tempoEstimadoHoras;

    public AlterarServicoRequest() {}

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public int getTempoEstimadoHoras() { return tempoEstimadoHoras; }
    public void setTempoEstimadoHoras(int tempoEstimadoHoras) { this.tempoEstimadoHoras = tempoEstimadoHoras; }
}
