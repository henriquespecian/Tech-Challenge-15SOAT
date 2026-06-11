package com.mecanica.oficina_api.application.ordemservico.usecase;

import static com.mecanica.oficina_api.application.ordemservico.OrdemServicoFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
import com.mecanica.oficina_api.domain.ordemservico.OrcamentoStatus;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServicoStatus;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NegarOrcamentoUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @InjectMocks
    private NegarOrcamentoUseCase useCase;

    @Test
    void deveNegarOrcamentoEFinalizarOs() {
        OrdemServico os = osComOrcamento(OrdemServicoStatus.AGUARDANDO_APROVACAO, OrcamentoStatus.ENVIADO,
            List.of(itemServico("serv-1")));
        when(ordemServicoGateway.encontrarOuLancar(OS_ID)).thenReturn(os);
        when(ordemServicoGateway.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        OrdemServico resultado = useCase.executar(OS_ID);

        assertThat(resultado.getOrcamento().getStatus()).isEqualTo(OrcamentoStatus.NEGADO);
        assertThat(resultado.getStatus()).isEqualTo(OrdemServicoStatus.FINALIZADA);
        assertThat(resultado.getDataFinal()).isNotNull();
    }

    @Test
    void deveLancarExcecao_quandoOrcamentoNaoPodeSerNegado() {
        OrdemServico os = osComOrcamento(OrdemServicoStatus.EM_DIAGNOSTICO, OrcamentoStatus.PENDENTE,
            List.of(itemServico("serv-1")));
        when(ordemServicoGateway.encontrarOuLancar(OS_ID)).thenReturn(os);

        assertThatThrownBy(() -> useCase.executar(OS_ID))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Orçamento deve estar ENVIADO ou AGUARDANDO");

        verify(ordemServicoGateway, never()).atualizar(any());
    }
}
