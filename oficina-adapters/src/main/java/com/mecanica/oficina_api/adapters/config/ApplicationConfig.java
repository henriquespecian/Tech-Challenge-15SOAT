package com.mecanica.oficina_api.adapters.config;

import com.mecanica.oficina_api.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_api.application.cliente.usecase.AlterarClienteUseCase;
import com.mecanica.oficina_api.application.cliente.usecase.CadastrarClienteUseCase;
import com.mecanica.oficina_api.application.cliente.usecase.ConsultarClienteUseCase;
import com.mecanica.oficina_api.application.cliente.usecase.DeletarClienteUseCase;
import com.mecanica.oficina_api.application.cliente.usecase.ListarClientesUseCase;
import com.mecanica.oficina_api.application.insumo.InsumosService;
import com.mecanica.oficina_api.application.insumo.NotificadorEstoqueBaixo;
import com.mecanica.oficina_api.application.insumo.gateway.InsumosGateway;
import com.mecanica.oficina_api.application.ordemservico.OrdemServicoService;
import com.mecanica.oficina_api.application.ordemservico.gateway.NotificadorCliente;
import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
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

    /* Cliente */
    @Bean
    CadastrarClienteUseCase cadastrarClienteUseCase(ClienteGateway gateway) {
        return new CadastrarClienteUseCase(gateway);
    }
    @Bean
    ConsultarClienteUseCase consultarClienteUseCase(ClienteGateway gateway) {
        return new ConsultarClienteUseCase(gateway);
    }
    @Bean
    ListarClientesUseCase listarClientesUseCase(ClienteGateway gateway) {
        return new ListarClientesUseCase(gateway);
    }
    @Bean
    AlterarClienteUseCase alterarClienteUseCase(ClienteGateway gateway) {
        return new AlterarClienteUseCase(gateway);
    }
    @Bean
    DeletarClienteUseCase deletarClienteUseCase(ClienteGateway gateway) {
        return new DeletarClienteUseCase(gateway);
    }

    /* Insumos */
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

    @Bean
    OrdemServicoService ordemServicoService(OrdemServicoGateway ordemServicoGateway,
            VeiculoGateway veiculoGateway,
            ClienteGateway clienteGateway,
            InsumosGateway insumosGateway,
            ServicoGateway servicoGateway,
            NotificadorEstoqueBaixo notificadorEstoqueBaixo,
            NotificadorCliente notificadorCliente,
            StatusServicoGateway statusServicoGateway) {
        return new OrdemServicoService(ordemServicoGateway, veiculoGateway, clienteGateway, insumosGateway,
                servicoGateway, notificadorEstoqueBaixo, notificadorCliente, statusServicoGateway);
    }
}
