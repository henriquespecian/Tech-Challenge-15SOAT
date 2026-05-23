package com.mecanica.oficina_api.domain.servico;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Objects;

import lombok.Getter;

@Getter
public class Servico {
    private String id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Duration tempoEstimadoHoras;
    private boolean ativo;

    protected Servico() {}

    public static Servico criar(String nome, String descricao, BigDecimal preco, Duration tempoEstimadoHoras) {
        
        Servico servico = new Servico();
        validarParametros(nome, descricao, preco, tempoEstimadoHoras);

        servico.id = java.util.UUID.randomUUID().toString();
        servico.nome = nome;
        servico.descricao = descricao;
        servico.preco = preco;
        servico.tempoEstimadoHoras = tempoEstimadoHoras;
        servico.ativo = true;

        return servico;
    }

    public static void validarParametros(String nome, String descricao, BigDecimal preco, Duration tempoEstimadoHoras) {
        if(Objects.isNull(nome) || nome.isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if(Objects.isNull(descricao) || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }
        if(Objects.isNull(preco) || preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço deve ser um número positivo");
        }
        if(Objects.isNull(tempoEstimadoHoras) || tempoEstimadoHoras.isNegative()) {
            throw new IllegalArgumentException("Tempo Estimado deve ser um valor positivo");
        }
    }

    public static Servico reconstituir(String id, String nome, String descricao,
                                       BigDecimal preco, Duration tempoEstimadoHoras, boolean ativo) {
        Servico servico = new Servico();
        servico.id = id;
        servico.nome = nome;
        servico.descricao = descricao;
        servico.preco = preco;
        servico.tempoEstimadoHoras = tempoEstimadoHoras;
        servico.ativo = ativo;
        return servico;
    }

    public void atualizar(String nome, String descricao, BigDecimal preco, Duration tempoEstimadoHoras) {
        validarParametros(nome, descricao, preco, tempoEstimadoHoras);
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.tempoEstimadoHoras = tempoEstimadoHoras;
    }

    public void ativar() {
        this.ativo = true;
    }
    public void inativar() {
        this.ativo = false;
    }

}
