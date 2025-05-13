## Usa uma imagem base leve do OpenJDK 17
#FROM openjdk:17-jdk-slim
## Define o diretorio de trabalho dentro do container
#WORKDIR /app
## Copia o arquivo JAR da aplicacao para dentro do container
#COPY target/upload-file-gateway-0.0.1-SNAPSHOT.jar app.jar
## Expoe a porta que o servico usara
#EXPOSE 8080
## Baixar o script wait-for-it.sh
#RUN apt-get update && apt-get install -y curl \
#    && curl -o /usr/local/bin/wait-for-it.sh https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh \
#    && chmod +x /usr/local/bin/wait-for-it.sh
## Comando para aguardar a disponibilidade do upload-service na rede Docker
#ENTRYPOINT ["/usr/local/bin/wait-for-it.sh", "upload-service:8081", "--", "java", "-jar", "app.jar"]
# Usa uma imagem base leve do OpenJDK 17
FROM openjdk:17-jdk-slim
# Atualiza os pacotes e instala o curl
RUN apt-get update && apt-get install -y \
    curl iputils-ping telnet net-tools \
    && rm -rf /var/lib/apt/lists/*
# Define o diretório de trabalho
WORKDIR /app
# Copia o JAR da aplicação
COPY target/upload-file-gateway-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/application.yml /app/application.yml
# Copia o script de espera e torna executável
COPY wait-for-services.sh /wait-for-services.sh
RUN chmod +x /wait-for-services.sh
# Exponha a porta usada pelo Spring Boot
EXPOSE 8080
# Entrypoint para iniciar o script
ENTRYPOINT ["/wait-for-services.sh", "--spring.config.location=/app/application.yml"]
