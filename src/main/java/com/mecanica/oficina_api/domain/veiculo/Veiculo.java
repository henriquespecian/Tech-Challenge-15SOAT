package com.mecanica.oficina_api.domain.veiculo;

import java.time.Year;
import java.util.Objects;

public class Veiculo {

    private String id;
    private String clienteId;
    private String placa;
    private String marca;
    private String modelo;
    private int ano;
    private String cor;
    private boolean ativo;

    protected Veiculo() {}

    public static Veiculo criar(String clienteId, String placa, String marca, String modelo, int ano, String cor) {
        Veiculo veiculo = new Veiculo();
        veiculo.clienteId = Objects.requireNonNull(clienteId, "Cliente é obrigatório");
        veiculo.marca = Objects.requireNonNull(marca, "Marca é obrigatória");
        veiculo.modelo = Objects.requireNonNull(modelo, "Modelo é obrigatório");
        veiculo.setAno(ano);
        veiculo.cor = cor;
        veiculo.setPlaca(placa);
        veiculo.ativo = true;
        return veiculo;
    }

    private void setAno(int ano) {
        int anoAtual = Year.now().getValue();
        if (ano < 1886 || ano > anoAtual + 1) {
            throw new IllegalArgumentException("Ano inválido");
        }
        this.ano = ano;
    }

    private void setPlaca(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("Placa é obrigatória");
        }
        this.placa = placa.toUpperCase().trim();
    }

    public String getId() { return id; }
    public String getClienteId() { return clienteId; }
    public String getPlaca() { return placa; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public int getAno() { return ano; }
    public String getCor() { return cor; }
    public boolean isAtivo() { return ativo; }
}