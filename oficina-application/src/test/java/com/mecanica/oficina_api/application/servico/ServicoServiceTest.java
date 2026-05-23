package com.mecanica.oficina_api.application.servico;

import com.mecanica.oficina_api.application.servico.gateway.ServicoGateway;
import com.mecanica.oficina_api.application.servico.gateway.StatusServicoGateway;
import com.mecanica.oficina_api.domain.servico.Servico;
import com.mecanica.oficina_api.domain.servico.TempoMedioServico;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoServiceTest {

    @Mock
    private ServicoGateway servicoGateway;

    @Mock
    private StatusServicoGateway statusServicoGateway;

    @InjectMocks
    private ServicoService servicoService;

    private Servico servicoPadrao;

    @BeforeEach
    void setUp() {
        servicoPadrao = Servico.reconstituir(
            "srv-1", "Troca de óleo", "Troca completa do óleo do motor",
            BigDecimal.valueOf(150), Duration.ofHours(1), true
        );
    }

    // --- cadastrar ---

    @Test
    void deveCadastrarServicoComSucesso() {
        when(servicoGateway.cadastrar(any(Servico.class))).thenReturn(servicoPadrao);

        Servico resp = servicoService.cadastrar("Troca de óleo", "Troca completa do óleo do motor", BigDecimal.valueOf(150), 1);

        assertThat(resp.getId()).isEqualTo("srv-1");
        assertThat(resp.getNome()).isEqualTo("Troca de óleo");
        assertThat(resp.isAtivo()).isTrue();
        verify(servicoGateway).cadastrar(any(Servico.class));
    }

    @Test
    void deveLancarExcecaoAoCadastrarComNomeNulo() {
        assertThatThrownBy(() -> servicoService.cadastrar(null, "Desc", BigDecimal.valueOf(150), 1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Nome é obrigatório");
        verify(servicoGateway, never()).cadastrar(any());
    }

    @Test
    void deveLancarExcecaoAoCadastrarComPrecoNegativo() {
        assertThatThrownBy(() -> servicoService.cadastrar("Nome", "Desc", BigDecimal.valueOf(-1), 1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Preço deve ser um número positivo");
        verify(servicoGateway, never()).cadastrar(any());
    }

    // --- buscar ---

    @Test
    void deveBuscarServicoComSucesso() {
        when(servicoGateway.buscar("srv-1")).thenReturn(Optional.of(servicoPadrao));

        Servico resp = servicoService.buscar("srv-1");

        assertThat(resp.getId()).isEqualTo("srv-1");
        assertThat(resp.getNome()).isEqualTo("Troca de óleo");
    }

    @Test
    void deveLancarExcecaoQuandoServicoNaoEncontrado() {
        when(servicoGateway.buscar("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicoService.buscar("inexistente"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Serviço não encontrado");
    }

    // --- listar ---

    @Test
    void deveListarServicosAtivos() {
        Servico segundo = Servico.reconstituir("srv-2", "Alinhamento", "Desc", BigDecimal.valueOf(200), Duration.ofHours(2), true);
        when(servicoGateway.listar()).thenReturn(List.of(servicoPadrao, segundo));

        List<Servico> resp = servicoService.listar();

        assertThat(resp).hasSize(2);
        assertThat(resp).extracting(Servico::getNome).containsExactly("Troca de óleo", "Alinhamento");
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaServicosAtivos() {
        when(servicoGateway.listar()).thenReturn(List.of());

        assertThat(servicoService.listar()).isEmpty();
    }

    // --- alterar ---

    @Test
    void deveAlterarServicoComSucesso() {
        Servico atualizado = Servico.reconstituir("srv-1", "Alinhamento e balanceamento", "Desc", BigDecimal.valueOf(200), Duration.ofHours(2), true);
        when(servicoGateway.buscar("srv-1")).thenReturn(Optional.of(servicoPadrao));
        when(servicoGateway.alterar(eq("srv-1"), any(Servico.class))).thenReturn(atualizado);

        Servico resp = servicoService.alterar("srv-1", "Alinhamento e balanceamento", "Desc", BigDecimal.valueOf(200), 2);

        assertThat(resp.getNome()).isEqualTo("Alinhamento e balanceamento");
        verify(servicoGateway).alterar(eq("srv-1"), any(Servico.class));
    }

    @Test
    void deveLancarExcecaoQuandoServicoNaoEncontradoAoAlterar() {
        when(servicoGateway.buscar("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicoService.alterar("inexistente", "Qualquer", "Desc", BigDecimal.valueOf(100), 1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Serviço não encontrado");
        verify(servicoGateway, never()).alterar(any(), any());
    }

    // --- ativar ---

    @Test
    void deveAtivarServicoComSucesso() {
        Servico inativo = Servico.reconstituir("srv-1", "Troca de óleo", "Desc", BigDecimal.valueOf(150), Duration.ofHours(1), false);
        Servico ativo = Servico.reconstituir("srv-1", "Troca de óleo", "Desc", BigDecimal.valueOf(150), Duration.ofHours(1), true);
        when(servicoGateway.buscar("srv-1")).thenReturn(Optional.of(inativo));
        when(servicoGateway.ativar("srv-1")).thenReturn(ativo);

        Servico resp = servicoService.ativar("srv-1");

        assertThat(resp.isAtivo()).isTrue();
    }

    @Test
    void deveLancarExcecaoQuandoServicoNaoEncontradoAoAtivar() {
        when(servicoGateway.buscar("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicoService.ativar("inexistente"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Serviço não encontrado");
        verify(servicoGateway, never()).ativar(any());
    }

    // --- inativar ---

    @Test
    void deveInativarServicoComSucesso() {
        when(servicoGateway.buscar("srv-1")).thenReturn(Optional.of(servicoPadrao));

        servicoService.inativar("srv-1");

        verify(servicoGateway).inativar("srv-1");
    }

    @Test
    void deveLancarExcecaoQuandoServicoNaoEncontradoAoInativar() {
        when(servicoGateway.buscar("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicoService.inativar("inexistente"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Serviço não encontrado");
        verify(servicoGateway, never()).inativar(any());
    }

    // --- buscarTempoMedio ---

    @Test
    void deveBuscarTempoMedioComSucesso() {
        when(servicoGateway.buscar("srv-1")).thenReturn(Optional.of(servicoPadrao));
        when(statusServicoGateway.calcularTempoMedioMinutos("srv-1")).thenReturn(45.0);

        TempoMedioServico resp = servicoService.buscarTempoMedio("srv-1");

        assertThat(resp.servicoId()).isEqualTo("srv-1");
        assertThat(resp.nome()).isEqualTo("Troca de óleo");
        assertThat(resp.tempoMedioEmMinutos()).isEqualTo(45.0);
    }

    @Test
    void deveLancarExcecaoQuandoServicoNaoEncontradoAoBuscarTempoMedio() {
        when(servicoGateway.buscar("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicoService.buscarTempoMedio("inexistente"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Serviço não encontrado");
        verify(statusServicoGateway, never()).calcularTempoMedioMinutos(any());
    }
}
