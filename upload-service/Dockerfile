# Base image com OpenJDK para rodar a aplicação
FROM openjdk:17-jdk-slim
# Atualiza os pacotes e instala o curl e net-tools
RUN apt-get update && apt-get install -y \
        curl iputils-ping telnet net-tools netcat \
        && rm -rf /var/lib/apt/lists/*
# Define o diretório de trabalho no container
WORKDIR /app
# Expoe a porta que o servico usara
EXPOSE 8081
# Copia o arquivo JAR gerado pelo Maven para o container
COPY target/upload-service-0.0.1-SNAPSHOT.jar app.jar
# Copia o script de espera e torna executável
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
# Configura o comando para iniciar o serviço
ENTRYPOINT ["/entrypoint.sh"]
