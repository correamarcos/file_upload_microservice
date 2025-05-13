#!/bin/bash

echo "? Aguardando RabbitMQ..."
while ! nc -z rabbitmq 5672; do
  sleep 1
done

echo "? RabbitMQ est� pronto!"

# Inicia a aplica��o
exec java -Djava.net.preferIPv4Stack=true -jar /app/app.jar
