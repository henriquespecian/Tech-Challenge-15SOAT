#################################################################
# Stage 1: Build
# Baixa todas as dependências e compila o projeto, gerando o .jar
#################################################################

# O jdk é só pra aopntar que a imagem é com compilação
FROM eclipse-temurin:25-jdk AS build

# Define o diretório padrão para as opreções seguintes
WORKDIR /app

# Configuração do Maven Wrapper para baixar as dependências do projeto
COPY .mvn/ .mvn/

# Copia os arquivos pom.xml que possuem as dependências de cada projeto
COPY mvnw pom.xml ./
COPY ./oficina-domain/pom.xml ./oficina-domain/
COPY ./oficina-application/pom.xml ./oficina-application/
COPY ./oficina-adapters/pom.xml ./oficina-adapters/
COPY ./oficina-infrastructure/pom.xml ./oficina-infrastructure/

# Roda o Maven e baixa as dependências do projeto primeiro deixando pronto para buildar
# -B significa o modo batch, sem interação com usuário
RUN ./mvnw dependency:go-offline -B

# Copia todo o código fonte para dentro do container 
COPY ./oficina-domain/src ./oficina-domain/src
COPY ./oficina-application/src ./oficina-application/src
COPY ./oficina-adapters/src ./oficina-adapters/src
COPY ./oficina-infrastructure/src ./oficina-infrastructure/src

# Roda o Maven, limpando o projeto e gerando o arquivo .jar, pulando os testes
RUN ./mvnw clean package -DskipTests -B

#################################################################
# Stage 2: Runtime
# Copia o .jar gerado e roda a aplicação usando uma imagem mais leve
#################################################################

# Somente o runtime, não precisa do compilador (JDK), é mais leve. Roda em alpine para ser ainda mais leve
FROM eclipse-temurin:25-jre-alpine

# Define novamente a workdir
WORKDIR /app

# Copia o .jar gerado na etapa anterior par ao diretório de runtime
# O .jar é gerado dentro do infrastructure
COPY --from=build /app/oficina-infrastructure/target/oficina-infrastructure-*.jar app.jar

# 1. Baixa o agente do open telemetry
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

# 2. Garante as permissões de leitura
RUN chmod 644 /app/opentelemetry-javaagent.jar

#Expondo a porta 8080 para acessar a aplicação
EXPOSE 8080

# Define o comando de entrada para rodar a aplicação, usando o .jar copiado junto com o agente do opentelemetry
ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-jar", "app.jar"]