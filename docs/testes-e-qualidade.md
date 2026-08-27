# 🧪 Testes e qualidade

## Executar os testes

```bash
# todos os testes
./mvnw test

# um teste específico
./mvnw test -Dtest=VeiculoTest
./mvnw test -Dtest=CadastrarVeiculoUseCaseTest
./mvnw test -Dtest=VeiculoControllerTest
```

Os testes usam H2 em memória — não é necessário ter o PostgreSQL rodando.

## Estratégia de testes por módulo

Cada módulo tem sua estratégia de teste, respeitando os limites da Clean Architecture:

| Módulo | Tipo | Ferramenta | Spring? |
|---|---|---|---|
| `oficina-domain` | Unitário puro | JUnit 5 + AssertJ | Não |
| `oficina-application` | Unitário com mock de gateway | JUnit 5 + Mockito | Não |
| `oficina-adapters` (controller) | Slice | `@WebMvcTest` + MockMvc | Parcial |
| `oficina-adapters` (JPA) | Slice | `@DataJpaTest` + H2 | Parcial |
| `oficina-infrastructure` | E2E | `@SpringBootTest` + H2 | Completo |

> **Regra:** testes em `oficina-application` nunca importam classes de `oficina-adapters`
> ou `oficina-infrastructure`. Mocke o gateway, não o repositório JPA.

## Testes manuais e E2E com Bruno

A coleção do [Bruno](https://www.usebruno.com/) está na pasta `src/test/bruno/`, contendo requisições organizadas por recurso (`auth/`, `cliente/`, `veiculo/`, `ordemservico/`, etc.) e fluxos E2E como o `caminho-feliz/` e `caminho-insumo-insuficiente/`.

### 1. Execução via Interface Gráfica (Bruno App)

1. Abra o Bruno e abra a pasta da coleção `src/test/bruno`.
2. No canto superior direito, selecione o environment **`local`**.
3. Para rodar o fluxo completo: clique com o botão direito na pasta **`caminho-feliz`** e selecione **Run** (ou **Run Folder**).
4. O fluxo gera automaticamente dados de cliente (CPF válido, nome, email, telefone) e dados de veículo (placa Mercosul, marca, modelo, cor) a cada execução, encadeando todas as etapas desde o login até a entrega do veículo.

### 2. Execução via Linha de Comando (Bruno CLI)

> ⚠️ **Importante:** O comando deve ser executado a partir do diretório onde está o `bruno.json` (`src/test/bruno`), caso contrário ocorrerá o erro `You can run only at the root of a collection`.

```bash
# Acessar a raiz da coleção
cd src/test/bruno

# Executar o caminho feliz
npx @usebruno/cli run caminho-feliz --env local
```

### 3. Execução em Lote (múltiplos clientes/veículos)

Para popular a base ou testar o fluxo de ponta a ponta múltiplas vezes de forma sequencial:

**PowerShell (Windows):**
```powershell
cd src/test/bruno
1..5 | ForEach-Object {
    Write-Host "`n================ Execução $_ ================" -ForegroundColor Cyan
    npx @usebruno/cli run caminho-feliz --env local
}
```

**Bash (Linux / macOS):**
```bash
cd src/test/bruno
for i in {1..5}; do
    echo -e "\n================ Execução $i ================"
    npx @usebruno/cli run caminho-feliz --env local
done
```

## Cobertura de testes (JaCoCo)

```bash
mvn clean test jacoco:report
```

![Relatório JaCoCo](assets/report_jacoco.png)

## Análise de qualidade e segurança (SonarQube)

Suba o SonarQube local:

```bash
docker run -d --name sonarqube -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true -p 9000:9000 sonarqube:latest
```

Depois siga o passo a passo em
<https://docs.sonarsource.com/sonarqube-community-build/try-out-sonarqube>.

![Relatório SonarQube](assets/report_sonarqube.jpeg)
