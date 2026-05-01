package com.mecanica.oficina_api.application.ordemservico;

import com.mecanica.oficina_api.infrastructure.persistence.ClienteJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.InsumosJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.ItemOrcamentoJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.OrdemServicoJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.VeiculoJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.repository.ClienteSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.InsumosSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.OrdemServicoSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.VeiculoSpringDataRepository;
import com.mecanica.oficina_api.interfaces.dto.request.CriarOrdemServicoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.GerarOrcamentoRequest;
import com.mecanica.oficina_api.interfaces.dto.request.ItemOrcamentoRequest;
import com.mecanica.oficina_api.interfaces.dto.response.OrdemServicoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdemServicoServiceTest {

    @Mock private OrdemServicoSpringDataRepository ordemServicoRepository;
    @Mock private VeiculoSpringDataRepository veiculoRepository;
    @Mock private ClienteSpringDataRepository clienteRepository;
    @Mock private InsumosSpringDataRepository insumosRepository;
    @InjectMocks private OrdemServicoService service;

    private VeiculoJpaEntity veiculo;
    private ClienteJpaEntity cliente;
    private OrdemServicoJpaEntity osEntity;

    @BeforeEach
    void setUp() {
        veiculo = new VeiculoJpaEntity();
        veiculo.setId("veiculo-1");
        veiculo.setAtivo(true);

        cliente = new ClienteJpaEntity();
        cliente.setId("cliente-1");
        cliente.setAtivo(true);

        osEntity = new OrdemServicoJpaEntity();
        osEntity.setId("os-1");
        osEntity.setVeiculoId("veiculo-1");
        osEntity.setClienteId("cliente-1");
        osEntity.setStatus("EM_TRIAGEM");
    }

    // --- criar ---

    @Test
    void deveCriarOrdemServicoComSucesso() {
        when(veiculoRepository.findByIdAndAtivoTrue("veiculo-1")).thenReturn(Optional.of(veiculo));
        when(clienteRepository.findById("cliente-1")).thenReturn(Optional.of(cliente));

        CriarOrdemServicoRequest req = new CriarOrdemServicoRequest();
        req.setVeiculoId("veiculo-1");
        req.setClienteId("cliente-1");

        service.criar(req);

        verify(ordemServicoRepository).save(argThat(e -> "EM_TRIAGEM".equals(e.getStatus())));
    }

    @Test
    void deveLancarExcecaoQuandoVeiculoNaoEncontradoAoCriar() {
        when(veiculoRepository.findByIdAndAtivoTrue("veiculo-x")).thenReturn(Optional.empty());

        CriarOrdemServicoRequest req = new CriarOrdemServicoRequest();
        req.setVeiculoId("veiculo-x");
        req.setClienteId("cliente-1");

        assertThatThrownBy(() -> service.criar(req))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));

        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoEncontradoAoCriar() {
        when(veiculoRepository.findByIdAndAtivoTrue("veiculo-1")).thenReturn(Optional.of(veiculo));
        when(clienteRepository.findById("cliente-x")).thenReturn(Optional.empty());

        CriarOrdemServicoRequest req = new CriarOrdemServicoRequest();
        req.setVeiculoId("veiculo-1");
        req.setClienteId("cliente-x");

        assertThatThrownBy(() -> service.criar(req))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));

        verify(ordemServicoRepository, never()).save(any());
    }

    // --- buscarPorId ---

    @Test
    void deveBuscarPorIdComSucesso() {
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));

        OrdemServicoResponse resp = service.buscarPorId("os-1");

        assertThat(resp.getId()).isEqualTo("os-1");
        assertThat(resp.getStatus()).isEqualTo("EM_TRIAGEM");
        assertThat(resp.getOrcamento()).isNull();
    }

    @Test
    void deveLancarExcecaoQuandoOsNaoEncontrada() {
        when(ordemServicoRepository.findById("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId("inexistente"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    // --- listarPorVeiculo ---

    @Test
    void deveListarPorVeiculoComSucesso() {
        when(ordemServicoRepository.findByVeiculoId("veiculo-1")).thenReturn(List.of(osEntity));

        List<OrdemServicoResponse> lista = service.listarPorVeiculo("veiculo-1");

        assertThat(lista).hasSize(1);
    }

    // --- gerarOrcamento ---

    @Test
    void deveGerarOrcamentoComSucesso() {
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(ordemServicoRepository.save(any())).thenReturn(osEntityComOrcamento("PENDENTE"));
        when(insumosRepository.findByIdAndAtivoTrue("insumo-1")).thenReturn(Optional.of(insumoEntity()));

        OrdemServicoResponse resp = service.gerarOrcamento("os-1", gerarOrcamentoRequest());

        assertThat(resp.getOrcamento()).isNotNull();
        verify(ordemServicoRepository).save(argThat(e -> "PENDENTE".equals(e.getOrcamentoStatus())));
    }

    @Test
    void deveLancarConflictAoGerarOrcamentoEmOsFinalizada() {
        osEntity.setStatus("FINALIZADO");
        osEntity.setOrcamentoStatus("APROVADO");
        osEntity.setItensOrcamento(itensEntity());
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(insumosRepository.findByIdAndAtivoTrue("insumo-1")).thenReturn(Optional.of(insumoEntity()));

        assertThatThrownBy(() -> service.gerarOrcamento("os-1", gerarOrcamentoRequest()))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    // --- enviarOrcamento ---

    @Test
    void deveEnviarOrcamentoComSucesso() {
        osEntity.setOrcamentoStatus("PENDENTE");
        osEntity.setItensOrcamento(itensEntity());
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        when(ordemServicoRepository.save(any())).thenReturn(osEntityComOrcamento("ENVIADO"));

        OrdemServicoResponse resp = service.enviarOrcamento("os-1");

        assertThat(resp.getOrcamento().getStatus()).isEqualTo("ENVIADO");
    }

    @Test
    void deveLancarConflictAoEnviarOrcamentoJaEnviado() {
        osEntity.setOrcamentoStatus("ENVIADO");
        osEntity.setItensOrcamento(itensEntity());
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));

        assertThatThrownBy(() -> service.enviarOrcamento("os-1"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    // --- aprovarOrcamento ---

    @Test
    void deveAprovarOrcamentoEFinalizarOs() {
        osEntity.setOrcamentoStatus("ENVIADO");
        osEntity.setItensOrcamento(itensEntity());
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        OrdemServicoJpaEntity finalizado = osEntityComOrcamento("APROVADO");
        finalizado.setStatus("FINALIZADO");
        when(ordemServicoRepository.save(any())).thenReturn(finalizado);

        OrdemServicoResponse resp = service.aprovarOrcamento("os-1");

        assertThat(resp.getStatus()).isEqualTo("FINALIZADO");
        assertThat(resp.getOrcamento().getStatus()).isEqualTo("APROVADO");
    }

    // --- retirarVeiculo ---

    @Test
    void deveRetirarVeiculoDeOsFinalizada() {
        osEntity.setStatus("FINALIZADO");
        osEntity.setOrcamentoStatus("APROVADO");
        osEntity.setItensOrcamento(itensEntity());
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));
        OrdemServicoJpaEntity retirado = osEntityComOrcamento("APROVADO");
        retirado.setStatus("VEICULO_RETIRADO");
        when(ordemServicoRepository.save(any())).thenReturn(retirado);

        OrdemServicoResponse resp = service.retirarVeiculo("os-1");

        assertThat(resp.getStatus()).isEqualTo("VEICULO_RETIRADO");
    }

    @Test
    void deveLancarConflictAoRetirarVeiculoDeOsNaoFinalizada() {
        when(ordemServicoRepository.findById("os-1")).thenReturn(Optional.of(osEntity));

        assertThatThrownBy(() -> service.retirarVeiculo("os-1"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    // --- helpers ---

    private GerarOrcamentoRequest gerarOrcamentoRequest() {
        ItemOrcamentoRequest item = new ItemOrcamentoRequest();
        item.setInsumoId("insumo-1");
        item.setQuantidade(1);

        GerarOrcamentoRequest req = new GerarOrcamentoRequest();
        req.setInsumos(List.of(item));
        req.setObservacoes("obs");
        return req;
    }

    private InsumosJpaEntity insumoEntity() {
        InsumosJpaEntity ie = new InsumosJpaEntity();
        ie.setId("insumo-1");
        ie.setNome("Óleo de motor");
        ie.setPrecoUnitario(BigDecimal.valueOf(100.0));
        ie.setEstoqueAtual(10);
        ie.setEstoqueMinimo(2);
        ie.setUnidade("Litro");
        ie.setAtivo(true);
        return ie;
    }

    private List<ItemOrcamentoJpaEntity> itensEntity() {
        ItemOrcamentoJpaEntity ie = new ItemOrcamentoJpaEntity();
        ie.setDescricao("Troca de óleo");
        ie.setQuantidade(1);
        ie.setPrecoUnitario(BigDecimal.valueOf(100.0));
        return new java.util.ArrayList<>(List.of(ie));
    }

    private OrdemServicoJpaEntity osEntityComOrcamento(String orcStatus) {
        OrdemServicoJpaEntity e = new OrdemServicoJpaEntity();
        e.setId("os-1");
        e.setVeiculoId("veiculo-1");
        e.setClienteId("cliente-1");
        e.setStatus("EM_TRIAGEM");
        e.setOrcamentoStatus(orcStatus);
        e.setItensOrcamento(itensEntity());
        return e;
    }
}
