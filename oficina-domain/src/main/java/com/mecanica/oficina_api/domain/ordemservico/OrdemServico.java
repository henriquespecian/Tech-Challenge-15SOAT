package com.mecanica.oficina_api.domain.ordemservico;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class OrdemServico {

    private String id;
    private final String veiculoId;
    private final String clienteId;
    private OrdemServicoStatus status;
    private Orcamento orcamento;
    private BigDecimal valorFinal;
    private LocalDateTime dataFinal;

    protected OrdemServico(String veiculoId, String clienteId) {
        this.veiculoId = Objects.requireNonNull(veiculoId, "Veículo é obrigatório");
        this.clienteId = Objects.requireNonNull(clienteId, "Cliente é obrigatório");
        this.status = OrdemServicoStatus.RECEBIDA;
    }

    public static OrdemServico criar(String veiculoId, String clienteId) {
        return new OrdemServico(veiculoId, clienteId);
    }


//    public static OrdemServico criar(String veiculoId, String clienteId, Orcamento orcamento) {
//
//    }

    public static OrdemServico reconstituir(String id, String veiculoId, String clienteId,
                                            OrdemServicoStatus status, Orcamento orcamento,
                                            BigDecimal valorFinal, LocalDateTime dataFinal) {
        OrdemServico os = new OrdemServico(veiculoId, clienteId);
        os.id = id;
        os.status = status;
        os.orcamento = orcamento;
        os.valorFinal = valorFinal;
        os.dataFinal = dataFinal;
        return os;
    }

    // RECEBIDA → EM_DIAGNOSTICO
    public void iniciarDiagnostico() {
        if (status != OrdemServicoStatus.RECEBIDA)
            throw new IllegalStateException("OS deve estar RECEBIDA para iniciar diagnóstico");
        this.status = OrdemServicoStatus.EM_DIAGNOSTICO;
    }

    // EM_DIAGNOSTICO → gera orçamento (permanece em EM_DIAGNOSTICO até enviar)
    public void gerarOrcamento(List<ItemOrcamento> itens, String observacoes) {
        if (status == OrdemServicoStatus.FINALIZADA || status == OrdemServicoStatus.ENTREGUE)
            throw new IllegalStateException("Não é possível gerar orçamento para OS " + status);
        this.orcamento = new Orcamento(itens, observacoes);
    }

    public void atualizarOrcamento(List<ItemOrcamento> itens, String observacoes) {
        if (orcamento == null || orcamento.getStatus() != OrcamentoStatus.AGUARDANDO)
            throw new IllegalStateException("Orçamento deve estar AGUARDANDO para ser atualizado");
        this.orcamento = new Orcamento(itens, observacoes);
    }

    // Envia orçamento ao cliente → OS entra em AGUARDANDO_APROVACAO
    public void enviarOrcamento() {
        if (orcamento == null)
            throw new IllegalStateException("OS não possui orçamento");
        this.orcamento = orcamento.enviar();
        this.status = OrdemServicoStatus.AGUARDANDO_APROVACAO;
    }

    // Cliente pede ajustes → orçamento vai a AGUARDANDO (negociação)
    public void aguardarOrcamento() {
        if (orcamento == null)
            throw new IllegalStateException("OS não possui orçamento");
        this.orcamento = orcamento.aguardar();
    }

    // Cliente aprova → orçamento APROVADO, OS permanece em AGUARDANDO_APROVACAO
    public void aprovarOrcamento() {
        if (orcamento == null)
            throw new IllegalStateException("OS não possui orçamento");
        this.orcamento = orcamento.aprovar();
    }

    // Mecânico inicia execução → OS vai a EM_EXECUCAO
    public void iniciarExecucao() {
        if (orcamento == null || orcamento.getStatus() != OrcamentoStatus.APROVADO)
            throw new IllegalStateException("Orçamento não aprovado");
        if (status != OrdemServicoStatus.AGUARDANDO_APROVACAO)
            throw new IllegalStateException("OS não está aguardando aprovação");
        this.status = OrdemServicoStatus.EM_EXECUCAO;
    }

    // Cliente nega → orçamento marcado como NEGADO, OS encerrada como FINALIZADA (sem execução)
    public void negarOrcamento() {
        if (orcamento == null)
            throw new IllegalStateException("OS não possui orçamento");
        this.orcamento = orcamento.negar();
        this.dataFinal = java.time.LocalDateTime.now();
        this.status = OrdemServicoStatus.FINALIZADA;
    }

    // Execução concluída → FINALIZADA, registra valor e data
    public void finalizar() {
        if (status != OrdemServicoStatus.EM_EXECUCAO)
            throw new IllegalStateException("OS deve estar EM_EXECUCAO para ser finalizada");
        this.valorFinal = orcamento != null ? orcamento.getValorTotal() : BigDecimal.ZERO;
        this.dataFinal = LocalDateTime.now();
        this.status = OrdemServicoStatus.FINALIZADA;
    }

    // Cliente retira o veículo → ENTREGUE
    public void entregar() {
        if (status != OrdemServicoStatus.FINALIZADA)
            throw new IllegalStateException("OS deve estar FINALIZADA para registrar entrega");
        this.status = OrdemServicoStatus.ENTREGUE;
    }

    public String getId() { return id; }
    public String getVeiculoId() { return veiculoId; }
    public String getClienteId() { return clienteId; }
    public OrdemServicoStatus getStatus() { return status; }
    public Orcamento getOrcamento() { return orcamento; }
    public BigDecimal getValorFinal() { return valorFinal; }
    public LocalDateTime getDataFinal() { return dataFinal; }
}
