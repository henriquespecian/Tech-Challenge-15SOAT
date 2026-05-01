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
    void deveTransicionarDePendenteParaEnviado() {
        Orcamento o = orcamentoSimples();
        Orcamento enviado = o.enviar();
        assertThat(enviado.getStatus()).isEqualTo(OrcamentoStatus.ENVIADO);
    }

    @Test
    void deveLancarExcecaoAoEnviarOrcamentoNaoPendente() {
        Orcamento enviado = orcamentoSimples().enviar();
        assertThatThrownBy(enviado::enviar).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deveTransicionarDeEnviadoParaAguardando() {
        Orcamento o = orcamentoSimples();
        Orcamento enviado = o.enviar();
        Orcamento aguardando = enviado.aguardar();
        assertThat(aguardando.getStatus()).isEqualTo(OrcamentoStatus.AGUARDANDO);
    }

    @Test
    void deveLancarExcecaoAoAguardarOrcamentoNaoEnviado() {
        Orcamento o = orcamentoSimples();
        assertThatThrownBy(o::aguardar).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deveAprovarOrcamentoEnviado() {
        Orcamento enviado = orcamentoSimples().enviar();
        Orcamento aprovado = enviado.aprovar();
        assertThat(aprovado.getStatus()).isEqualTo(OrcamentoStatus.APROVADO);
        assertThat(aprovado.getRespondidoEm()).isNotNull();
    }

    @Test
    void deveAprovarOrcamentoAguardando() {
        Orcamento aguardando = orcamentoSimples().enviar().aguardar();
        Orcamento aprovado = aguardando.aprovar();
        assertThat(aprovado.getStatus()).isEqualTo(OrcamentoStatus.APROVADO);
    }

    @Test
    void deveLancarExcecaoAoAprovarOrcamentoPendente() {
        Orcamento o = orcamentoSimples();
        assertThatThrownBy(o::aprovar).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deveNegarOrcamentoEnviado() {
        Orcamento enviado = orcamentoSimples().enviar();
        Orcamento negado = enviado.negar();
        assertThat(negado.getStatus()).isEqualTo(OrcamentoStatus.NEGADO);
        assertThat(negado.getRespondidoEm()).isNotNull();
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
