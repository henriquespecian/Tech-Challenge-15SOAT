package com.mecanica.oficina_api.domain.servico;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;

import static org.assertj.core.api.Assertions.*;

class ServicoTest {

    // --- criar ---

    @Test
    void deveCriarServicoComDadosValidos() {
        Servico servico = Servico.criar("Troca de óleo", "Troca completa do óleo", BigDecimal.valueOf(150), Duration.ofHours(1));

        assertThat(servico.getNome()).isEqualTo("Troca de óleo");
        assertThat(servico.getDescricao()).isEqualTo("Troca completa do óleo");
        assertThat(servico.getPreco()).isEqualByComparingTo(BigDecimal.valueOf(150));
        assertThat(servico.getTempoEstimadoHoras()).isEqualTo(Duration.ofHours(1));
        assertThat(servico.isAtivo()).isTrue();
    }

    @Test
    void deveGerarIdAoCriarServico() {
        Servico servico = Servico.criar("Alinhamento", "Alinhamento e balanceamento", BigDecimal.valueOf(80), Duration.ofHours(2));

        assertThat(servico.getId()).isNotNull().isNotBlank();
    }

    @Test
    void deveCriarServicosComIdsDistintos() {
        Servico s1 = Servico.criar("Serviço A", "Desc A", BigDecimal.valueOf(100), Duration.ofHours(1));
        Servico s2 = Servico.criar("Serviço B", "Desc B", BigDecimal.valueOf(200), Duration.ofHours(2));

        assertThat(s1.getId()).isNotEqualTo(s2.getId());
    }

    @Test
    void deveAceitarPrecoZero() {
        Servico servico = Servico.criar("Revisão gratuita", "Revisão de cortesia", BigDecimal.ZERO, Duration.ofHours(1));

        assertThat(servico.getPreco()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void deveLancarExcecaoQuandoNomeNulo() {
        assertThatThrownBy(() -> Servico.criar(null, "Desc", BigDecimal.valueOf(100), Duration.ofHours(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nome é obrigatório");
    }

    @Test
    void deveLancarExcecaoQuandoNomeVazio() {
        assertThatThrownBy(() -> Servico.criar("   ", "Desc", BigDecimal.valueOf(100), Duration.ofHours(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nome é obrigatório");
    }

    @Test
    void deveLancarExcecaoQuandoDescricaoNula() {
        assertThatThrownBy(() -> Servico.criar("Nome", null, BigDecimal.valueOf(100), Duration.ofHours(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Descrição é obrigatória");
    }

    @Test
    void deveLancarExcecaoQuandoDescricaoVazia() {
        assertThatThrownBy(() -> Servico.criar("Nome", "   ", BigDecimal.valueOf(100), Duration.ofHours(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Descrição é obrigatória");
    }

    @Test
    void deveLancarExcecaoQuandoPrecoNulo() {
        assertThatThrownBy(() -> Servico.criar("Nome", "Desc", null, Duration.ofHours(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Preço deve ser um número positivo");
    }

    @Test
    void deveLancarExcecaoQuandoPrecoNegativo() {
        assertThatThrownBy(() -> Servico.criar("Nome", "Desc", BigDecimal.valueOf(-1), Duration.ofHours(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Preço deve ser um número positivo");
    }

    @Test
    void deveLancarExcecaoQuandoTempoNulo() {
        assertThatThrownBy(() -> Servico.criar("Nome", "Desc", BigDecimal.valueOf(100), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Tempo Estimado deve ser um valor positivo");
    }

    @Test
    void deveLancarExcecaoQuandoTempoNegativo() {
        assertThatThrownBy(() -> Servico.criar("Nome", "Desc", BigDecimal.valueOf(100), Duration.ofHours(-1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Tempo Estimado deve ser um valor positivo");
    }

    // --- reconstituir ---

    @Test
    void deveReconstituirServicoComTodosOsCampos() {
        Servico servico = Servico.reconstituir("id-fixo", "Alinhamento", "Desc",
                BigDecimal.valueOf(80), Duration.ofHours(2), false);

        assertThat(servico.getId()).isEqualTo("id-fixo");
        assertThat(servico.getNome()).isEqualTo("Alinhamento");
        assertThat(servico.getDescricao()).isEqualTo("Desc");
        assertThat(servico.getPreco()).isEqualByComparingTo(BigDecimal.valueOf(80));
        assertThat(servico.getTempoEstimadoHoras()).isEqualTo(Duration.ofHours(2));
        assertThat(servico.isAtivo()).isFalse();
    }

    // --- atualizar ---

    @Test
    void deveAtualizarServicoComDadosValidos() {
        Servico servico = Servico.criar("Nome original", "Desc original", BigDecimal.valueOf(100), Duration.ofHours(1));

        servico.atualizar("Nome atualizado", "Desc atualizada", BigDecimal.valueOf(200), Duration.ofHours(3));

        assertThat(servico.getNome()).isEqualTo("Nome atualizado");
        assertThat(servico.getDescricao()).isEqualTo("Desc atualizada");
        assertThat(servico.getPreco()).isEqualByComparingTo(BigDecimal.valueOf(200));
        assertThat(servico.getTempoEstimadoHoras()).isEqualTo(Duration.ofHours(3));
    }

    @Test
    void deveLancarExcecaoAoAtualizarComNomeNulo() {
        Servico servico = Servico.criar("Nome", "Desc", BigDecimal.valueOf(100), Duration.ofHours(1));

        assertThatThrownBy(() -> servico.atualizar(null, "Desc", BigDecimal.valueOf(100), Duration.ofHours(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nome é obrigatório");
    }

    @Test
    void deveLancarExcecaoAoAtualizarComPrecoNegativo() {
        Servico servico = Servico.criar("Nome", "Desc", BigDecimal.valueOf(100), Duration.ofHours(1));

        assertThatThrownBy(() -> servico.atualizar("Nome", "Desc", BigDecimal.valueOf(-50), Duration.ofHours(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Preço deve ser um número positivo");
    }

    // --- ativar / inativar ---

    @Test
    void deveAtivarServico() {
        Servico servico = Servico.reconstituir("id", "Nome", "Desc", BigDecimal.valueOf(100), Duration.ofHours(1), false);

        servico.ativar();

        assertThat(servico.isAtivo()).isTrue();
    }

    @Test
    void deveInativarServico() {
        Servico servico = Servico.criar("Nome", "Desc", BigDecimal.valueOf(100), Duration.ofHours(1));

        servico.inativar();

        assertThat(servico.isAtivo()).isFalse();
    }
}
