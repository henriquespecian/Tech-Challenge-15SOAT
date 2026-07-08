# 💻 Execução local

Como subir a Oficina API na sua máquina — via **Docker Compose** (mais simples) ou em um cluster **Minikube** (para exercitar os manifestos Kubernetes).

---

## Opção A — Docker Compose

### Pré-requisitos
- Docker e Docker Compose
- `make` (opcional, mas recomendado)

### Com Make (recomendado)

| Comando | Descrição |
|---|---|
| `make start` | Limpa tudo, reconstrói a imagem e sobe a aplicação com logs no terminal |
| `make dev` | Sobe apenas o PostgreSQL (para rodar a aplicação pela IDE) |

### Sem Make

```bash
docker-compose up --build
```

Isso cria os containers da aplicação e do PostgreSQL:
- API: `http://localhost:8080`
- Banco: `localhost:5432` — `oficina_db` / `postgres` / `123`

Com a aplicação no ar, o Swagger fica em `http://localhost:8080/swagger-ui.html`.
Sobre login e perfis, ver [autenticacao-e-perfis.md](autenticacao-e-perfis.md).

---

## Opção B — Minikube (Kubernetes local)

A aplicação usa **Kustomize** para reaproveitar os manifestos: um `base/` comum e dois
overlays (`minikube` e `aws`) com apenas as diferenças de cada ambiente.

```
k8s/
  base/                 # Deployment, Service, HPA, ConfigMap comuns
  overlays/
    minikube/           # Postgres in-cluster + NodePort + imagem Docker Hub
    aws/                # RDS (via Terraform) + LoadBalancer + imagem ECR
```

> 🔐 Os `secret.yaml` reais estão no `.gitignore`. Cada overlay tem um
> `secret.example.yaml` versionado como modelo. Copie e preencha antes de aplicar.

### Pré-requisitos
- Docker, Minikube e `kubectl` instalados.

### Passos

**1. Suba o Minikube e o metrics-server (necessário para o HPA):**
```bash
minikube start
minikube addons enable metrics-server
```

**2. Aponte o kubectl para o cluster local e confirme:**
```bash
kubectl config use-context minikube
kubectl config current-context   # deve dizer: minikube
```

**3. Crie o Secret local a partir do modelo:**
```bash
cp k8s/overlays/minikube/secret.example.yaml k8s/overlays/minikube/secret.yaml
# edite e preencha os valores base64:
#   echo -n 'senha-do-postgres-local' | base64
#   echo -n 'seu-jwt-secret'          | base64
```

**4. (Opcional) Valide o manifesto final sem aplicar:**
```bash
kubectl kustomize k8s/overlays/minikube
```

**5. Aplique e acompanhe:**
```bash
kubectl apply -k k8s/overlays/minikube
kubectl get pods -w        # aguarde os dois pods 1/1 Running
```

**6. Acesse a API (abre um túnel; mantenha o terminal aberto):**
```bash
minikube service oficina-api --url
```
Use a URL retornada + `/swagger-ui/index.html`.

> Para ter o usuário admin e dados de exemplo, o `configmap-local.yaml` deve conter
> `SPRING_PROFILES_ACTIVE: dev` — o seed (`DevDataLoader`) só roda no profile `dev`.
> As credenciais dos usuários de exemplo estão em [autenticacao-e-perfis.md](autenticacao-e-perfis.md).

**7. Verifique o autoscaler:**
```bash
kubectl get hpa
```

---

## Troubleshooting (local)

| Sintoma | Causa provável | Correção |
|---|---|---|
| `dial tcp 127.0.0.1:xxxxx: connection refused` | Minikube parado | `minikube start` |
| `kubectl top pods` diz `Metrics API not available` | metrics-server não habilitado/pronto | `minikube addons enable metrics-server` e aguarde 1-2 min |
| Overlay aplicado no cluster errado | contexto do kubectl trocado | `kubectl config current-context` e ajuste antes do apply |
| HPA mostra `<unknown>` em TARGETS | metrics-server ainda não coleta | aguarde o metrics-server ficar pronto |
| Login não retorna token | banco vazio (profile sem seed) | garanta `SPRING_PROFILES_ACTIVE: dev` no `configmap-local.yaml` |

> No driver Docker do Minikube é comum o metrics-server não validar o certificado do kubelet.
> Nesse caso, aplique o patch:
> ```bash
> kubectl -n kube-system patch deployment metrics-server --type=json \
>   -p='[{"op":"add","path":"/spec/template/spec/containers/0/args/-","value":"--kubelet-insecure-tls"}]'
> ```

Para deploy na AWS (EKS + RDS), ver [deploy-aws.md](deploy-aws.md).
