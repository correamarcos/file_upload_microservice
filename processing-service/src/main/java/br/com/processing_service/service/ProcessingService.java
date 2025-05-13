package br.com.processing_service.service;

import br.com.processing_service.model.FailureNotification;
import br.com.processing_service.model.FileData;
import br.com.processing_service.model.NotificationMessage;
import br.com.processing_service.model.ProcessedFile;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
public class ProcessingService {
    private static final Logger logger = LoggerFactory.getLogger(ProcessingService.class);

    private final MinioService minioService;
    private final FileProcessorService fileProcessorService;
    private final NotificationService notificationService;
    private final String notificationQueue;
    private final String notificationFailureQueue;
    private final FanoutExchange fileProcessedExchange;

    public ProcessingService(MinioService minioService,
                             NotificationService notificationService,
                             FileProcessorService fileProcessorService,
                             FanoutExchange fileProcessedExchange,
                             @Value("${spring.rabbitmq.queues.notification}") String notificationQueue,
                             @Value("${spring.rabbitmq.queues.notification_failure}") String notificationFailureQueue) {
        this.minioService = minioService;
        this.notificationService = notificationService;
        this.fileProcessorService = fileProcessorService;
        this.notificationQueue = notificationQueue;
        this.fileProcessedExchange = fileProcessedExchange;
        this.notificationFailureQueue = notificationFailureQueue;
    }

    @RabbitListener(queues = "${spring.rabbitmq.queues.processing}")
    public Mono<Void> processFile(@Valid FileData fileData) {
        logger.info("Iniciando processamento do arquivo: {}", fileData.getFileName());

        return fileProcessorService.processAndSaveTempFile(fileData)
                .flatMap(tempFilePath -> uploadAndNotify(fileData, tempFilePath))
                .doOnSuccess(v -> logger.info("Arquivo processado com sucesso: {}", fileData.getFileName()))
                .doOnError(e -> logger.error("Erro ao processar arquivo {}: {}", fileData.getFileName(), e.getMessage(), e))
                .onErrorResume(e -> handleError(e, fileData));
    }

    private Mono<Void> uploadAndNotify(FileData fileData, Path tempFilePath) {
        return minioService.uploadFile(tempFilePath.toFile(), fileData.getFileName())
                .flatMap(fileCloudPath -> notificationService.sendToTopic(fileProcessedExchange,
                                new ProcessedFile(fileData.getFileId(), fileData.getFileName(), fileCloudPath))
                        .then(fileProcessorService.deleteTemp(tempFilePath))
                        .then(notificationService.send(
                                "arquivo " + fileData.getFileName() + "processado",
                                        notificationQueue, new NotificationMessage(
                                        "PROCESSING_SUCCESS",
                                                "O arquivo " + fileData.getFileName() + " foi processado com sucesso!"))
                                .onErrorResume(e -> handlerNotificationError(notificationFailureQueue, fileData, e))
                        )
                );
    }

    private Mono<Void> handleError(Throwable e, FileData fileData) {
        String message = "Falha ao processar o arquivo" + fileData.getFileName();
        logger.error("{}: {}", message, e.getMessage());
        NotificationMessage event = new NotificationMessage("PROCESSING_FAILED", message);
        return notificationService.send(message, notificationQueue, event)
                .onErrorResume(ex -> {
                    logger.error("Erro ao tentar lidar com a falha: {}", ex.getMessage());
                    return Mono.empty();
                });
    }

    private Mono<Void> handlerNotificationError(String queue, FileData file, Throwable e){
        logger.error("Erro ao enviar notificação: {}", e.getMessage());
        FailureNotification event = new FailureNotification(
                file.getFileId(), "NOTIFICATION_ERROR", "message", queue, e.getMessage(), LocalDateTime.now());
        return notificationService.send("Erro ao enviar a notificação", queue, event);
    }
}