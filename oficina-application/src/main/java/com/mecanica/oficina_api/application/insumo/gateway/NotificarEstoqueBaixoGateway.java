package com.mecanica.oficina_api.application.insumo.gateway;

import com.mecanica.oficina_api.application.insumo.output.AlertaEstoqueBaixo;

public interface NotificarEstoqueBaixoGateway {

    void notificar(AlertaEstoqueBaixo alerta);
}
