package com.mecanica.oficina_api.application.cliente;

import com.mecanica.oficina_api.domain.cliente.model.Cliente;
import com.mecanica.oficina_api.domain.cliente.model.Cpf;
import com.mecanica.oficina_api.domain.cliente.model.Email;
import com.mecanica.oficina_api.domain.cliente.model.Telefone;
import com.mecanica.oficina_api.infrastructure.persistence.ClienteJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.repository.ClienteSpringDataRepository;
import com.mecanica.oficina_api.interfaces.dto.request.AlterarClienteRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarClienteRequest;
import com.mecanica.oficina_api.interfaces.dto.response.ConsultarClienteResponse;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ClienteService {

    private final ClienteSpringDataRepository repository;

    public ClienteService(ClienteSpringDataRepository repository) {
        this.repository = repository;
    }

    public void cadastrar(CadastrarClienteRequest request) {
        if (repository.existsByCpf(request.getCpf())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        }

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
        entity.setAtivo(true);

        repository.save(entity);
    }

    public ConsultarClienteResponse consultar(String cpf) {

        Cpf cpf_cliente = new Cpf(cpf);

        ClienteJpaEntity entity = repository.findByCpfAndAtivoTrue(cpf_cliente.getValue())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CPF inexistente"));

        var response = new ConsultarClienteResponse(entity.getId(), entity.getNome(), entity.getCpf(), entity.getEmail(), entity.getTelefone());

        return response;
    }

    public void alterar(String cpf, AlterarClienteRequest request) {
        Cpf cpf_cliente = new Cpf(cpf);

        ClienteJpaEntity entity = repository.findByCpfAndAtivoTrue(cpf_cliente.getValue())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CPF inexistente"));

        Cliente cliente = Cliente.criar(
            request.getNome(),
            new Cpf(cpf_cliente.getValue()),
            new Email(request.getEmail()),
            new Telefone(request.getTelefone())
        );

        entity.setNome(cliente.getNome());
        entity.setEmail(cliente.getEmail().getValue());
        entity.setTelefone(cliente.getTelefone().getValue());

        repository.save(entity);
    }

    public void deletar(String cpf) {
        Cpf cpf_cliente = new Cpf(cpf);

        Optional<ClienteJpaEntity> entity = repository.findByCpfAndAtivoTrue(cpf_cliente.getValue());

        if(entity.isPresent()) {
            entity.get().setAtivo(false);
            repository.save(entity.get());
        }
    }
}
