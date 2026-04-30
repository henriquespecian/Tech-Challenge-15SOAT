package com.mecanica.oficina_api.application.veiculo;

import com.mecanica.oficina_api.domain.veiculo.Veiculo;
import com.mecanica.oficina_api.infrastructure.persistence.ClienteJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.VeiculoJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.repository.ClienteSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.VeiculoSpringDataRepository;
import com.mecanica.oficina_api.interfaces.dto.request.AlterarVeiculoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarVeiculoRequest;
import com.mecanica.oficina_api.interfaces.dto.response.VeiculoResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado: " + request.getClienteId()));

        if (veiculoRepository.existsByPlacaAndAtivoTrue(request.getPlaca())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um veículo com a placa: " + request.getPlaca());
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
        entity.setAtivo(true);

        veiculoRepository.save(entity);
    }

    public VeiculoResponse buscarPorId(String id) {
        VeiculoJpaEntity entity = veiculoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veículo não encontrado: " + id));

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
        return veiculoRepository.findByCliente_IdAndAtivoTrue(clienteId).stream()
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

    public VeiculoResponse alterar(String id, AlterarVeiculoRequest request) {
        VeiculoJpaEntity entity = veiculoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veículo não encontrado: " + id));

        if (!entity.getPlaca().equals(request.getPlaca()) &&
                veiculoRepository.existsByPlacaAndAtivoTrue(request.getPlaca())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um veículo com a placa: " + request.getPlaca());
        }

        entity.setPlaca(request.getPlaca());
        entity.setMarca(request.getMarca());
        entity.setModelo(request.getModelo());
        entity.setAno(request.getAno());
        entity.setCor(request.getCor());

        VeiculoJpaEntity saved = veiculoRepository.save(entity);

        return new VeiculoResponse(
                saved.getId(),
                saved.getCliente().getId(),
                saved.getPlaca(),
                saved.getMarca(),
                saved.getModelo(),
                saved.getAno(),
                saved.getCor()
        );
    }

    public void deletar(String id) {

        Optional<VeiculoJpaEntity> entity = veiculoRepository.findByIdAndAtivoTrue(id);
        
        if(entity.isPresent()) {
            entity.get().setAtivo(false);
            veiculoRepository.save(entity.get());
        }
    }
}
