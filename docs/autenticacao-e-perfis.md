# 🔐 Autenticação e perfis

A API usa **JWT stateless** via Spring Security. A autorização por endpoint fica nos
controllers com `@PreAuthorize`.

## Fluxo

1. `POST /auth/login` com `{ "email": "...", "senha": "..." }` → retorna um `token`.
2. Nos demais requests, envie o header `Authorization: Bearer <token>`.

## Usuário admin padrão (dev)

O `DevDataLoader` cria automaticamente um usuário ADMIN ao subir em perfil de
desenvolvimento (`SPRING_PROFILES_ACTIVE=dev`):

- **Email:** `admin@email.com`
- **Senha:** `123456`

> O seed só roda no profile `dev` e é idempotente (só insere se as tabelas estiverem vazias).

## Perfis de usuário

| Perfil | Acesso |
|---|---|
| `ADMIN` | Acesso total — gerencia usuários, serviços, insumos e todas as operações |
| `ATENDENTE` | Cadastra e consulta clientes/veículos, abre e acompanha ordens de serviço |
| `MECANICO` | Consulta e executa ordens de serviço, registra peças e serviços realizados |
| `CLIENTE` | Consulta apenas as próprias ordens de serviço |

## Usuários de exemplo (seed nos ambientes Kubernetes)

Quando o seed roda em Minikube/EKS, estes usuários ficam disponíveis:

| Perfil | E-mail | Senha |
|---|---|---|
| ADMIN | `admin@oficina.com` | `admin123` |
| MECANICO | `mecanico@oficina.com` | `mecanico123` |
| ATENDENTE | `atendente@oficina.com` | `atendente123` |
| CLIENTE | `ana.portal@teste.com` | `cliente123` |
