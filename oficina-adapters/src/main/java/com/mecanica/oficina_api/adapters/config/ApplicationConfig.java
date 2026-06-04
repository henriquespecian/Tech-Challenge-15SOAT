package com.mecanica.oficina_api.adapters.config;

import com.mecanica.oficina_api.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_api.application.cliente.usecase.AlterarClienteUseCase;
import com.mecanica.oficina_api.application.cliente.usecase.CadastrarClienteUseCase;
import com.mecanica.oficina_api.application.cliente.usecase.ConsultarClienteUseCase;
import com.mecanica.oficina_api.application.cliente.usecase.DeletarClienteUseCase;
import com.mecanica.oficina_api.application.cliente.usecase.ListarClientesUseCase;
import com.mecanica.oficina_api.application.insumo.gateway.InsumosGateway;
import com.mecanica.oficina_api.application.insumo.gateway.NotificarEstoqueBaixoGateway;
import com.mecanica.oficina_api.application.insumo.usecase.AlterarInsumoUseCase;
import com.mecanica.oficina_api.application.insumo.usecase.AtivarInsumoUseCase;
import com.mecanica.oficina_api.application.insumo.usecase.CadastrarInsumoUseCase;
import com.mecanica.oficina_api.application.insumo.usecase.ConsultarInsumoUseCase;
import com.mecanica.oficina_api.application.insumo.usecase.InativarInsumoUseCase;
import com.mecanica.oficina_api.application.insumo.usecase.ListarInsumosUseCase;
import com.mecanica.oficina_api.application.insumo.usecase.RegistrarCompraUseCase;
import com.mecanica.oficina_api.application.ordemservico.OrdemServicoService;
import com.mecanica.oficina_api.application.ordemservico.gateway.NotificadorCliente;
import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
import com.mecanica.oficina_api.application.servico.gateway.ServicoGateway;
import com.mecanica.oficina_api.application.servico.gateway.StatusServicoGateway;
import com.mecanica.oficina_api.application.servico.usecase.AlterarServicoUseCase;
import com.mecanica.oficina_api.application.servico.usecase.AtivarServicoUseCase;
import com.mecanica.oficina_api.application.servico.usecase.CadastrarServicoUseCase;
import com.mecanica.oficina_api.application.servico.usecase.ConsultarServicoUseCase;
import com.mecanica.oficina_api.application.servico.usecase.ConsultarTempoMedioUseCase;
import com.mecanica.oficina_api.application.servico.usecase.InativarServicoUseCase;
import com.mecanica.oficina_api.application.servico.usecase.ListarServicosUseCase;
import com.mecanica.oficina_api.application.usuario.gateway.PasswordEncoder;
import com.mecanica.oficina_api.application.usuario.gateway.UsuarioGateway;
import com.mecanica.oficina_api.application.usuario.usecase.AlterarUsuarioUseCase;
import com.mecanica.oficina_api.application.usuario.usecase.CadastrarUsuarioUseCase;
import com.mecanica.oficina_api.application.usuario.usecase.ConsultarUsuarioUseCase;
import com.mecanica.oficina_api.application.usuario.usecase.InativarUsuarioUseCase;
import com.mecanica.oficina_api.application.veiculo.gateway.VeiculoGateway;
import com.mecanica.oficina_api.application.veiculo.usecase.AlterarVeiculoUseCase;
import com.mecanica.oficina_api.application.veiculo.usecase.CadastrarVeiculoUseCase;
import com.mecanica.oficina_api.application.veiculo.usecase.ConsultarVeiculoUseCase;
import com.mecanica.oficina_api.application.veiculo.usecase.InativarVeiculoUseCase;
import com.mecanica.oficina_api.application.veiculo.usecase.ListarVeiculosPorClienteUseCase;
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
    CadastrarInsumoUseCase cadastrarInsumoUseCase(InsumosGateway insumosGateway) {
        return new CadastrarInsumoUseCase(insumosGateway);
    }
    @Bean
    ConsultarInsumoUseCase consultarInsumoUseCase(InsumosGateway insumosGateway) {
        return new ConsultarInsumoUseCase(insumosGateway);
    }
    @Bean
    ListarInsumosUseCase listarInsumosUseCase(InsumosGateway insumosGateway) {
        return new ListarInsumosUseCase(insumosGateway);
    }
    @Bean
    AlterarInsumoUseCase alterarInsumoUseCase(InsumosGateway insumosGateway, NotificarEstoqueBaixoGateway notificarEstoqueBaixoGateway) {
        return new AlterarInsumoUseCase(insumosGateway, notificarEstoqueBaixoGateway);
    }
    @Bean
    AtivarInsumoUseCase ativarInsumoUseCase(InsumosGateway insumosGateway) {
        return new AtivarInsumoUseCase(insumosGateway);
    }
    @Bean
    InativarInsumoUseCase inativarInsumoUseCase(InsumosGateway insumosGateway) {
        return new InativarInsumoUseCase(insumosGateway);
    }
    @Bean
    RegistrarCompraUseCase registrarCompraUseCase(InsumosGateway insumosGateway) {
        return new RegistrarCompraUseCase(insumosGateway);
    }

    /* Serviço */
    @Bean
    CadastrarServicoUseCase cadastrarServicoUseCase(ServicoGateway servicoGateway) {
        return new CadastrarServicoUseCase(servicoGateway);
    }
    @Bean
    ConsultarServicoUseCase consultarServicoUseCase(ServicoGateway servicoGateway) {
        return new ConsultarServicoUseCase(servicoGateway);
    }
    @Bean
    ConsultarTempoMedioUseCase consultarTempoMedioUseCase(ServicoGateway servicoGateway, StatusServicoGateway statusServicoGateway) {
        return new ConsultarTempoMedioUseCase(servicoGateway, statusServicoGateway);
    }
    @Bean
    ListarServicosUseCase listarServicosUseCase(ServicoGateway servicoGateway) {
        return new ListarServicosUseCase(servicoGateway);
    }
    @Bean
    AlterarServicoUseCase alterarServicoUseCase(ServicoGateway servicoGateway) {
        return new AlterarServicoUseCase(servicoGateway);
    }
    @Bean
    AtivarServicoUseCase ativarServicoUseCase(ServicoGateway servicoGateway) {
        return new AtivarServicoUseCase(servicoGateway);
    }
    @Bean
    InativarServicoUseCase inativarServicoUseCase(ServicoGateway servicoGateway) {
        return new InativarServicoUseCase(servicoGateway);
    }

    /* Usuário */
    @Bean
    CadastrarUsuarioUseCase cadastrarUsuarioUseCase(UsuarioGateway usuarioGateway, PasswordEncoder passwordEncoder) {
        return new CadastrarUsuarioUseCase(usuarioGateway, passwordEncoder);
    }
    @Bean
    ConsultarUsuarioUseCase consultarUsuarioUseCase(UsuarioGateway usuarioGateway) {
        return new ConsultarUsuarioUseCase(usuarioGateway);
    }
    @Bean
    AlterarUsuarioUseCase alterarUsuarioUseCase(UsuarioGateway usuarioGateway) {
        return new AlterarUsuarioUseCase(usuarioGateway);
    }
    @Bean
    InativarUsuarioUseCase inativarUsuarioUseCase(UsuarioGateway usuarioGateway) {
        return new InativarUsuarioUseCase(usuarioGateway);
    }


    /* Veículo */
    @Bean
    CadastrarVeiculoUseCase cadastrarVeiculoUseCase(VeiculoGateway veiculoGateway, ClienteGateway clienteGateway) {
        return new CadastrarVeiculoUseCase(veiculoGateway, clienteGateway);
    }
    @Bean
    ConsultarVeiculoUseCase consultarVeiculoUseCase(VeiculoGateway veiculoGateway) {
        return new ConsultarVeiculoUseCase(veiculoGateway);
    }
    @Bean
    ListarVeiculosPorClienteUseCase listarVeiculosPorClienteUseCase(VeiculoGateway veiculoGateway) {
        return new ListarVeiculosPorClienteUseCase(veiculoGateway);
    }
    @Bean
    AlterarVeiculoUseCase alterarVeiculoUseCase(VeiculoGateway veiculoGateway) {
        return new AlterarVeiculoUseCase(veiculoGateway);
    }
    @Bean
    InativarVeiculoUseCase inativarVeiculoUseCase(VeiculoGateway veiculoGateway) {
        return new InativarVeiculoUseCase(veiculoGateway);
    }

    @Bean
    OrdemServicoService ordemServicoService(OrdemServicoGateway ordemServicoGateway,
            VeiculoGateway veiculoGateway,
            ClienteGateway clienteGateway,
            InsumosGateway insumosGateway,
            ServicoGateway servicoGateway,
            NotificarEstoqueBaixoGateway notificadorEstoqueBaixo,
            NotificadorCliente notificadorCliente,
            StatusServicoGateway statusServicoGateway) {
        return new OrdemServicoService(ordemServicoGateway, veiculoGateway, clienteGateway, insumosGateway,
                servicoGateway, notificadorEstoqueBaixo, notificadorCliente, statusServicoGateway);
    }
}
