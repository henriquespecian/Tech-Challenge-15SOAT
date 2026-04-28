package com.mecanica.oficina_api.domain.cliente.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Cliente {
    private String id;
    private String nome;
    private Cpf cpf;
    private Email email;
    private Telefone telefone;
    private final List<String> veiculoIds;
    private LocalDateTime dataCadastro;

    protected Cliente() {
        this.veiculoIds = new ArrayList<>();
    }

    public static Cliente criar(String nome, Cpf cpf, Email email, Telefone telefone) {
        Cliente cliente = new Cliente();
        cliente.nome = Objects.requireNonNull(nome, "Nome é obrigatório");
        cliente.cpf = Objects.requireNonNull(cpf, "CPF é obrigatório");
        cliente.email = Objects.requireNonNull(email, "Email é obrigatório");
        cliente.telefone = Objects.requireNonNull(telefone, "Telefone é obrigatório");
        cliente.dataCadastro = LocalDateTime.now();
        return cliente;
    }

    public void adicionarVeiculo(String veiculoId) {
        Objects.requireNonNull(veiculoId, "ID do veículo é obrigatório");
        this.veiculoIds.add(veiculoId);
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public Cpf getCpf() { return cpf; }
    public Email getEmail() { return email; }
    public Telefone getTelefone() { return telefone; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public List<String> getVeiculoIds() { return Collections.unmodifiableList(veiculoIds); }
}
