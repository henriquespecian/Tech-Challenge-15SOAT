# AGENTS.md — Quick Start for AI Coding Agents

This file provides project-specific guidance to help AI agents be immediately productive in the **Oficina API** codebase.

## Quick Reference

**Tech Stack**: Java 25, Spring Boot 4.0.5, Spring Data JPA, PostgreSQL 15, Docker/Compose

**Architecture**: Clean Architecture + Domain-Driven Design (DDD)  
**Key Principle**: Immutable value objects + factory methods enforce domain invariants

---

## 1. Build, Test & Run Commands

```bash
# Start everything (clean, rebuild, up with logs)
make start

# Or without make:
docker-compose up --build

# Run all tests
./mvnw test

# Build without tests
./mvnw clean package -DskipTests
```

**Services after startup:**
- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- PostgreSQL: `localhost:5432` (oficina_db / postgres / 123)

See [README.md](README.md) for full documentation.

---

## 2. Architecture Overview

```
src/main/java/com/mecanica/oficina_api/
├── domain/          # Business rules, entities, value objects (Cpf, Email, Telefone)
├── application/     # Service layer (ClienteService, UsuarioService)
├── infrastructure/  # Persistence (JPA entities, Spring Data repositories)
└── interfaces/      # Controllers, DTOs (request/response)
```

**Layer interaction**: Controllers → Services → Domain Models → Repositories

---

## 3. Domain-Specific Patterns

### ✅ Value Objects Validate Themselves

`Cpf`, `Email`, `Telefone` enforce invariants in constructors. **You cannot create invalid instances.**

```java
// ✅ Correct: Throws exception if invalid
Email email = new Email("user@example.com");

// ❌ Never do this: Direct setters don't exist
// email.setValor("invalid");  // Not available
```

When working with these classes:
- Use constructor-based validation
- Trust that valid instances remain valid (immutable)
- Add validation tests for edge cases in `domain/*/` test directory

### ✅ Factory Methods Prevent Invalid State

Domain models use `.criar()` static factory methods instead of public constructors to enforce domain rules.

```java
// ✅ Correct: Uses factory method
Veiculo veiculo = Veiculo.criar(placa, marca, modelo, ano);

// ❌ Never do this: Direct new
// Veiculo v = new Veiculo();  // Violates domain invariants
```

**Pattern**: Look for `public static [Entity].criar(...)` methods in `domain/` classes before implementing creation logic.

### ✅ Service Layer Handles Orchestration

Services (`ClienteService`, `UsuarioService`, `VeiculoService`) coordinate domain logic and persistence:
- Call domain factories to create/modify entities
- Use repositories for persistence
- Throw domain exceptions for business rule violations

Services are **not** thin wrappers—they enforce use cases.

### ✅ Soft Deletes via `ativo` Flag

Entities use logical deletion (`ativo = false`), not physical deletion. **All repository queries automatically filter by `ativo=true`.**

When implementing new repositories:
- Add queries that implicitly filter `WHERE ativo = true`
- Never expose methods that bypass this filter
- Mark deletion methods as "logical delete" that set `ativo=false`

---

## 4. Naming Conventions (Portuguese Native)

The codebase uses **Portuguese naming intentionally** for a Brazilian Tech Challenge context. This is not an oversight.

**Packages & Classes**:
- `cliente`, `usuario`, `veiculo` (entities)
- `ClienteService`, `UsuarioService` (services)

**DTOs**:
- Request: `[Verbo][Entidade]Request` — `CadastrarClienteRequest`, `AlterarVeiculoRequest`
- Response: `[Consultar|Listar][Entidade]Response` — `ConsultarClienteResponse`, `VeiculoResponse`

**Test Method Names**:
- Pattern: `deve[X]Quando[Y]()` — `deveRetornarClienteQuandoValido()`, `deveLanguarExcecaoQuandoEmailInvalido()`

**Variables**: Use Portuguese names (`cliente`, `veiculo`, `email`) throughout.

---

## 5. Manual Mapping (No MapStruct/Lombok)

Entity-to-DTO mapping is **explicit and manual** — no abstractions like MapStruct or Lombok.

**Why**: Pedagogical clarity in a Tech Challenge context. Mapping is visible and intentional.

When adding DTOs:
- Write plain `toResponse()` methods in services or dedicated mappers
- Map field-by-field explicitly
- Don't hide transformations behind annotations
- Test mapping thoroughly in controller tests

---

## 6. Test Structure

Mirror the architecture in tests:

```
src/test/java/com/mecanica/oficina_api/
├── domain/*/        # Pure unit tests (no Spring, no DB)
├── application/*/   # Service tests with Mockito
└── interfaces/*/    # Controller tests with MockMvc
```

**Test Levels**:
- **Domain tests** (`VeiculoTest`): Test value objects & entities in isolation
- **Service tests** (`VeiculoServiceTest`): Mock repositories, test business logic
- **Controller tests** (`VeiculoControllerTest`): Test HTTP layer, status codes, JSON marshaling

Each layer tests only its responsibility.

---

## 7. Common Pitfalls & How to Avoid Them

| Pitfall | Solution |
|---------|----------|
| Using `new Cpf()` or `new Email()` without validation | Use constructors; they validate. Trust invalid instances can't exist. |
| Bypassing `.criar()` factory methods | Always use factories on domain models. They enforce rules. |
| Trying to add Lombok or MapStruct | Don't—explicit mappings & constructors are intentional. |
| Allowing physical deletes | Implement logical deletes via `ativo` flag. Filter in queries. |
| Adding business logic in Controllers | Services handle orchestration. Controllers only map & delegate. |
| Missing soft-delete filtering in new queries | New repository methods must filter `ativo=true` implicitly. |

---

## 8. File Layout Reference

**Most frequently edited:**
- Domain rules: `src/main/java/.../domain/[entity]/`
- Business logic: `src/main/java/.../application/[entity]/[Entity]Service.java`
- HTTP contracts: `src/main/java/.../interfaces/[Entity]Controller.java` & DTOs
- Tests: `src/test/java/.../` (mirror structure)

**One-time setup:**
- Persistence: `src/main/java/.../infrastructure/persistence/`
- App config: `src/main/java/.../config/` (e.g., `DevDataLoader.java`)

---

## 9. Before Starting Any Task

1. **Understand the use case first** — Read domain classes to see what rules must hold.
2. **Check existing patterns** — Look for similar entities (Cliente, Veiculo) as examples.
3. **Test-first mindset** — Write domain/service tests before implementation.
4. **Preserve Portuguese naming** — Don't rename to English; it's intentional.
5. **Trust immutability** — Value objects won't break; you can't create invalid ones.

---

## 10. Related Documentation

- [README.md](README.md) — Architecture overview, build commands, endpoints
- [Makefile](Makefile) — Available development commands
- [docker-compose.yaml](docker-compose.yaml) — Service configuration
- [pom.xml](pom.xml) — Dependencies & build configuration
- [CLAUDE.md](CLAUDE.md) — General behavioral guidelines for AI coding

---

**For questions**: Read the domain classes first—they document invariants better than comments.
