FROM openjdk:17-jdk-slim

RUN apt-get update && apt-get install -y \
        curl netcat \
        && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY target/processing-service-0.0.1-SNAPSHOT.jar app.jar

COPY src/main/resources/application.yml /app/application.yml

COPY entrypoint.sh /entrypoint.sh

RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
