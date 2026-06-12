package com.mecanica.oficina_api.adapters.persistence;

import com.mecanica.oficina_api.adapters.persistence.repository.StatusServicoSpringDataRepository;
import com.mecanica.oficina_api.domain.ordemservico.ServicoStatus;
import com.mecanica.oficina_api.domain.ordemservico.StatusServico;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Import(StatusServicoJpaGateway.class)
@Transactional
class StatusServicoJpaGatewayTest {

    private static final String OS_ID = "os-1";

    @Autowired
    private StatusServicoJpaGateway gateway;

    @Autowired
    private StatusServicoSpringDataRepository repository;

    @Test
    void deveSalvarListaERetornarComIds() {
        StatusServico s1 = StatusServico.criar(OS_ID, "servico-1");
        StatusServico s2 = StatusServico.criar(OS_ID, "servico-2");

        List<StatusServico> salvos = gateway.salvarLista(List.of(s1, s2));

        assertThat(salvos).hasSize(2);
        assertThat(salvos).allMatch(s -> s.getId() != null && !s.getId().isBlank());
        assertThat(salvos).allMatch(s -> s.getStatus() == ServicoStatus.AGUARDANDO);
    }

    @Test
    void deveListarServicosPorOS() {
        gateway.salvarLista(List.of(
                StatusServico.criar(OS_ID, "servico-1"),
                StatusServico.criar(OS_ID, "servico-2"),
                StatusServico.criar("outra-os", "servico-3")));

        List<StatusServico> servicos = gateway.listarServicosPorOS(OS_ID);

        assertThat(servicos).hasSize(2);
        assertThat(servicos).allMatch(s -> s.getOrdemServicoId().equals(OS_ID));
    }

    @Test
    void deveBuscarPorIdEStatus() {
        StatusServico salvo = gateway.salvarLista(List.of(StatusServico.criar(OS_ID, "servico-1"))).get(0);

        Optional<StatusServico> encontrado = gateway.buscarPorIdEStatus(salvo.getId(), ServicoStatus.AGUARDANDO);
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getServicoId()).isEqualTo("servico-1");

        assertThat(gateway.buscarPorIdEStatus(salvo.getId(), ServicoStatus.FINALIZADO)).isEmpty();
    }

    @Test
    void deveAtualizarStatusServico() {
        StatusServico salvo = gateway.salvarLista(List.of(StatusServico.criar(OS_ID, "servico-1"))).get(0);

        salvo.iniciarServico();
        StatusServico atualizado = gateway.atualizar(salvo);

        assertThat(atualizado.getStatus()).isEqualTo(ServicoStatus.INICIADO);
        assertThat(atualizado.getDataInicio()).isNotNull();

        Optional<StatusServico> lido = gateway.buscarPorIdEStatus(salvo.getId(), ServicoStatus.INICIADO);
        assertThat(lido).isPresent();
    }

    @Test
    void deveCalcularTempoMedioMinutos_comServicosFinalizados() {
        LocalDateTime base = LocalDateTime.of(2026, 1, 1, 10, 0);
        // Persiste direto via repositório com id gerado e datas determinísticas (60 e 120 min)
        repository.save(statusFinalizado("servico-1", base, base.plusMinutes(60)));
        repository.save(statusFinalizado("servico-1", base, base.plusMinutes(120)));

        double media = gateway.calcularTempoMedioMinutos("servico-1");

        assertThat(media).isEqualTo(90.0);
    }

    private StatusServicoJpaEntity statusFinalizado(String servicoId, LocalDateTime inicio, LocalDateTime fim) {
        StatusServicoJpaEntity e = new StatusServicoJpaEntity();
        e.setOrdemServicoId(OS_ID);
        e.setServicoId(servicoId);
        e.setStatus(ServicoStatus.FINALIZADO.name());
        e.setDataInicio(inicio);
        e.setDataFim(fim);
        return e;
    }

    @Test
    void deveRetornarZero_quandoNaoHaServicosFinalizados() {
        gateway.salvarLista(List.of(StatusServico.criar(OS_ID, "servico-1")));

        assertThat(gateway.calcularTempoMedioMinutos("servico-1")).isEqualTo(0.0);
        assertThat(gateway.calcularTempoMedioMinutos("inexistente")).isEqualTo(0.0);
    }
}
