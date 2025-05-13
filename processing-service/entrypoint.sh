#!/bin/bash

echo "? Aguardando RabbitMQ..."
while ! nc -z rabbitmq 5672; do
  sleep 1
done

echo "? RabbitMQ está pronto!"

# Inicia a aplicação
exec java -Djava.net.preferIPv4Stack=true -jar /app/app.jar
