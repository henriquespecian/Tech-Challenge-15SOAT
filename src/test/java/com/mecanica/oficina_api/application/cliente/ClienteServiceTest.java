package com.mecanica.oficina_api.application.cliente;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mecanica.oficina_api.infrastructure.persistence.ClienteJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.repository.ClienteSpringDataRepository;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarClienteRequest;
import com.mecanica.oficina_api.interfaces.dto.response.ConsultarClienteResponse;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

  @Mock
  private ClienteSpringDataRepository clienteRepository;

  @InjectMocks
  private ClienteService clienteService;
  private CadastrarClienteRequest cadastrarClienteRequest;

  @BeforeEach
  void setUp() {
    cadastrarClienteRequest = new CadastrarClienteRequest();

    cadastrarClienteRequest.setNome("Joao Silva");
    cadastrarClienteRequest.setDocumento("37518712091");
    cadastrarClienteRequest.setEmail("cliente@teste.com");
    cadastrarClienteRequest.setTelefone("5437891237");

  }

  @Test
  void deveCriarClienteComSucesso() {
    var clienteSalvo = new ClienteJpaEntity();

    clienteSalvo.setId("cliente-1");
    clienteSalvo.setNome("Joao Silva");
    clienteSalvo.setDocumento("37518712091");
    clienteSalvo.setEmail("cliente@teste.com");
    clienteSalvo.setTelefone("5437891237");
    clienteSalvo.setDataCadastro(LocalDateTime.ofEpochSecond(1777752619, 0, ZoneOffset.UTC));
    clienteSalvo.setAtivo(true);

    when(clienteRepository.save(any())).thenReturn(clienteSalvo);

    ConsultarClienteResponse resp = clienteService.cadastrar(cadastrarClienteRequest);

    assertThat(resp.getDocumento()).isEqualTo("37518712091");
    verify(clienteRepository).save(argThat(e -> e.getAtivo()));

  }
}
