# Adicionar Open Telemetry + New Relic

## Resumo
 A fim de sanar os requistios da fase 3 do projeto da Pós Tech, é necessário adicionar ferramentas de Monitoramento e Observalidade, sendo assim foi decidio intrumentalizar agnósticamente com Open Telemetry e New Relic para o armazenamento e vizualiação dos dados

## Problema
O motivo é monitorar o ambiente e detectar gargalos em tempo real

## Proposta técnica
- Instrumentação: Utilização do Java Agent do OpenTelemetry injetado via Dockerfile. Esta abordagem permite a auto-instrumentação (captura automática de logs, métricas e traces) sem a necessidade de alterações significativas no código-fonte Java/Maven.


- Protocolo: Exportação de dados via OTLP/Protobuf através da porta 4318 (HTTP), garantindo compatibilidade com redes externas e firewalls.


- Back-end: Utilização do New Relic como receptor de dados, utilizando as credenciais de Ingest License Key gerenciadas via GitHub Organization Secrets para evitar exposição de chaves.


- Ambiente de Desenvolvimento: Uso de Docker Compose com variáveis de ambiente injetadas via arquivo .env (ignorado pelo Git).

## Impacto esperado

- Visibilidade completa do rastreamento da requisição
- OpenTelemetry agnóstico, sendo possível plugar ele em outros fornecedores como o DataDog
- Baixo acoplamento do código, já que nenhuma nova dependência foi inserida

## Alternativas consideradas
- Utilizar o próprio agente do New Relic, foi descartado pela auta acoplação com o fornecedor
- Intrumentalizaçaõ via SDK, foi descartada pela melhor alternativa de não criar mais dependências no projeto


## Pontos em aberto:
- Estratégias de retenção dos logs
- Como vai ser o acesso das outras pessoas do grupo, já que a key do New Relic está associada com o meu usuário.