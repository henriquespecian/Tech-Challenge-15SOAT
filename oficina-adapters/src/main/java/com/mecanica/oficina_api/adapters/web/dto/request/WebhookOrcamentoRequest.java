package com.mecanica.oficina_api.adapters.web.dto.request;

public class WebhookOrcamentoRequest {

    private String externalId;
    private String observacoes;

    public WebhookOrcamentoRequest() {}

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
