package br.com.uploadService.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.rabbitmq.queues")
public class RabbitMQProperties {
    private String processing;
    private String processed;
    private String notification_failure;

    public String getProcessing() {
        return processing;
    }

    public void setProcessing(String processing) {
        this.processing = processing;
    }

    public String getProcessed() {
        return processed;
    }

    public void setProcessed(String processed) {
        this.processed = processed;
    }

    public String getNotification_failure() {
        return notification_failure;
    }

    public void setNotification_failure(String notification_failure) {
        this.notification_failure = notification_failure;
    }
}
