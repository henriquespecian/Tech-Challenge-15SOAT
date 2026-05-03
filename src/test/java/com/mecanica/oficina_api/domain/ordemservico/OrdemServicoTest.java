package com.mecanica.oficina_api.domain.ordemservico;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrdemServicoTest {

    private List<ItemOrcamento> itens() {
        return List.of(new ItemOrcamento(null, null, "Troca de oleo", 1, BigDecimal.valueOf(100.0)));
    }

    @Test
    void deveCriarOrdemServicoComoRecebida() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");

        assertThat(os.getVeiculoId()).isEqualTo("veiculo-1");
        assertThat(os.getClienteId()).isEqualTo("cliente-1");
        assertThat(os.getStatus()).isEqualTo(OrdemServicoStatus.RECEBIDA);
        assertThat(os.getOrcamento()).isNull();
        assertThat(os.getValorFinal()).isNull();
        assertThat(os.getDataFinal()).isNull();
    }

    @Test
    void deveLancarExcecaoQuandoVeiculoIdForNulo() {
        assertThatThrownBy(() -> OrdemServico.criar(null, "cliente-1"))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void deveLancarExcecaoQuandoClienteIdForNulo() {
        assertThatThrownBy(() -> OrdemServico.criar("veiculo-1", null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void deveIniciarDiagnosticoDeOsRecebida() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");
        os.iniciarDiagnostico();

        assertThat(os.getStatus()).isEqualTo(OrdemServicoStatus.EM_DIAGNOSTICO);
    }

    @Test
    void deveLancarExcecaoAoIniciarDiagnosticoEmOsNaoRecebida() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");
        os.iniciarDiagnostico();

        assertThatThrownBy(os::iniciarDiagnostico).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deveGerarOrcamentoComSucesso() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");
        os.iniciarDiagnostico();
        os.gerarOrcamento(itens(), "observacao");

        assertThat(os.getOrcamento()).isNotNull();
        assertThat(os.getOrcamento().getStatus()).isEqualTo(OrcamentoStatus.PENDENTE);
    }

    @Test
    void deveEnviarOrcamentoETransicionarOsParaAguardandoAprovacao() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");
        os.iniciarDiagnostico();
        os.gerarOrcamento(itens(), null);
        os.enviarOrcamento();

        assertThat(os.getOrcamento().getStatus()).isEqualTo(OrcamentoStatus.ENVIADO);
        assertThat(os.getStatus()).isEqualTo(OrdemServicoStatus.AGUARDANDO_APROVACAO);
    }

    @Test
    void deveAguardarOrcamento() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");
        os.iniciarDiagnostico();
        os.gerarOrcamento(itens(), null);
        os.enviarOrcamento();
        os.aguardarOrcamento();

        assertThat(os.getOrcamento().getStatus()).isEqualTo(OrcamentoStatus.AGUARDANDO);
    }

    @Test
    void deveAtualizarOrcamentoEAjustarStatusParaPendente() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");
        os.iniciarDiagnostico();
        os.gerarOrcamento(itens(), null);
        os.enviarOrcamento();
        os.aguardarOrcamento();
        os.atualizarOrcamento(itens(), "ajuste");

        assertThat(os.getOrcamento().getStatus()).isEqualTo(OrcamentoStatus.PENDENTE);
        assertThat(os.getOrcamento().getObservacoes()).isEqualTo("ajuste");
    }

    @Test
    void deveAprovarOrcamentoEManterOsEmAguardandoAprovacao() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");
        os.iniciarDiagnostico();
        os.gerarOrcamento(itens(), null);
        os.enviarOrcamento();
        os.aprovarOrcamento();

        assertThat(os.getStatus()).isEqualTo(OrdemServicoStatus.AGUARDANDO_APROVACAO);
        assertThat(os.getOrcamento().getStatus()).isEqualTo(OrcamentoStatus.APROVADO);
        assertThat(os.getOrcamento().getRespondidoEm()).isNotNull();
    }

    @Test
    void deveIniciarExecucaoAposOrcamentoAprovado() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");
        os.iniciarDiagnostico();
        os.gerarOrcamento(itens(), null);
        os.enviarOrcamento();
        os.aprovarOrcamento();
        os.iniciarExecucao();

        assertThat(os.getStatus()).isEqualTo(OrdemServicoStatus.EM_EXECUCAO);
    }

    @Test
    void deveLancarExcecaoAoIniciarExecucaoSemOrcamentoAprovado() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");
        os.iniciarDiagnostico();
        os.gerarOrcamento(itens(), null);
        os.enviarOrcamento();

        assertThatThrownBy(os::iniciarExecucao).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deveNegarOrcamentoEFinalizarOs() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");
        os.iniciarDiagnostico();
        os.gerarOrcamento(itens(), null);
        os.enviarOrcamento();
        os.negarOrcamento();

        assertThat(os.getOrcamento().getStatus()).isEqualTo(OrcamentoStatus.NEGADO);
        assertThat(os.getStatus()).isEqualTo(OrdemServicoStatus.FINALIZADA);
        assertThat(os.getDataFinal()).isNotNull();
    }

    @Test
    void deveFinalizarOsEmExecucaoComValorEData() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");
        os.iniciarDiagnostico();
        os.gerarOrcamento(itens(), null);
        os.enviarOrcamento();
        os.aprovarOrcamento();
        os.iniciarExecucao();
        os.finalizar();

        assertThat(os.getStatus()).isEqualTo(OrdemServicoStatus.FINALIZADA);
        assertThat(os.getValorFinal()).isEqualByComparingTo(BigDecimal.valueOf(100.0));
        assertThat(os.getDataFinal()).isNotNull();
    }

    @Test
    void deveLancarExcecaoAoFinalizarOsNaoEmExecucao() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");
        assertThatThrownBy(os::finalizar).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deveEntregarVeiculoDeOsFinalizada() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");
        os.iniciarDiagnostico();
        os.gerarOrcamento(itens(), null);
        os.enviarOrcamento();
        os.aprovarOrcamento();
        os.iniciarExecucao();
        os.finalizar();
        os.entregar();

        assertThat(os.getStatus()).isEqualTo(OrdemServicoStatus.ENTREGUE);
    }

    @Test
    void deveLancarExcecaoAoEntregarVeiculoDeOsNaoFinalizada() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");
        assertThatThrownBy(os::entregar).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deveLancarExcecaoAoGerarOrcamentoEmOsFinalizada() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");
        os.iniciarDiagnostico();
        os.gerarOrcamento(itens(), null);
        os.enviarOrcamento();
        os.aprovarOrcamento();
        os.iniciarExecucao();
        os.finalizar();

        assertThatThrownBy(() -> os.gerarOrcamento(itens(), null))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deveLancarExcecaoAoGerarOrcamentoEmOsEntregue() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");
        os.iniciarDiagnostico();
        os.gerarOrcamento(itens(), null);
        os.enviarOrcamento();
        os.aprovarOrcamento();
        os.iniciarExecucao();
        os.finalizar();
        os.entregar();

        assertThatThrownBy(() -> os.gerarOrcamento(itens(), null))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deveLancarExcecaoAoAprovarSemOrcamento() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");
        assertThatThrownBy(os::aprovarOrcamento).isInstanceOf(IllegalStateException.class);
    }
}
