package com.mecanica.oficina_api.domain.cliente.model;

import java.util.ArrayList;
import java.util.List;

public class Cliente {
    private String nome;
    private Cpf cpf;
    private Email emailString;
    private Telefone telefone;
    private final List<Veiculo> veiculos;

    protected Cliente() {
        this.veiculos = new ArrayList<>();
    }

    public static Cliente criar(String nome, String cpf, String email, String telefone) {
        Cliente cliente = new Cliente();
    
        return cliente;
    }
}
