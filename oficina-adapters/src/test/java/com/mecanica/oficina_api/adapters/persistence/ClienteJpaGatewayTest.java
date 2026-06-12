package com.mecanica.oficina_api.adapters.persistence;

import com.mecanica.oficina_api.domain.cliente.Cliente;
import com.mecanica.oficina_api.domain.cliente.Documento;
import com.mecanica.oficina_api.domain.cliente.Email;
import com.mecanica.oficina_api.domain.cliente.Telefone;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Import(ClienteJpaGateway.class)
@Transactional
class ClienteJpaGatewayTest {

    private static final String CPF = "52998224725";

    @Autowired
    private ClienteJpaGateway gateway;

    private Cliente novoCliente(String documento) {
        return Cliente.criar("Maria", Documento.parse(documento),
                new Email("maria@email.com"), new Telefone("11999999999"));
    }

    @Test
    void deveSalvarEBuscarClientePorDocumentoAtivo() {
        Cliente salvo = gateway.save(novoCliente(CPF));

        assertThat(salvo.getId()).isNotBlank();
        assertThat(gateway.existsByDocumento(CPF)).isTrue();

        Optional<Cliente> encontrado = gateway.findByDocumentoAtivo(CPF);
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Maria");
        assertThat(encontrado.get().getEmail().getValue()).isEqualTo("maria@email.com");
    }

    @Test
    void deveRetornarVazio_quandoDocumentoNaoExiste() {
        assertThat(gateway.existsByDocumento(CPF)).isFalse();
        assertThat(gateway.findByDocumentoAtivo(CPF)).isEmpty();
        assertThat(gateway.findByIdAtivo("inexistente")).isEmpty();
    }

    @Test
    void deveFazerSoftDelete_mantendoRegistroMasInativando() {
        Cliente salvo = gateway.save(novoCliente(CPF));

        gateway.softDelete(CPF);

        assertThat(gateway.findByDocumentoAtivo(CPF)).isEmpty();
        assertThat(gateway.findByIdAtivo(salvo.getId())).isEmpty();
        // existsByDocumento não filtra por ativo: o registro continua existindo
        assertThat(gateway.existsByDocumento(CPF)).isTrue();
    }

    @Test
    void deveListarApenasClientesAtivos() {
        gateway.save(novoCliente(CPF));
        gateway.softDelete(CPF);
        gateway.save(Cliente.criar("Ativo", Documento.parse("11144477735"),
                new Email("ativo@email.com"), new Telefone("11988887777")));

        List<Cliente> ativos = gateway.findAllAtivos();

        assertThat(ativos).hasSize(1);
        assertThat(ativos.get(0).getNome()).isEqualTo("Ativo");
    }
}
