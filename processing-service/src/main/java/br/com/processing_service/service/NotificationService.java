package br.com.processing_service.service;

import br.com.processing_service.model.NotificationMessage;
import br.com.processing_service.model.ProcessedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final RabbitTemplate rabbitTemplate;

    public NotificationService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public Mono<Void> send(String message, String queue, Object event) {
        return Mono.fromRunnable(() -> rabbitTemplate.convertAndSend(queue, event))
                .doOnSuccess(unused -> logger.info("Notificação enviada: {}", message))
                .doOnError(e -> logger.error("Erro ao enviar a notificação: {}", e.getMessage()))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    public Mono<Void> sendToTopic(FanoutExchange exchange, ProcessedFile processedFile) {
        return Mono.fromRunnable(() -> rabbitTemplate.convertAndSend(exchange.getName(), "", processedFile))
                .doOnSuccess(o -> logger.info("Mensagem enviada para o tópico {}: {}", exchange.getName(), processedFile))
                .doOnError(e -> logger.error("Erro ao enviar para tópico: {}", e.getMessage()))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
