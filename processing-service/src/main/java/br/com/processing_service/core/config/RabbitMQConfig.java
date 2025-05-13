package br.com.processing_service.core.config;

import br.com.processing_service.core.properties.RabbitMQProperties;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);
    private final RabbitMQProperties rabbitMQProperties;
    private final String exchange;

    public RabbitMQConfig(RabbitMQProperties rabbitMQProperties,
                          @Value("${spring.rabbitmq.template.exchange}") String exchange) {
        this.rabbitMQProperties = rabbitMQProperties;
        this.exchange = exchange;
    }

    @Bean
    FanoutExchange fileProcessedExchange() {
        return new FanoutExchange(exchange);
    }

    @Bean
    public Queue fileProcessingQueue() {
        logger.info("Configurando fila: {}", rabbitMQProperties.getProcessing());
        return new Queue(rabbitMQProperties.getProcessing(), true);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }

    @PostConstruct
    public void logRabbitMQSettings() {
        logger.info("RabbitMQ Queue Configurada: {}", rabbitMQProperties.getProcessing());
    }
}
