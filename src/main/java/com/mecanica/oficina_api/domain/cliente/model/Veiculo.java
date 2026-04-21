package com.mecanica.oficina_api.domain.cliente.model;

import java.util.Objects;

public class Veiculo {
    private String placa;
    private String marca;
    private String modelo;
    private int ano;
    private String cor;

    protected Veiculo() {
        // Construtor protegido para uso do JPA
    }

    public Veiculo(String placa, String marca, String modelo, int ano, String cor) {
        setPlaca(placa);
        this.marca = Objects.requireNonNull(marca, "Marca é obrigatória");
        this.modelo = Objects.requireNonNull(modelo, "Modelo é obrigatório");
        this.ano = ano;
        this.cor = cor;
    }

    private void setPlaca(String placa) {
        if(placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("Placa é obrigatória");
        }
        this.placa = placa.toUpperCase().trim();
    }

    // Getters
    public String getPlaca() { return placa; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public int getAno() { return ano; }
    public String getCor() { return cor; }

}
