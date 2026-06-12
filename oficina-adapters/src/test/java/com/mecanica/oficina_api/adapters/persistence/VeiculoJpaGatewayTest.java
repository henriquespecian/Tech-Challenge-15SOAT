package com.mecanica.oficina_api.adapters.persistence;

import com.mecanica.oficina_api.adapters.persistence.repository.ClienteSpringDataRepository;
import com.mecanica.oficina_api.domain.veiculo.Veiculo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Import(VeiculoJpaGateway.class)
@Transactional
class VeiculoJpaGatewayTest {

    private static final String PLACA = "ABC1D23";

    @Autowired
    private VeiculoJpaGateway gateway;

    @Autowired
    private ClienteSpringDataRepository clienteRepo;

    private String clienteId;

    @BeforeEach
    void seedCliente() {
        ClienteJpaEntity cliente = new ClienteJpaEntity();
        cliente.setId("cliente-1");
        cliente.setNome("Maria");
        cliente.setDocumento("52998224725");
        cliente.setEmail("maria@email.com");
        cliente.setTelefone("11999999999");
        cliente.setDataCadastro(LocalDateTime.now());
        cliente.setAtivo(true);
        clienteId = clienteRepo.save(cliente).getId();
    }

    private Veiculo novoVeiculo(String placa) {
        return Veiculo.criar(clienteId, placa, "Ford", "Ka", 2020, "Prata");
    }

    @Test
    void deveCadastrarEBuscarVeiculoAtivo() {
        Veiculo salvo = gateway.cadastrar(novoVeiculo(PLACA));

        assertThat(salvo.getId()).isNotBlank();
        assertThat(salvo.getClienteId()).isEqualTo(clienteId);
        assertThat(salvo.getPlaca()).isEqualTo(PLACA);

        assertThat(gateway.placaExiste(PLACA)).isTrue();

        Optional<Veiculo> encontrado = gateway.buscar(salvo.getId());
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getMarca()).isEqualTo("Ford");
        assertThat(encontrado.get().getModelo()).isEqualTo("Ka");
    }

    @Test
    void deveRetornarVazio_quandoVeiculoNaoExiste() {
        assertThat(gateway.placaExiste(PLACA)).isFalse();
        assertThat(gateway.buscarPorPlaca(PLACA)).isFalse();
        assertThat(gateway.buscar("inexistente")).isEmpty();
    }

    @Test
    void deveBuscarVeiculosPorCliente() {
        gateway.cadastrar(novoVeiculo(PLACA));
        gateway.cadastrar(novoVeiculo("XYZ9K88"));

        List<Veiculo> veiculos = gateway.buscarVeiculoPorCliente(clienteId);

        assertThat(veiculos).hasSize(2);
        assertThat(veiculos).allMatch(v -> v.getClienteId().equals(clienteId));
    }

    @Test
    void deveAlterarVeiculo() {
        Veiculo salvo = gateway.cadastrar(novoVeiculo(PLACA));

        Veiculo atualizado = Veiculo.reconstituir(
                salvo.getId(), clienteId, "XYZ9K88", "Fiat", "Uno", 2021, "Branco", true);

        Veiculo resultado = gateway.alterar(salvo.getId(), atualizado);

        assertThat(resultado.getPlaca()).isEqualTo("XYZ9K88");
        assertThat(resultado.getMarca()).isEqualTo("Fiat");
        assertThat(resultado.getModelo()).isEqualTo("Uno");
        assertThat(resultado.getAno()).isEqualTo(2021);
        assertThat(resultado.getCor()).isEqualTo("Branco");
    }

    @Test
    void deveInativarVeiculo_comSoftDelete() {
        Veiculo salvo = gateway.cadastrar(novoVeiculo(PLACA));

        gateway.inativar(salvo.getId());

        assertThat(gateway.buscar(salvo.getId())).isEmpty();
        assertThat(gateway.placaExiste(PLACA)).isFalse();
    }

    @Test
    void deveBuscarPorPlaca_quandoVeiculoAtivoExiste() {
        gateway.cadastrar(novoVeiculo(PLACA));

        assertThat(gateway.buscarPorPlaca(PLACA)).isTrue();
    }
}
