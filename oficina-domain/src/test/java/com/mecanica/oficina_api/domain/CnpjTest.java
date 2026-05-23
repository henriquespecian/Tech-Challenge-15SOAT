package com.mecanica.oficina_api.domain;

import org.junit.jupiter.api.Test;

import com.mecanica.oficina_api.domain.cliente.Cnpj;

import static org.junit.jupiter.api.Assertions.*;

class CnpjTest {

    // CNPJ matematicamente válido: 11.222.333/0001-81
    private static final String CNPJ_VALIDO       = "11222333000181";
    private static final String CNPJ_FORMATADO     = "11.222.333/0001-81";

    @Test
    void deveAceitarCnpjValido() {
        Cnpj cnpj = new Cnpj(CNPJ_VALIDO);
        assertEquals(CNPJ_VALIDO, cnpj.getValue());
    }

    @Test
    void deveAceitarCnpjFormatado() {
        Cnpj cnpj = new Cnpj(CNPJ_FORMATADO);
        assertEquals(CNPJ_VALIDO, cnpj.getValue());
    }

    @Test
    void deveRejeitarNull() {
        assertThrows(IllegalArgumentException.class, () -> new Cnpj(null));
    }

    @Test
    void deveRejeitarDigitosVerificadoresErrados() {
        assertThrows(IllegalArgumentException.class, () -> new Cnpj("11222333000199"));
    }

    @Test
    void deveRejeitarMenosDe14Digitos() {
        assertThrows(IllegalArgumentException.class, () -> new Cnpj("1122233300018"));
    }

    @Test
    void deveRejeitarSequenciaHomogenea() {
        assertThrows(IllegalArgumentException.class, () -> new Cnpj("00000000000000"));
    }

    @Test
    void deveTerEqualsEHashCodePorValor() {
        Cnpj a = new Cnpj(CNPJ_VALIDO);
        Cnpj b = new Cnpj(CNPJ_FORMATADO);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
