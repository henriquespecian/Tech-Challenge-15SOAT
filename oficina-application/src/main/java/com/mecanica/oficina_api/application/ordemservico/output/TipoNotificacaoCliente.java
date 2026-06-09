package com.mecanica.oficina_api.application.ordemservico.output;

/** Texto estável para filtro em log (ex.: {@code tipo_evento=envio_orcamento}). */
public enum TipoNotificacaoCliente {
    ENVIO_ORCAMENTO,
    FINALIZACAO_OS;

    public String chaveLog() {
        return switch (this) {
            case ENVIO_ORCAMENTO -> "envio_orcamento";
            case FINALIZACAO_OS -> "finalizacao_os";
        };
    }
}
