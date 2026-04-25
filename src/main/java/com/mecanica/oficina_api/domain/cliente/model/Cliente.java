package com.mecanica.oficina_api.domain.cliente.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cliente {
    private String nome;
    private Cpf cpf;
    private Email email;
    private Telefone telefone;
    private final List<Veiculo> veiculos;
    private LocalDateTime dataCadastro;

    protected Cliente() {
        this.veiculos = new ArrayList<>();
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

    public String getNome() { return nome; }
    public Cpf getCpf() { return cpf; }
    public Email getEmail() { return email; }
    public Telefone getTelefone() { return telefone; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
}
