package com.mecanica.oficina_api.interfaces.dto.response;

public class VeiculoResponse {

    private String id;
    private String clienteId;
    private String placa;
    private String marca;
    private String modelo;
    private int ano;
    private String cor;

    public VeiculoResponse() {}

    public VeiculoResponse(String id, String clienteId, String placa, String marca, String modelo, int ano, String cor) {
        this.id = id;
        this.clienteId = clienteId;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.cor = cor;
    }

    public String getId() { return id; }
    public String getClienteId() { return clienteId; }
    public String getPlaca() { return placa; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public int getAno() { return ano; }
    public String getCor() { return cor; }
}
