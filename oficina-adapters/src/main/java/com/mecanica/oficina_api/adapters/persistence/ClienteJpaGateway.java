package com.mecanica.oficina_api.adapters.persistence;

import com.mecanica.oficina_api.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_api.domain.cliente.Cliente;
import com.mecanica.oficina_api.domain.cliente.Documento;
import com.mecanica.oficina_api.domain.cliente.Email;
import com.mecanica.oficina_api.domain.cliente.Telefone;
import com.mecanica.oficina_api.adapters.persistence.repository.ClienteSpringDataRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ClienteJpaGateway implements ClienteGateway {

    private final ClienteSpringDataRepository repo;

    public ClienteJpaGateway(ClienteSpringDataRepository repo) {
        this.repo = repo;
    }

    @Override
    public boolean existsByDocumento(String documento) {
        return repo.existsByDocumento(documento);
    }

    @Override
    public Optional<Cliente> findByDocumentoAtivo(String documento) {
        return repo.findByDocumentoAndAtivoTrue(documento)
                .map(this::toDomain);
    }

    @Override
    public List<Cliente> findAllAtivos() {
        return repo.findAllByAtivoTrue().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Cliente save(Cliente cliente) {
        ClienteJpaEntity entity = toEntity(cliente);
        ClienteJpaEntity saved = repo.save(entity);
        return toDomain(saved);
    }

    @Override
    public void softDelete(String documento) {
        repo.findByDocumentoAndAtivoTrue(documento).ifPresent(e -> {
            e.setAtivo(false);
            repo.save(e);
        });
    }

    @Override
    public Optional<Cliente> findByIdAtivo(String id) {
        return repo.findByIdAndAtivoTrue(id).map(this::toDomain);
    }

    // --- mapeamento privado ---

    private Cliente toDomain(ClienteJpaEntity e) {
        return Cliente.reconstituir(
            e.getId(),
            e.getNome(),
            Documento.parse(e.getDocumento()),
            new Email(e.getEmail()),
            new Telefone(e.getTelefone()),
            e.getDataCadastro(),
            e.getDataAtualizacao()
        );
    }

    private ClienteJpaEntity toEntity(Cliente c) {
        ClienteJpaEntity e = new ClienteJpaEntity();
        e.setId(c.getId() != null ? c.getId() : UUID.randomUUID().toString());
        e.setNome(c.getNome());
        e.setDocumento(c.getDocumento().getValue());
        e.setEmail(c.getEmail().getValue());
        e.setTelefone(c.getTelefone().getValue());
        e.setDataCadastro(c.getDataCadastro());
        e.setDataAtualizacao(c.getDataAtualizacao());
        e.setAtivo(true);
        return e;
    }
}
