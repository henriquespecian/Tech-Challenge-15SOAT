# Tech Challenge 15SOAT — Oficina API

API REST para gerenciamento de clientes e veículos de uma oficina mecânica, desenvolvida como Tech Challenge da pós-graduação SOAT (Software Architecture) da FIAP.

## Tecnologias

- Java 25
- Spring Boot 4.0.5
- Spring Data JPA + Hibernate
- PostgreSQL 15
- OpenAPI/Swagger (springdoc-openapi 2.8.3)
- Docker / Docker Compose

## Arquitetura

O projeto segue os princípios de **Clean Architecture** e **Domain-Driven Design (DDD)**:

```
src/main/java/com/mecanica/oficina_api/
├── domain/          # Regras de negócio e value objects (Cpf, Email, Telefone, Veiculo)
├── application/     # Casos de uso (ClienteService)
├── infrastructure/  # Persistência JPA
└── interfaces/      # Controllers REST e DTOs
```

## Por Que PostgreSQL?

PostgreSQL é a escolha ideal para este projeto por:

1. **ACID + Integridade Referencial**
   - Garante consistência de dados críticos (clientes, veículos, usuários)
   - Foreign keys previnem dados órfãos automaticamente

2. **Relacionamentos Entre Agregados**
   - Suporta as referências
   - Constraints validam relacionamentos em tempo de banco

3. **Otimização de Soft Deletes**
   - Índices compostos aceleram queries que filtram por `ativo = true`
   - Melhor performance em operações lógicas de exclusão

4. **Compatibilidade com Spring Boot + JPA/Hibernate**
   - Suporte nativo para tipos avançados (UUID, arrays, JSON)
   - Queries customizadas funcionam sem limitações
   - Migrações são diretas e previsíveis

5. **Auditoria Nativa**
   - Triggers podem auto-manter `dataCadastro` e `dataAtualizacao` (se expandido)
   - Suporta histórico de alterações em implementações futuras

6. **Escalabilidade e Confiabilidade**
   - Adequado para crescimento de dados (centenas/milhares de clientes)
   - Backup, restore e replicação são operações padrão
   - Comunidade ativa e bem documentado

7. **Padrão da Indústria**
   - Escolha padrão em sistemas enterprise
   - Educativamente realista para um Tech Challenge
   - Gratuito (open source)

PostgreSQL evita problemas comuns de bancos menos robustos.

## Pré-requisitos

- Docker e Docker Compose
- `make` (opcional, mas recomendado)

## Como executar

### Com Make (recomendado)

```bash
make start
```

Isso remove tudo que existia, reconstrói a imagem e sobe a aplicação com os logs no terminal.

| Comando | Descrição |
|---|---|
| `make start` | Limpa tudo e sobe do zero com logs |
| `make dev` | Sobe apenas o PostgreSQL (para rodar a aplicação pela IDE) |

### Sem Make

**1. Suba tudo:**

```bash
docker-compose up --build
```

Isso cria os containers da aplicação e do PostgreSQL:
- API: `http://localhost:8080`
- Banco: `localhost:5432` — `oficina_db` / `postgres` / `123`

## Endpoints
A documentação interativa (Swagger UI) está disponível em:

```
http://localhost:8080/swagger-ui.html
```

## Autenticação

A API usa JWT stateless. O `DevDataLoader` cria automaticamente um usuário ADMIN ao subir em perfil de desenvolvimento:

- **Email:** `admin@email.com`
- **Senha:** `123456`

Fluxo:
1. `POST /auth/login` com `{ "email": "...", "senha": "..." }` → retorna `token`
2. Nos demais requests: header `Authorization: Bearer <token>`

## Perfis de usuário

| Perfil | Acesso |
|---|---|
| `ADMIN` | Acesso total — gerencia usuários, serviços, insumos e todas as operações |
| `ATENDENTE` | Cadastra e consulta clientes/veículos, abre e acompanha ordens de serviço |
| `MECANICO` | Consulta e executa ordens de serviço, registra peças e serviços realizados |
| `CLIENTE` | Consulta apenas as próprias ordens de serviço |

## Testes

### Executar todos os testes

```bash
./mvnw test
```

### Testando com Bruno

As coleções Bruno estão na pasta `bruno/` do projeto, organizadas por recurso.

#### Como usar

1. Instale o [Bruno](https://www.usebruno.com/)
2. Abra o Bruno e importe a pasta `bruno/` do projeto
3. Selecione o environment `local`
4. Execute o request **Auth > Login** — o token JWT será salvo automaticamente no environment
5. Todos os demais requests já usam `{{token}}` e estarão prontos para uso

### Estrutura de testes

Os testes seguem as mesmas camadas da aplicação:

```
src/test/java/com/mecanica/oficina_api/
├── domain/veiculo/
│   └── VeiculoTest.java          # Validações de domínio (sem Spring)
├── application/veiculo/
│   └── VeiculoServiceTest.java   # Lógica de negócio com Mockito
└── interfaces/
    └── VeiculoControllerTest.java # HTTP (status codes, JSON) com MockMvc
```

## Relatório de vulnerabilidade


### Análise de qualidade, segurança, confiabilidade e manutenibilidade

```
docker run -d --name sonarqube -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true -p 9000:9000 sonarqube:latest
```

Seguir o passo a passo disponível em:

https://docs.sonarsource.com/sonarqube-community-build/try-out-sonarqube

### Relatório de cobertura de testes

```
mvn clean test jacoco:report
```


## Licença

Distribuído sob a licença [MIT](https://opensource.org/licenses/MIT).
