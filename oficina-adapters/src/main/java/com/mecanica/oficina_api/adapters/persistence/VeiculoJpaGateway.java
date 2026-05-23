package com.mecanica.oficina_api.adapters.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.mecanica.oficina_api.adapters.persistence.repository.ClienteSpringDataRepository;
import com.mecanica.oficina_api.adapters.persistence.repository.VeiculoSpringDataRepository;
import com.mecanica.oficina_api.application.veiculo.gateway.VeiculoGateway;
import com.mecanica.oficina_api.domain.veiculo.Veiculo;

@Repository
public class VeiculoJpaGateway implements VeiculoGateway {

    private final VeiculoSpringDataRepository repo;
    private final ClienteSpringDataRepository clienteRepo;

    public VeiculoJpaGateway(VeiculoSpringDataRepository repo, ClienteSpringDataRepository clienteRepo) {
        this.repo = repo;
        this.clienteRepo = clienteRepo;
    }

    @Override
    public boolean placaExiste(String placa) {
        return repo.existsByPlacaAndAtivoTrue(placa);
    }

    @Override
    public Veiculo cadastrar(Veiculo veiculo) {
        return toDomain(repo.save(toEntity(veiculo)));
    }

    @Override
    public Optional<Veiculo> buscar(String id) {
        return repo.findByIdAndAtivoTrue(id).map(this::toDomain);
    }

    @Override
    public List<Veiculo> buscarVeiculoPorCliente(String clienteId) {
        return repo.findByCliente_IdAndAtivoTrue(clienteId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void inativar(String id) {
        repo.findByIdAndAtivoTrue(id).ifPresent(e -> {
            e.setAtivo(false);
            repo.save(e);
        });
    }

    @Override
    public boolean buscarPorPlaca(String placa) {
        return repo.existsByPlacaAndAtivoTrue(placa);
    }

    @Override
    public Veiculo alterar(String id, Veiculo veiculo) {
        VeiculoJpaEntity entity = repo.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + id));
        entity.setPlaca(veiculo.getPlaca());
        entity.setMarca(veiculo.getMarca());
        entity.setModelo(veiculo.getModelo());
        entity.setAno(veiculo.getAno());
        entity.setCor(veiculo.getCor());
        return toDomain(repo.save(entity));
    }

    private Veiculo toDomain(VeiculoJpaEntity e) {
        return Veiculo.reconstituir(
                e.getId(),
                e.getCliente().getId(),
                e.getPlaca(),
                e.getMarca(),
                e.getModelo(),
                e.getAno(),
                e.getCor(),
                e.getAtivo()
        );
    }

    private VeiculoJpaEntity toEntity(Veiculo v) {
        ClienteJpaEntity cliente = clienteRepo.findByIdAndAtivoTrue(v.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + v.getClienteId()));
        VeiculoJpaEntity e = new VeiculoJpaEntity();
        e.setPlaca(v.getPlaca());
        e.setMarca(v.getMarca());
        e.setModelo(v.getModelo());
        e.setAno(v.getAno());
        e.setCor(v.getCor());
        e.setAtivo(true);
        e.setCliente(cliente);
        return e;
    }
}
