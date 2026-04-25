package com.mecanica.oficina_api.application.cliente;

import com.mecanica.oficina_api.domain.cliente.model.Cliente;
import com.mecanica.oficina_api.domain.cliente.model.Cpf;
import com.mecanica.oficina_api.domain.cliente.model.Email;
import com.mecanica.oficina_api.domain.cliente.model.Telefone;
import com.mecanica.oficina_api.infrastructure.persistence.ClienteJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.repository.ClienteSpringDataRepository;
import com.mecanica.oficina_api.interfaces.dto.CadastrarClienteRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ClienteService {

    private final ClienteSpringDataRepository repository;

    public ClienteService(ClienteSpringDataRepository repository) {
        this.repository = repository;
    }

    public void cadastrar(CadastrarClienteRequest request) {
        Cliente cliente = Cliente.criar(
            request.getNome(),
            new Cpf(request.getCpf()),
            new Email(request.getEmail()),
            new Telefone(request.getTelefone())
        );

        ClienteJpaEntity entity = new ClienteJpaEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setNome(cliente.getNome());
        entity.setCpf(cliente.getCpf().getValue());
        entity.setEmail(cliente.getEmail().getValue());
        entity.setTelefone(cliente.getTelefone().getValue());
        entity.setDataCadastro(cliente.getDataCadastro());

        repository.save(entity);
    }
}
