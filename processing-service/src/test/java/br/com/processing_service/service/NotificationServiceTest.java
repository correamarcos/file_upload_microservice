package br.com.processing_service.service;

import br.com.processing_service.model.NotificationMessage;
import br.com.processing_service.model.ProcessedFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private FanoutExchange fanoutExchange;
    @InjectMocks
    private NotificationService notificationService;
    private String queue;
    private String exchangeName;

    @BeforeEach
    void setup(){
        this.queue = "test.queue";
        this.exchangeName = "test.fanout";
        this.notificationService = new NotificationService(rabbitTemplate);
    }

    @Test
    void testSend() {
        String status = "PROCESSING_SUCCESS";
        String message = "Arquivo processado com sucesso!";

        notificationService.send(message, queue, new NotificationMessage(status, message)).block();
        verify(rabbitTemplate, times(1)).convertAndSend(
                (String) eq(queue),
                (Object) argThat(arg -> {
                    if (!(arg instanceof NotificationMessage)) return false;
                    NotificationMessage notification = (NotificationMessage) arg;
                    return status.equals(notification.getStatus())
                            && message.equals(notification.getMessage());
                }));
    }

    @Test
    void testSendWithError() {
        String status = "ERROR";
        String message = "Falha ao processar arquivo";
        // Simula a excecao no envio da mensagem
        doThrow(new RuntimeException("Falha ao enviar"))
                .when(rabbitTemplate).convertAndSend(anyString(), (Object) any());

        StepVerifier.create(notificationService.send(message, queue, new NotificationMessage(status, message)))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().contains("Falha ao enviar"))
                .verify();

        verify(rabbitTemplate).convertAndSend(anyString(), (Object) any());
    }


    @Test
    void testSendToTopic() {
        ProcessedFile processedFile = new ProcessedFile(123L, "file.jpg", "bucket/file.jpg");

        when(fanoutExchange.getName()).thenReturn(exchangeName);
        notificationService.sendToTopic(fanoutExchange, processedFile).block();
        verify(rabbitTemplate, times(1)).convertAndSend(
                (String) eq(exchangeName),
                (String) eq(""),
                (Object) eq(processedFile)
        );
    }

    @Test
    void testSendToTopicWithError() {
        FanoutExchange exchange = new FanoutExchange(exchangeName);
        ProcessedFile processedFile = new ProcessedFile(123L, "arquivo.txt", "/caminho/nuvem");

        doThrow(new RuntimeException("Falha ao enviar para topico"))
                .when(rabbitTemplate).convertAndSend(eq(exchange.getName()), eq(""), eq(processedFile));

        StepVerifier.create(notificationService.sendToTopic(exchange, processedFile))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().contains("Falha ao enviar para topico"))
                .verify();
    }

}