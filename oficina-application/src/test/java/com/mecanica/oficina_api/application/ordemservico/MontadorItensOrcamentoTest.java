package com.mecanica.oficina_api.application.ordemservico;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.insumo.gateway.InsumosGateway;
import com.mecanica.oficina_api.application.ordemservico.input.GerarOrcamentoInput;
import com.mecanica.oficina_api.application.servico.gateway.ServicoGateway;
import com.mecanica.oficina_api.domain.insumo.Insumos;
import com.mecanica.oficina_api.domain.ordemservico.ItemOrcamento;
import com.mecanica.oficina_api.domain.servico.Servico;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MontadorItensOrcamentoTest {

    @Mock
    private InsumosGateway insumosGateway;

    @Mock
    private ServicoGateway servicoGateway;

    @InjectMocks
    private MontadorItensOrcamento montador;

    @Test
    void deveMontarItensDeInsumosEServicos() {
        Insumos insumo = Insumos.reconstituir("ins-1", "Óleo", BigDecimal.valueOf(50), 10, 2, "LITRO");
        Servico servico = Servico.reconstituir("serv-1", "Troca de óleo", "Troca completa", BigDecimal.valueOf(150), Duration.ofHours(2), true);
        when(insumosGateway.buscar("ins-1")).thenReturn(Optional.of(insumo));
        when(servicoGateway.buscar("serv-1")).thenReturn(Optional.of(servico));

        GerarOrcamentoInput input = new GerarOrcamentoInput(
            List.of(new GerarOrcamentoInput.ItemInsumoInput("ins-1", 2)),
            List.of(new GerarOrcamentoInput.ItemServicoInput("serv-1", 1)),
            null
        );

        List<ItemOrcamento> itens = montador.montar(input);

        assertThat(itens).hasSize(2);
        assertThat(itens.get(0).getInsumoId()).isEqualTo("ins-1");
        assertThat(itens.get(0).getQuantidade()).isEqualTo(2);
        assertThat(itens.get(0).getPrecoUnitario()).isEqualTo(BigDecimal.valueOf(50));
        assertThat(itens.get(1).getServicoId()).isEqualTo("serv-1");
        assertThat(itens.get(1).getPrecoUnitario()).isEqualTo(BigDecimal.valueOf(150));
    }

    @Test
    void deveLancarExcecao_quandoInsumoNaoEncontrado() {
        when(insumosGateway.buscar("ins-x")).thenReturn(Optional.empty());

        GerarOrcamentoInput input = new GerarOrcamentoInput(
            List.of(new GerarOrcamentoInput.ItemInsumoInput("ins-x", 1)),
            null,
            null
        );

        assertThatThrownBy(() -> montador.montar(input))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Insumo não encontrado");
    }

    @Test
    void deveLancarExcecao_quandoServicoNaoEncontrado() {
        when(servicoGateway.buscar("serv-x")).thenReturn(Optional.empty());

        GerarOrcamentoInput input = new GerarOrcamentoInput(
            null,
            List.of(new GerarOrcamentoInput.ItemServicoInput("serv-x", 1)),
            null
        );

        assertThatThrownBy(() -> montador.montar(input))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Serviço não encontrado");
    }

    @Test
    void deveLancarExcecao_quandoNenhumItemInformado() {
        GerarOrcamentoInput input = new GerarOrcamentoInput(null, null, null);

        assertThatThrownBy(() -> montador.montar(input))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Orçamento deve ter ao menos um item");

        verify(insumosGateway, never()).buscar(anyString());
        verify(servicoGateway, never()).buscar(anyString());
    }

    @Test
    void deveLancarExcecao_quandoListasVazias() {
        GerarOrcamentoInput input = new GerarOrcamentoInput(List.of(), List.of(), null);

        assertThatThrownBy(() -> montador.montar(input))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Orçamento deve ter ao menos um item");
    }
}
