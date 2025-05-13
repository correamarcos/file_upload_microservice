package br.com.processing_service.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.rabbitmq.queues")
public class RabbitMQProperties {
    private String processing;
    private String notification;
    private String processed;

    public String getProcessing() {
        return processing;
    }

    public void setProcessing(String processing) {
        this.processing = processing;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getProcessed() { return processed; }

    public void setProcessed(String processed) { this.processed = processed; }
}
