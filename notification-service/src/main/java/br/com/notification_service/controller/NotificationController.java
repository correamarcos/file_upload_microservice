package br.com.notification_service.controller;

import br.com.notification_service.model.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    private final Sinks.Many<NotificationMessage> sink = Sinks.many().multicast().onBackpressureBuffer();

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<NotificationMessage> streamNotifications() {
        return sink.asFlux();
    }

    @RabbitListener(queues = "${spring.rabbitmq.queue}")
    public void processNotification(NotificationMessage notificationMessage) {
        logger.info("Sending notification");
        sink.tryEmitNext(notificationMessage);
    }
}
