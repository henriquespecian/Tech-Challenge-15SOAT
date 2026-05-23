package com.mecanica.oficina_api.domain.ordemservico;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrcamentoTest {

    private ItemOrcamento item(String desc, int qtd, double preco) {
        return new ItemOrcamento(null, null, desc, qtd, BigDecimal.valueOf(preco));
    }

    private Orcamento orcamentoSimples() {
        return new Orcamento(List.of(item("Troca de oleo", 1, 50.0), item("Filtro", 2, 30.0)), "obs");
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
    void deveTransicionarDePendenteParaEnviado() {
        Orcamento enviado = orcamentoSimples().enviar();

        assertThat(enviado.getStatus()).isEqualTo(OrcamentoStatus.ENVIADO);
    }

    @Test
    void deveLancarExcecaoAoEnviarOrcamentoNaoPendente() {
        Orcamento enviado = orcamentoSimples().enviar();

        assertThatThrownBy(enviado::enviar).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deveTransicionarDeEnviadoParaAguardando() {
        Orcamento aguardando = orcamentoSimples().enviar().aguardar();

        assertThat(aguardando.getStatus()).isEqualTo(OrcamentoStatus.AGUARDANDO);
    }

    @Test
    void deveLancarExcecaoAoAguardarOrcamentoNaoEnviado() {
        Orcamento o = orcamentoSimples();

        assertThatThrownBy(o::aguardar).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deveAprovarOrcamentoEnviado() {
        Orcamento aprovado = orcamentoSimples().enviar().aprovar();

        assertThat(aprovado.getStatus()).isEqualTo(OrcamentoStatus.APROVADO);
        assertThat(aprovado.getRespondidoEm()).isNotNull();
    }

    @Test
    void deveAprovarOrcamentoAguardando() {
        Orcamento aprovado = orcamentoSimples().enviar().aguardar().aprovar();

        assertThat(aprovado.getStatus()).isEqualTo(OrcamentoStatus.APROVADO);
    }

    @Test
    void deveLancarExcecaoAoAprovarOrcamentoPendente() {
        Orcamento o = orcamentoSimples();

        assertThatThrownBy(o::aprovar).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deveNegarOrcamentoEnviado() {
        Orcamento negado = orcamentoSimples().enviar().negar();

        assertThat(negado.getStatus()).isEqualTo(OrcamentoStatus.NEGADO);
        assertThat(negado.getRespondidoEm()).isNotNull();
    }

    @Test
    void deveNegarOrcamentoAguardando() {
        Orcamento negado = orcamentoSimples().enviar().aguardar().negar();

        assertThat(negado.getStatus()).isEqualTo(OrcamentoStatus.NEGADO);
        assertThat(negado.getRespondidoEm()).isNotNull();
    }

    @Test
    void deveLancarExcecaoAoNegarOrcamentoPendente() {
        Orcamento o = orcamentoSimples();

        assertThatThrownBy(o::negar).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void itemOrcamentoDeveLancarExcecaoQuantidadeZero() {
        assertThatThrownBy(() -> item("desc", 0, 10.0))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void itemOrcamentoDeveLancarExcecaoPrecoZero() {
        assertThatThrownBy(() -> item("desc", 1, 0.0))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
