# Tech Challenge 15SOAT — Oficina API

API REST para gerenciamento de clientes e veículos de uma oficina mecânica, desenvolvida como Tech Challenge da pós-graduação SOAT (Software Architecture) da FIAP.

## Tecnologias

- Java 25
- Spring Boot 4.1.0
- Spring Data JPA + Hibernate
- Spring Security + JWT
- PostgreSQL 15
- OpenAPI/Swagger (springdoc-openapi 2.8.3)
- JaCoCo (cobertura de testes)
- Maven multi-módulo
- Docker / Docker Compose

## Arquitetura

O projeto é um **Maven multi-módulo** que segue **Clean Architecture** pura. As dependências fluem sempre de fora para dentro:

```
oficina-infrastructure → oficina-adapters → oficina-application → oficina-domain
```

| Módulo | Responsabilidade |
|---|---|
| **`oficina-domain`** | Entidades, value objects (`Cpf`, `Email`, `Telefone`) e regras de negócio. Zero dependência de framework. |
| **`oficina-application`** | Casos de uso (`XxxUseCase`) e interfaces de gateway. Conhece apenas o domínio. |
| **`oficina-adapters`** | Implementações de gateway (JPA), controllers REST, DTOs e wiring dos beans (`ApplicationConfig`). |
| **`oficina-infrastructure`** | Ponto de entrada (`@SpringBootApplication`), configuração de segurança e Spring. |

Regras obrigatórias: nenhum módulo interno importa o externo; a camada `application` usa **use cases** (não services); regras de negócio vivem no domínio; DTOs não entram no domínio.

## Funcionalidades

A API cobre os seguintes domínios:

- **Clientes** — cadastro, consulta, alteração e exclusão
- **Veículos** — cadastro e gestão, vinculados a clientes
- **Usuários** — gestão de usuários e perfis de acesso
- **Serviços** — catálogo de serviços com tempo médio de execução
- **Insumos** — controle de estoque, compras e movimentação
- **Ordens de Serviço** — fluxo completo com orçamento e ciclo de vida de status:

```
RECEBIDA → EM_DIAGNOSTICO → AGUARDANDO_APROVACAO → EM_EXECUCAO → FINALIZADA → ENTREGUE
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

## Executando no Kubernetes (Minikube)

A aplicação também pode ser orquestrada com Kubernetes. Os manifestos estão na pasta [`k8s/`](k8s/) e contemplam Deployments, Services, ConfigMap/Secret, PersistentVolumeClaim e Horizontal Pod Autoscaler (HPA).

### Pré-requisitos

- [Minikube](https://minikube.sigs.k8s.io/) instalado e em execução
- `kubectl` configurado para o cluster do Minikube
- Docker (para build/push da imagem, caso vá alterar o código)

### O que há na pasta `k8s/`

| Arquivo | Objeto | Papel |
|---|---|---|
| `configmap.yaml` | ConfigMap | Configuração não sensível (URL do banco, usuário, profile) |
| `secret.yaml` | Secret | Senha do banco e segredo do JWT (base64) |
| `postgres-pvc.yaml` | PersistentVolumeClaim | Disco persistente do PostgreSQL |
| `postgres-deployment.yaml` | Deployment | Container do PostgreSQL |
| `postgres-service.yaml` | Service (ClusterIP) | Nome interno `postgres` para a aplicação encontrar o banco |
| `app-deployment.yaml` | Deployment | Aplicação Spring Boot, com `requests/limits` e probes |
| `app-service.yaml` | Service (NodePort) | Expõe a API para fora do cluster |
| `app-hpa.yaml` | HorizontalPodAutoscaler | Escala a aplicação conforme CPU/memória |

### 1. Suba o Minikube

```bash
minikube start
```

### 2. Habilite o metrics-server (necessário para o HPA)

O HPA depende do `metrics-server` para ler o consumo de CPU/memória — ele **não vem habilitado** por padrão:

```bash
minikube addons enable metrics-server
```

Após ~1-2 minutos, valide a coleta de métricas:

```bash
kubectl top pods
```

> **Se `kubectl top pods` continuar retornando `Metrics API not available` após alguns minutos**, verifique se o Pod está pronto com `kubectl get pods -n kube-system | grep metrics-server`. No driver Docker do Minikube é comum o metrics-server não validar o certificado do kubelet — nesse caso, aplique:
>
> ```bash
> kubectl -n kube-system patch deployment metrics-server --type=json \
>   -p='[{"op":"add","path":"/spec/template/spec/containers/0/args/-","value":"--kubelet-insecure-tls"}]'
> ```

### 3. Aplique os manifestos

A imagem da aplicação já está publicada no Docker Hub (`henriquespecian/oficina-api`), então basta aplicar a pasta inteira:

```bash
kubectl apply -f k8s/
```

Acompanhe os Pods até ficarem `1/1 Running`:

```bash
kubectl get pods -w
```

> O Pod da aplicação leva alguns segundos em `0/1` enquanto o Spring Boot inicializa (a `startupProbe` cobre esse tempo). O Pod do PostgreSQL deve ficar `1/1` primeiro.

### 4. Acesse a API

No Minikube, abra o túnel para o Service da aplicação:

```bash
minikube service oficina-api --url
```

O comando devolve uma URL (ex.: `http://127.0.0.1:51234`) e abre um túnel. **No driver Docker (Windows), mantenha esse terminal aberto** — o túnel só funciona enquanto o comando estiver em execução, e a porta muda a cada vez que ele é executado.

Acesse o Swagger usando o caminho completo:

```
<URL>/swagger-ui/index.html
```

### 5. Verifique o autoscaler

```bash
kubectl get hpa
```

A coluna `TARGETS` deve mostrar o consumo atual de CPU e memória contra as metas (ex.: `cpu: 2%/70%, memory: 40%/80%`). Se aparecer `<unknown>`, o metrics-server ainda não está coletando (volte ao passo 2).

### (Opcional) Reconstruir a imagem

Caso altere o código da aplicação, reconstrua e publique a imagem com uma **nova tag**, depois atualize o campo `image:` em `k8s/app-deployment.yaml`:

```bash
docker build -t henriquespecian/oficina-api:<nova-tag> .
docker push henriquespecian/oficina-api:<nova-tag>
kubectl apply -f k8s/app-deployment.yaml
```

> Use sempre uma tag nova (nunca reaproveite a mesma) para garantir que o cluster faça o pull da versão atualizada em vez de usar a imagem em cache.

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

#### Rodar um teste específico

```bash
./mvnw test -Dtest=VeiculoTest
./mvnw test -Dtest=CadastrarVeiculoUseCaseTest
./mvnw test -Dtest=VeiculoControllerTest
```

### Estrutura de testes

Cada módulo tem sua estratégia de teste:

| Módulo | Tipo | Ferramenta | Spring? |
|---|---|---|---|
| `oficina-domain` | Unitário puro | JUnit 5 + AssertJ | Não |
| `oficina-application` | Unitário com mock de gateway | JUnit 5 + Mockito | Não |
| `oficina-adapters` (controller) | Slice | `@WebMvcTest` + MockMvc | Parcial |
| `oficina-adapters` (JPA) | Slice | `@DataJpaTest` + H2 | Parcial |
| `oficina-infrastructure` | E2E | `@SpringBootTest` + H2 | Completo |

Os testes usam H2 em memória — não é necessário ter o PostgreSQL rodando.

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
