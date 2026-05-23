package com.mecanica.oficina_api.adapters.config;

import com.mecanica.oficina_api.application.cliente.ClienteService;
import com.mecanica.oficina_api.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_api.application.insumo.InsumosService;
import com.mecanica.oficina_api.application.insumo.NotificadorEstoqueBaixo;
import com.mecanica.oficina_api.application.insumo.gateway.InsumosGateway;
import com.mecanica.oficina_api.application.servico.ServicoService;
import com.mecanica.oficina_api.application.servico.gateway.ServicoGateway;
import com.mecanica.oficina_api.application.servico.gateway.StatusServicoGateway;
import com.mecanica.oficina_api.application.usuario.PasswordEncoder;
import com.mecanica.oficina_api.application.usuario.UsuarioService;
import com.mecanica.oficina_api.application.usuario.gateway.UsuarioGateway;
import com.mecanica.oficina_api.application.veiculo.VeiculoService;
import com.mecanica.oficina_api.application.veiculo.gateway.VeiculoGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    ClienteService clienteService(ClienteGateway gateway) {
        return new ClienteService(gateway);
    }

    @Bean
    InsumosService insumosService(InsumosGateway insumosgateway, NotificadorEstoqueBaixo notificador ) {
        return new InsumosService(insumosgateway, notificador);
    }

    @Bean
    ServicoService servicoService(ServicoGateway servicoGateway, StatusServicoGateway statusServicoGateway) {
        return new ServicoService(servicoGateway, statusServicoGateway);
    }

    @Bean
    UsuarioService usuarioService(UsuarioGateway usuarioGateway, ClienteGateway clienteGateway, PasswordEncoder passwordEncoder) {
        return new UsuarioService(usuarioGateway, clienteGateway, passwordEncoder);
    }

    @Bean
    VeiculoService veiculoService(VeiculoGateway veiculoGateway, ClienteGateway clienteGateway) {
        return new VeiculoService(veiculoGateway, clienteGateway);
    }
}
