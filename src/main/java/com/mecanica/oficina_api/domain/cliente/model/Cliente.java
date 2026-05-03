package com.mecanica.oficina_api.domain.cliente.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Cliente {
    private String id;
    private String nome;
    private Documento documento;
    private Email email;
    private Telefone telefone;
    private final List<String> veiculoIds;
    private LocalDateTime dataCadastro;
    private boolean ativo;


    protected Cliente() {
        this.veiculoIds = new ArrayList<>();
    }

    public static Cliente criar(String nome, Documento documento, Email email, Telefone telefone) {
        Cliente cliente = new Cliente();
        cliente.nome = Objects.requireNonNull(nome, "Nome é obrigatório");
        cliente.documento = Objects.requireNonNull(documento, "Documento é obrigatório");
        cliente.email = Objects.requireNonNull(email, "Email é obrigatório");
        cliente.telefone = Objects.requireNonNull(telefone, "Telefone é obrigatório");
        cliente.dataCadastro = LocalDateTime.now();
        cliente.ativo = true;
        return cliente;
    }

    public void adicionarVeiculo(String veiculoId) {
        Objects.requireNonNull(veiculoId, "ID do veículo é obrigatório");
        this.veiculoIds.add(veiculoId);
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public Documento getDocumento() { return documento; }
    public Email getEmail() { return email; }
    public Telefone getTelefone() { return telefone; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public  boolean getAtivo() { return ativo; }
    public List<String> getVeiculoIds() { return Collections.unmodifiableList(veiculoIds); }
}
