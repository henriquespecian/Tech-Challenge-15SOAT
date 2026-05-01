package com.mecanica.oficina_api.domain.servico;

import java.math.BigDecimal;
import java.util.Objects;

public class Servico {

    private String id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private int tempoEstimadoHoras;
    private boolean ativo;

    protected Servico() {}

    public static Servico criar(String nome, String descricao, BigDecimal preco, int tempoEstimadoHoras) {
        Servico s = new Servico();
        s.nome = Objects.requireNonNull(nome, "Nome é obrigatório");
        s.descricao = descricao;
        s.setPreco(preco);
        s.setTempoEstimadoHoras(tempoEstimadoHoras);
        s.ativo = true;
        return s;
    }

    public static Servico reconstituir(String id, String nome, String descricao, BigDecimal preco,
                                       int tempoEstimadoHoras, boolean ativo) {
        Servico s = new Servico();
        s.id = id;
        s.nome = Objects.requireNonNull(nome, "Nome é obrigatório");
        s.descricao = descricao;
        s.setPreco(preco);
        s.setTempoEstimadoHoras(tempoEstimadoHoras);
        s.ativo = ativo;
        return s;
    }

    public void ativar() { this.ativo = true; }
    public void inativar() { this.ativo = false; }

    public void atualizar(String nome, String descricao, BigDecimal preco, int tempoEstimadoHoras) {
        this.nome = Objects.requireNonNull(nome, "Nome é obrigatório");
        this.descricao = descricao;
        setPreco(preco);
        setTempoEstimadoHoras(tempoEstimadoHoras);
    }

    private void setPreco(BigDecimal preco) {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Preço deve ser um valor não negativo");
        this.preco = preco;
    }

    private void setTempoEstimadoHoras(int tempoEstimadoHoras) {
        if (tempoEstimadoHoras < 0)
            throw new IllegalArgumentException("Tempo estimado não pode ser negativo");
        this.tempoEstimadoHoras = tempoEstimadoHoras;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public BigDecimal getPreco() { return preco; }
    public int getTempoEstimadoHoras() { return tempoEstimadoHoras; }
    public boolean isAtivo() { return ativo; }
}
