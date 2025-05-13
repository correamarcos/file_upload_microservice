#!/bin/sh

echo "Esperando upload-service..."
until curl -s http://upload-service:8081/actuator/health | grep '"status":"UP"' > /dev/null; do
  echo "Aguardando upload-service ficar UP..."
  sleep 2
done

echo "Upload-service está UP! Iniciando Gateway..."
exec java -Djava.net.preferIPv4Stack=true -jar /app/app.jar
