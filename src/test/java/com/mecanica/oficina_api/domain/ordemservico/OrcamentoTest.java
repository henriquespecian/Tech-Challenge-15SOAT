package com.mecanica.oficina_api.domain.ordemservico;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class OrcamentoTest {

    private ItemOrcamento item(String desc, int qtd, double preco) {
        return new ItemOrcamento(desc, qtd, BigDecimal.valueOf(preco));
    }

    private Orcamento orcamentoSimples() {
        return new Orcamento(List.of(item("Troca de óleo", 1, 50.0), item("Filtro", 2, 30.0)), "obs");
    }

    @Test
    void deveCriarOrcamentoComItensECalcularValorTotal() {
        Orcamento o = orcamentoSimples();

        assertThat(o.getStatus()).isEqualTo(OrcamentoStatus.PENDENTE);
        assertThat(o.getValorTotal()).isEqualByComparingTo(new BigDecimal("110.0"));
        assertThat(o.getItens()).hasSize(2);
    }

    @Test
    void deveLancarExcecaoQuandoListaDeItensForVazia() {
        assertThatThrownBy(() -> new Orcamento(List.of(), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveTransicionarDeStatuPendente_paraEnviado() {
        Orcamento o = orcamentoSimples();
        o.enviar();
        assertThat(o.getStatus()).isEqualTo(OrcamentoStatus.ENVIADO);
    }

    @Test
    void deveLancarExcecaoAoEnviarOrcamentoNaoPendente() {
        Orcamento o = orcamentoSimples();
        o.enviar();
        assertThatThrownBy(o::enviar).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deveTransicionarDeEnviadoParaNegociacao() {
        Orcamento o = orcamentoSimples();
        o.enviar();
        o.negociar();
        assertThat(o.getStatus()).isEqualTo(OrcamentoStatus.EM_NEGOCIACAO);
    }

    @Test
    void deveLancarExcecaoAoNegociarOrcamentoNaoEnviado() {
        Orcamento o = orcamentoSimples();
        assertThatThrownBy(o::negociar).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deveAprovarOrcamentoEnviado() {
        Orcamento o = orcamentoSimples();
        o.enviar();
        o.aprovar();
        assertThat(o.getStatus()).isEqualTo(OrcamentoStatus.APROVADO);
    }

    @Test
    void deveAprovarOrcamentoEmNegociacao() {
        Orcamento o = orcamentoSimples();
        o.enviar();
        o.negociar();
        o.aprovar();
        assertThat(o.getStatus()).isEqualTo(OrcamentoStatus.APROVADO);
    }

    @Test
    void deveLancarExcecaoAoAprovarOrcamentoPendente() {
        Orcamento o = orcamentoSimples();
        assertThatThrownBy(o::aprovar).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deveNegarOrcamentoEnviado() {
        Orcamento o = orcamentoSimples();
        o.enviar();
        o.negar();
        assertThat(o.getStatus()).isEqualTo(OrcamentoStatus.NEGADO);
    }

    @Test
    void deveLancarExcecaoAoNegarOrcamentoPendente() {
        Orcamento o = orcamentoSimples();
        assertThatThrownBy(o::negar).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void itemOrcamento_deveLancarExcecaoQuantidadeZero() {
        assertThatThrownBy(() -> item("desc", 0, 10.0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void itemOrcamento_deveLancarExcecaoPrecoZero() {
        assertThatThrownBy(() -> item("desc", 1, 0.0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
