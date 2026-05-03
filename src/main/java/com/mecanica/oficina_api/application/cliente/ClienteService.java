package com.mecanica.oficina_api.application.cliente;

import com.mecanica.oficina_api.domain.cliente.model.Cliente;
import com.mecanica.oficina_api.domain.cliente.model.Documento;
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

    public ConsultarClienteResponse cadastrar(CadastrarClienteRequest request) {
        Documento doc = Documento.parse(request.getDocumento());

        if (repository.existsByDocumento(doc.getValue())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Documento já cadastrado");
        }

        Cliente cliente = Cliente.criar(
            request.getNome(),
            doc,
            new Email(request.getEmail()),
            new Telefone(request.getTelefone())
        );

        ClienteJpaEntity entity = new ClienteJpaEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setNome(cliente.getNome());
        entity.setDocumento(cliente.getDocumento().getValue());
        entity.setEmail(cliente.getEmail().getValue());
        entity.setTelefone(cliente.getTelefone().getValue());
        entity.setDataCadastro(cliente.getDataCadastro());
        entity.setAtivo(true);

        repository.save(entity);
        return new ConsultarClienteResponse(
            entity.getId(), entity.getNome(), entity.getDocumento(), entity.getEmail(), entity.getTelefone());
    }

    public ConsultarClienteResponse consultar(String documento) {
        Documento doc = Documento.parse(documento);

        ClienteJpaEntity entity = repository.findByDocumentoAndAtivoTrue(doc.getValue())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        return new ConsultarClienteResponse(
            entity.getId(), entity.getNome(), entity.getDocumento(), entity.getEmail(), entity.getTelefone());
    }

    public void alterar(String documento, AlterarClienteRequest request) {
        Documento doc = Documento.parse(documento);

        ClienteJpaEntity entity = repository.findByDocumentoAndAtivoTrue(doc.getValue())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        Cliente cliente = Cliente.criar(
            request.getNome(),
            doc,
            new Email(request.getEmail()),
            new Telefone(request.getTelefone())
        );

        entity.setNome(cliente.getNome());
        entity.setEmail(cliente.getEmail().getValue());
        entity.setTelefone(cliente.getTelefone().getValue());

        repository.save(entity);
    }

    public void deletar(String documento) {
        Documento doc = Documento.parse(documento);

        Optional<ClienteJpaEntity> entity = repository.findByDocumentoAndAtivoTrue(doc.getValue());

        if (entity.isPresent()) {
            entity.get().setAtivo(false);
            repository.save(entity.get());
        }
    }
}
