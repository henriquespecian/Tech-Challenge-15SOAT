package com.mecanica.oficina_api.domain.ordemservico;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class OrdemServicoTest {

    private List<ItemOrcamento> itens() {
        return List.of(new ItemOrcamento("Troca de óleo", 1, BigDecimal.valueOf(100.0)));
    }

    @Test
    void deveCriarOrdemServicoEmTriagem() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");

        assertThat(os.getVeiculoId()).isEqualTo("veiculo-1");
        assertThat(os.getClienteId()).isEqualTo("cliente-1");
        assertThat(os.getStatus()).isEqualTo(OrdemServicoStatus.EM_TRIAGEM);
        assertThat(os.getOrcamento()).isNull();
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
    void deveGerarOrcamentoComSucesso() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");
        os.gerarOrcamento(itens(), "observacao");

        assertThat(os.getOrcamento()).isNotNull();
        assertThat(os.getOrcamento().getStatus()).isEqualTo(OrcamentoStatus.PENDENTE);
    }

    @Test
    void deveLancarExcecaoAoGerarOrcamentoEmOsFinalizadaa() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");
        os.gerarOrcamento(itens(), null);
        os.enviarOrcamento();
        os.aprovarOrcamento();

        assertThatThrownBy(() -> os.gerarOrcamento(itens(), null))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deveLancarExcecaoAoGerarOrcamentoEmOsComVeiculoRetirado() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");
        os.gerarOrcamento(itens(), null);
        os.enviarOrcamento();
        os.aprovarOrcamento();
        os.retirarVeiculo();

        assertThatThrownBy(() -> os.gerarOrcamento(itens(), null))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deveAprovarOrcamentoEFinalizarOs() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");
        os.gerarOrcamento(itens(), null);
        os.enviarOrcamento();
        os.aprovarOrcamento();

        assertThat(os.getStatus()).isEqualTo(OrdemServicoStatus.FINALIZADO);
        assertThat(os.getOrcamento().getStatus()).isEqualTo(OrcamentoStatus.APROVADO);
    }

    @Test
    void deveLancarExcecaoAoAprovarSemOrcamento() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");
        assertThatThrownBy(os::aprovarOrcamento).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deveRetirarVeiculoDeOsFinalizada() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");
        os.gerarOrcamento(itens(), null);
        os.enviarOrcamento();
        os.aprovarOrcamento();
        os.retirarVeiculo();

        assertThat(os.getStatus()).isEqualTo(OrdemServicoStatus.VEICULO_RETIRADO);
    }

    @Test
    void deveLancarExcecaoAoRetirarVeiculoDeOsNaoFinalizada() {
        OrdemServico os = OrdemServico.criar("veiculo-1", "cliente-1");
        assertThatThrownBy(os::retirarVeiculo).isInstanceOf(IllegalStateException.class);
    }
}
