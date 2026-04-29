package com.mecanica.oficina_api.config;

import com.mecanica.oficina_api.domain.usuario.Perfil;
import com.mecanica.oficina_api.infrastructure.persistence.ClienteJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.UsuarioJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.VeiculoJpaEntity;
import com.mecanica.oficina_api.infrastructure.persistence.repository.ClienteSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.UsuarioSpringDataRepository;
import com.mecanica.oficina_api.infrastructure.persistence.repository.VeiculoSpringDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Ativação: SPRING_PROFILES_ACTIVE=dev
 * Roda apenas uma vez — pula se já houver dados no banco.
 *
 * Credenciais criadas:
 *   admin@oficina.com       / admin123    (ADMIN)
 *   mecanico@oficina.com    / mecanico123 (MECANICO)
 *   atendente@oficina.com   / atendente123 (ATENDENTE)
 *   ana.portal@teste.com    / cliente123  (CLIENTE → Ana Souza)
 */
@Component
@Profile("dev")
public class DevDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataLoader.class);

    private final ClienteSpringDataRepository clienteRepository;
    private final VeiculoSpringDataRepository veiculoRepository;
    private final UsuarioSpringDataRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DevDataLoader(ClienteSpringDataRepository clienteRepository,
                         VeiculoSpringDataRepository veiculoRepository,
                         UsuarioSpringDataRepository usuarioRepository) {
        this.clienteRepository = clienteRepository;
        this.veiculoRepository = veiculoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {
        log.info("=== [DEV] Iniciando carga de dados de teste ===");
        ClienteJpaEntity ana = seedClientes();
        seedVeiculos();
        seedUsuarios(ana);
        log.info("=== [DEV] Carga concluída ===");
    }

    private ClienteJpaEntity seedClientes() {
        if (clienteRepository.count() > 0) {
            log.info("[DEV] Clientes já existem, pulando.");
            return clienteRepository.findByCpf("52998224725").orElse(null);
        }

        ClienteJpaEntity ana = cliente("Ana Souza",      "52998224725", "ana@teste.com",   "11999991111");
        ClienteJpaEntity bruno = cliente("Bruno Lima",   "11144477735", "bruno@teste.com", "11999992222");
        ClienteJpaEntity carla = cliente("Carla Mendes", "45317828791", "carla@teste.com", "11999993333");

        clienteRepository.save(ana);
        clienteRepository.save(bruno);
        clienteRepository.save(carla);

        log.info("[DEV] 3 clientes criados: Ana Souza, Bruno Lima, Carla Mendes");
        return ana;
    }

    private void seedVeiculos() {
        if (veiculoRepository.count() > 0) {
            log.info("[DEV] Veículos já existem, pulando.");
            return;
        }

        ClienteJpaEntity ana   = clienteRepository.findByCpf("52998224725").orElse(null);
        ClienteJpaEntity bruno = clienteRepository.findByCpf("11144477735").orElse(null);
        ClienteJpaEntity carla = clienteRepository.findByCpf("45317828791").orElse(null);

        if (ana == null || bruno == null || carla == null) {
            log.warn("[DEV] Clientes não encontrados, veículos não serão criados.");
            return;
        }

        veiculoRepository.save(veiculo("ABC1234", "Toyota",     "Corolla", 2020, "Branco",  ana));
        veiculoRepository.save(veiculo("XYZ5678", "Honda",      "Civic",   2022, "Preto",   ana));
        veiculoRepository.save(veiculo("DEF9012", "Ford",       "Ka",      2019, "Vermelho",bruno));
        veiculoRepository.save(veiculo("GHI3456", "Volkswagen", "Gol",     2021, "Prata",   carla));

        log.info("[DEV] 4 veículos criados: ABC1234, XYZ5678, DEF9012, GHI3456");
    }

    private void seedUsuarios(ClienteJpaEntity ana) {
        if (usuarioRepository.count() > 0) {
            log.info("[DEV] Usuários já existem, pulando.");
            return;
        }

        String anaClienteId = ana != null ? ana.getId() : null;

        usuarioRepository.save(usuario("Admin Sistema",    "admin@oficina.com",     "admin123",     Perfil.ADMIN,     null));
        usuarioRepository.save(usuario("Mecânico João",    "mecanico@oficina.com",  "mecanico123",  Perfil.MECANICO,  null));
        usuarioRepository.save(usuario("Atendente Maria",  "atendente@oficina.com", "atendente123", Perfil.ATENDENTE, null));
        usuarioRepository.save(usuario("Ana (portal)",     "ana.portal@teste.com",  "cliente123",   Perfil.CLIENTE,   anaClienteId));

        log.info("[DEV] 4 usuários criados:");
        log.info("[DEV]   admin@oficina.com      / admin123     (ADMIN)");
        log.info("[DEV]   mecanico@oficina.com   / mecanico123  (MECANICO)");
        log.info("[DEV]   atendente@oficina.com  / atendente123 (ATENDENTE)");
        log.info("[DEV]   ana.portal@teste.com   / cliente123   (CLIENTE)");
    }

    // --- builders ---

    private ClienteJpaEntity cliente(String nome, String cpf, String email, String telefone) {
        ClienteJpaEntity e = new ClienteJpaEntity();
        e.setId(UUID.randomUUID().toString());
        e.setNome(nome);
        e.setCpf(cpf);
        e.setEmail(email);
        e.setTelefone(telefone);
        e.setDataCadastro(LocalDateTime.now());
        e.setAtivo(true);
        return e;
    }

    private VeiculoJpaEntity veiculo(String placa, String marca, String modelo,
                                     int ano, String cor, ClienteJpaEntity cliente) {
        VeiculoJpaEntity e = new VeiculoJpaEntity();
        e.setPlaca(placa);
        e.setMarca(marca);
        e.setModelo(modelo);
        e.setAno(ano);
        e.setCor(cor);
        e.setAtivo(true);
        e.setCliente(cliente);
        return e;
    }

    private UsuarioJpaEntity usuario(String nome, String email, String senha,
                                     Perfil perfil, String clienteId) {
        UsuarioJpaEntity e = new UsuarioJpaEntity();
        e.setId(UUID.randomUUID().toString());
        e.setNome(nome);
        e.setEmail(email);
        e.setSenha(passwordEncoder.encode(senha));
        e.setPerfil(perfil);
        e.setClienteId(clienteId);
        e.setDataCadastro(LocalDateTime.now());
        e.setDataAtualizacao(LocalDateTime.now());
        e.setAtivo(true);
        return e;
    }
}
