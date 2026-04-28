package com.mecanica.oficina_api.application.veiculo;

import com.mecanica.oficina_api.domain.veiculo.Veiculo;
import com.mecanica.oficina_api.infrastructure.persistence.ClienteJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.VeiculoJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.repository.ClienteSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.VeiculoSpringDataRepository;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarVeiculoRequest;
import com.mecanica.oficina_api.interfaces.dto.response.VeiculoResponse;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VeiculoService {

    private final VeiculoSpringDataRepository veiculoRepository;
    private final ClienteSpringDataRepository clienteRepository;

    public VeiculoService(VeiculoSpringDataRepository veiculoRepository,
                          ClienteSpringDataRepository clienteRepository) {
        this.veiculoRepository = veiculoRepository;
        this.clienteRepository = clienteRepository;
    }

    public void cadastrar(CadastrarVeiculoRequest request) {
        ClienteJpaEntity clienteEntity = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + request.getClienteId()));

        if (veiculoRepository.existsByPlaca(request.getPlaca())) {
            throw new IllegalArgumentException("Já existe um veículo com a placa: " + request.getPlaca());
        }

        Veiculo veiculo = Veiculo.criar(
                request.getClienteId(),
                request.getPlaca(),
                request.getMarca(),
                request.getModelo(),
                request.getAno(),
                request.getCor()
        );

        VeiculoJpaEntity entity = new VeiculoJpaEntity();
        entity.setPlaca(veiculo.getPlaca());
        entity.setMarca(veiculo.getMarca());
        entity.setModelo(veiculo.getModelo());
        entity.setAno(veiculo.getAno());
        entity.setCor(veiculo.getCor());
        entity.setCliente(clienteEntity);

        veiculoRepository.save(entity);
    }

    public VeiculoResponse buscarPorId(String id) {
        VeiculoJpaEntity entity = veiculoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + id));

        return new VeiculoResponse(
                entity.getId(),
                entity.getCliente().getId(),
                entity.getPlaca(),
                entity.getMarca(),
                entity.getModelo(),
                entity.getAno(),
                entity.getCor()
        );
    }

    public List<VeiculoResponse> listarPorCliente(String clienteId) {
        return veiculoRepository.findByCliente_Id(clienteId).stream()
                .map(e -> new VeiculoResponse(
                        e.getId(),
                        e.getCliente().getId(),
                        e.getPlaca(),
                        e.getMarca(),
                        e.getModelo(),
                        e.getAno(),
                        e.getCor()
                ))
                .toList();
    }
}
