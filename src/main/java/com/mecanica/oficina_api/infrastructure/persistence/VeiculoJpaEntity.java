package com.mecanica.oficina_api.infrastructure.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "veiculos")
public class VeiculoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String placa;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String modelo;

    @Column(nullable = false)
    private int ano;

    private String cor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteJpaEntity cliente;

    public VeiculoJpaEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }
    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }
    public ClienteJpaEntity getCliente() { return cliente; }
    public void setCliente(ClienteJpaEntity cliente) { this.cliente = cliente; }
}
