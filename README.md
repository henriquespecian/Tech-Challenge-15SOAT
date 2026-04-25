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

- Java 25+
- Docker e Docker Compose

## Como executar

**1. Suba o banco de dados:**

```bash
docker-compose up -d
```

Isso cria um container PostgreSQL com as seguintes configurações:
- Host: `localhost:5432`
- Banco: `oficina_db`
- Usuário: `postgres` / Senha: `123`

**2. Execute a aplicação:**

```bash
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

## Endpoints

| Método | Rota | Descrição |
|--------|------|-----------|
| `POST` | `/cliente` | Cadastra um novo cliente |
| `GET` | `/cliente/{idCliente}` | Busca cliente por ID |

A documentação interativa (Swagger UI) está disponível em:

```
http://localhost:8080/swagger-ui.html
```

## Build e testes

```bash
# Executar testes
./mvnw test

# Gerar JAR
./mvnw clean package

# Executar o JAR gerado
java -jar target/oficina-api-0.0.1-SNAPSHOT.jar
```

## Licença

Distribuído sob a licença [MIT](https://opensource.org/licenses/MIT).
