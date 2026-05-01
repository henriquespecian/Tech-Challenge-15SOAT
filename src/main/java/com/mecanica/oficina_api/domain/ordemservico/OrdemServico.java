package com.mecanica.oficina_api.domain.ordemservico;

import java.util.List;
import java.util.Objects;

public class OrdemServico {

    private String id;
    private final String veiculoId;
    private final String clienteId;
    private OrdemServicoStatus status;
    private Orcamento orcamento;

    protected OrdemServico(String veiculoId, String clienteId) {
        this.veiculoId = Objects.requireNonNull(veiculoId, "Veículo é obrigatório");
        this.clienteId = Objects.requireNonNull(clienteId, "Cliente é obrigatório");
        this.status = OrdemServicoStatus.EM_TRIAGEM;
    }

    public static OrdemServico criar(String veiculoId, String clienteId) {
        return new OrdemServico(veiculoId, clienteId);
    }

    public static OrdemServico reconstituir(String id, String veiculoId, String clienteId,
                                            OrdemServicoStatus status, Orcamento orcamento) {
        OrdemServico os = new OrdemServico(veiculoId, clienteId);
        os.id = id;
        os.status = status;
        os.orcamento = orcamento;
        return os;
    }

    public void gerarOrcamento(List<ItemOrcamento> itens, String observacoes) {
        if (status == OrdemServicoStatus.FINALIZADO || status == OrdemServicoStatus.VEICULO_RETIRADO)
            throw new IllegalStateException("Não é possível gerar orçamento para OS " + status);
        this.orcamento = new Orcamento(itens, observacoes);
    }

    public void atualizarOrcamento(List<ItemOrcamento> itens, String observacoes) {
        if (orcamento == null || orcamento.getStatus() != OrcamentoStatus.EM_NEGOCIACAO)
            throw new IllegalStateException("Orçamento deve estar EM_NEGOCIACAO para ser atualizado");
        this.orcamento = new Orcamento(itens, observacoes);
    }

    public void enviarOrcamento() {
        if (orcamento == null)
            throw new IllegalStateException("OS não possui orçamento");
        orcamento.enviar();
    }

    public void negociarOrcamento() {
        if (orcamento == null)
            throw new IllegalStateException("OS não possui orçamento");
        orcamento.negociar();
    }

    public void aprovarOrcamento() {
        if (orcamento == null)
            throw new IllegalStateException("OS não possui orçamento");
        orcamento.aprovar();
        this.status = OrdemServicoStatus.FINALIZADO;
    }

    public void negarOrcamento() {
        if (orcamento == null)
            throw new IllegalStateException("OS não possui orçamento");
        orcamento.negar();
    }

    public void retirarVeiculo() {
        if (status != OrdemServicoStatus.FINALIZADO)
            throw new IllegalStateException("OS deve estar FINALIZADO para retirar o veículo");
        this.status = OrdemServicoStatus.VEICULO_RETIRADO;
    }

    public String getId() { return id; }
    public String getVeiculoId() { return veiculoId; }
    public String getClienteId() { return clienteId; }
    public OrdemServicoStatus getStatus() { return status; }
    public Orcamento getOrcamento() { return orcamento; }
}
