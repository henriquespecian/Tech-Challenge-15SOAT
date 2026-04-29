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

## Testes

### Executar todos os testes

```bash
./mvnw test
```

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


## Licença

Distribuído sob a licença [MIT](https://opensource.org/licenses/MIT).
