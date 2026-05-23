package com.mecanica.oficina_api.domain.cliente;

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
    private LocalDateTime dataAtualizacao;
    private boolean ativo;


    protected Cliente() {
        this.veiculoIds = new ArrayList<>();
    }

    public static Cliente reconstituir(
    String id, String nome, Documento doc, Email email, Telefone telefone, LocalDateTime dataCadastro, LocalDateTime dataAtualizacao) {
        Cliente c = new Cliente();
        c.id = id;
        c.nome = Objects.requireNonNull(nome, "Nome é obrigatório");
        c.documento = Objects.requireNonNull(doc, "Documento é obrigatório");
        c.email = Objects.requireNonNull(email, "Email é obrigatório");
        c.telefone = Objects.requireNonNull(telefone, "Telefone é obrigatório");
        c.dataCadastro = dataCadastro;
        c.dataAtualizacao = dataAtualizacao;
        c.ativo = true;
        return c;
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

    public void alterar(String nome, Documento documento, Email email, Telefone telefone){
        this.nome = Objects.requireNonNull(nome, "Nome é obrigatório");
        this.documento = Objects.requireNonNull(documento, "Documento é obrigatório");
        this.email = Objects.requireNonNull(email, "Email é obrigatório");
        this.telefone = Objects.requireNonNull(telefone, "Telefone é obrigatório");
        this.dataAtualizacao = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public Documento getDocumento() { return documento; }
    public Email getEmail() { return email; }
    public Telefone getTelefone() { return telefone; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public  boolean getAtivo() { return ativo; }
    public List<String> getVeiculoIds() { return Collections.unmodifiableList(veiculoIds); }
}
