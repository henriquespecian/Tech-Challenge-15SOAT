package com.mecanica.oficina_api.application.insumo;

/**
 * Carga útil do alerta — campos pensados para template de e-mail futuro.
 */
public record AlertaEstoqueBaixo(
        String insumoId,
        String nomeInsumo,
        int estoqueAnterior,
        int estoqueAtual,
        int estoqueMinimo,
        OrigemNotificacaoEstoque origem,
        String referenciaOrigem) {

    /**
     * Emite quando o par (estoque, mínimo) fica na zona crítica ({@code estoque <= mínimo})
     * e pelo menos uma destas situações ocorre:
     * <ul>
     *   <li>o estoque estava estritamente acima do mínimo antigo (baixa típica ou ajuste de quantidade);</li>
     *   <li>o mínimo foi aumentado e o estoque atual fica igual ou abaixo do novo mínimo
     *       (ex.: estava 5 com mínimo 5 e só sobe o mínimo para 10).</li>
     * </ul>
     * Não emite quando já era crítico com os mesmos limites e só baixa mais estoque (evita spam).
     */
    public static boolean deveEmitirAlerta(
            int estoqueAnterior,
            int estoqueMinimoAnterior,
            int estoqueAtual,
            int estoqueMinimoAtual) {
        boolean agoraCritico = estoqueAtual <= estoqueMinimoAtual;
        if (!agoraCritico) {
            return false;
        }
        boolean estavaEstritamenteAcimaDoMinimoAntigo = estoqueAnterior > estoqueMinimoAnterior;
        boolean minimoAumentou = estoqueMinimoAtual > estoqueMinimoAnterior;
        return estavaEstritamenteAcimaDoMinimoAntigo || minimoAumentou;
    }
}
