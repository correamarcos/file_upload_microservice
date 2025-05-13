package br.com.processing_service.service;

import br.com.processing_service.model.FileData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.FanoutExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessingServiceTest {

    @Mock
    private MinioService minioService;
    @Mock
    private FileProcessorService fileProcessorService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private FanoutExchange fanoutExchange;
    @InjectMocks
    private ProcessingService processingService;

    private String notificationQueue;
    private String notificationFailureQueue;

    @BeforeEach
    void setUp() {
        this.notificationQueue = "notification.queue";
        this.notificationFailureQueue = "notification.failure.queue";
        this.processingService = new ProcessingService(
                minioService,
                notificationService,
                fileProcessorService,
                fanoutExchange,
                notificationQueue,
                notificationFailureQueue
        );
    }

    @Test
    void shouldProcessFileSuccessfully() {
        FileData fileData = new FileData(123L, "test.txt", "/tmp/test.txt");
        Path tempPath = Paths.get(fileData.getFilePath());
        String minioPath = "minio://bucket/test.txt";

        when(fileProcessorService.processAndSaveTempFile(fileData)).thenReturn(Mono.just(tempPath));
        when(minioService.uploadFile(tempPath.toFile(), "test.txt")).thenReturn(Mono.just(minioPath));
        when(notificationService.sendToTopic(any(), any())).thenReturn(Mono.empty());
        when(fileProcessorService.deleteTemp(tempPath)).thenReturn(Mono.empty());
        when(notificationService.send(anyString(), anyString(), any())).thenReturn(Mono.empty());

        StepVerifier.create(processingService.processFile(fileData))
                .verifyComplete();

        verify(notificationService).sendToTopic(any(), any());
        verify(notificationService).send(contains("processado"), anyString(), any());
    }

    @Test
    void shouldHandleUploadFailure() {
        FileData fileData = new FileData(456L, "fail.txt", "/tmp/fail.txt");
        Path tempPath = Paths.get(fileData.getFilePath());

        when(fileProcessorService.processAndSaveTempFile(fileData)).thenReturn(Mono.just(tempPath));
        when(minioService.uploadFile(tempPath.toFile(), "fail.txt")).thenReturn(Mono.error(new RuntimeException("Erro no upload")));
        when(notificationService.send(anyString(), anyString(), any())).thenReturn(Mono.empty());

        StepVerifier.create(processingService.processFile(fileData))
                .verifyComplete(); // Erro tratado internamente

        verify(notificationService).send(contains("Falha ao processar"), anyString(), any());
    }

//    @Test
//    void shouldHandleNotificationSendFailure() {
//        FileData fileData = new FileData(789L, "notifyfail.txt", "/tmp/notifyfail.txt");
//        Path tempPath = Paths.get(fileData.getFilePath());
//        String minioPath = "minio://bucket/notifyfail.txt";
//
//        when(fileProcessorService.processAndSaveTempFile(fileData)).thenReturn(Mono.just(tempPath));
//        when(minioService.uploadFile(tempPath.toFile(), "notifyfail.txt")).thenReturn(Mono.just(minioPath));
//        when(notificationService.sendToTopic(any(), any())).thenReturn(Mono.empty());
//        when(fileProcessorService.deleteTemp(tempPath)).thenReturn(Mono.empty());
//        when(notificationService.send(anyString(), eq(notificationQueue), any()))
//                .thenReturn(Mono.error(new RuntimeException("Erro ao enviar a notificacao")));
//
//        StepVerifier.create(processingService.processFile(fileData))
//                .verifyComplete();
//
//        verify(notificationService).send(eq("Erro ao enviar a notificacao"), eq(notificationFailureQueue), any());
//    }
}