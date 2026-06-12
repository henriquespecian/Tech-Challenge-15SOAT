package com.mecanica.oficina_api.adapters.persistence;

import com.mecanica.oficina_api.domain.servico.Servico;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Import(ServicoJpaGateway.class)
@Transactional
class ServicoJpaGatewayTest {

    @Autowired
    private ServicoJpaGateway gateway;

    private Servico novoServico(String nome) {
        return Servico.criar(nome, "Descrição de " + nome,
                new BigDecimal("150.00"), Duration.ofHours(2));
    }

    @Test
    void deveCadastrarEBuscarServicoPorId() {
        Servico salvo = gateway.cadastrar(novoServico("Troca de óleo"));

        assertThat(salvo.getId()).isNotBlank();

        Optional<Servico> encontrado = gateway.buscar(salvo.getId());
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Troca de óleo");
        assertThat(encontrado.get().getPreco()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(encontrado.get().getTempoEstimadoHoras()).isEqualTo(Duration.ofHours(2));
        assertThat(encontrado.get().isAtivo()).isTrue();
    }

    @Test
    void deveRetornarVazio_quandoServicoNaoExiste() {
        assertThat(gateway.buscar("inexistente")).isEmpty();
    }

    @Test
    void deveListarApenasServicosAtivos() {
        Servico ativo = gateway.cadastrar(novoServico("Ativo"));
        Servico paraInativar = gateway.cadastrar(novoServico("Inativo"));
        gateway.inativar(paraInativar.getId());

        List<Servico> ativos = gateway.listar();

        assertThat(ativos).hasSize(1);
        assertThat(ativos.get(0).getId()).isEqualTo(ativo.getId());
        assertThat(ativos.get(0).getNome()).isEqualTo("Ativo");
    }

    @Test
    void deveAlterarServico() {
        Servico salvo = gateway.cadastrar(novoServico("Troca de óleo"));

        Servico alterado = Servico.reconstituir(
                salvo.getId(), "Troca de óleo premium", "Óleo sintético",
                new BigDecimal("250.00"), Duration.ofHours(3), true);
        gateway.alterar(salvo.getId(), alterado);

        Optional<Servico> encontrado = gateway.buscar(salvo.getId());
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Troca de óleo premium");
        assertThat(encontrado.get().getDescricao()).isEqualTo("Óleo sintético");
        assertThat(encontrado.get().getPreco()).isEqualByComparingTo(new BigDecimal("250.00"));
        assertThat(encontrado.get().getTempoEstimadoHoras()).isEqualTo(Duration.ofHours(3));
    }

    @Test
    void deveAtivarServicoPreviamenteInativado() {
        Servico salvo = gateway.cadastrar(novoServico("Troca de óleo"));
        gateway.inativar(salvo.getId());

        Servico ativado = gateway.ativar(salvo.getId());

        assertThat(ativado.isAtivo()).isTrue();
        assertThat(gateway.buscar(salvo.getId())).get()
                .extracting(Servico::isAtivo).isEqualTo(true);
    }

    @Test
    void deveLancarExcecaoAoAtivar_quandoServicoNaoExiste() {
        // O gateway é um @Repository: o Spring traduz a IllegalArgumentException lançada
        // internamente em InvalidDataAccessApiUsageException, preservando a causa raiz.
        assertThatThrownBy(() -> gateway.ativar("inexistente"))
                .hasRootCauseInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Serviço não encontrado");
    }

    @Test
    void deveInativarServico() {
        Servico salvo = gateway.cadastrar(novoServico("Troca de óleo"));

        gateway.inativar(salvo.getId());

        Optional<Servico> encontrado = gateway.buscar(salvo.getId());
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().isAtivo()).isFalse();
        assertThat(gateway.listar()).isEmpty();
    }
}
