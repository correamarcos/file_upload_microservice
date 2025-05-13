package br.com.uploadService.service;

import br.com.uploadService.controller.Dtos.FailureNotificationDto;
import br.com.uploadService.controller.Dtos.FileProcessedDto;
import br.com.uploadService.controller.Dtos.FileUploadedDto;
import br.com.uploadService.core.enums.StatusFile;
import br.com.uploadService.model.FailureNotification;
import br.com.uploadService.model.FileEntity;
import br.com.uploadService.repository.FailureNotificationRepository;
import br.com.uploadService.repository.FileRepository;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

@Service
public class UploadService {
    private static final Logger logger = LoggerFactory.getLogger(UploadService.class);

    private final FileRepository fileRepository;
    private final RabbitTemplate rabbitTemplate;
    private final Path uploadDir;
    private final String processingQueue;
    private final FailureNotificationRepository failureNotificationRepository;

    public UploadService(FileRepository fileRepository,
                         RabbitTemplate rabbitTemplate,
                         @Value("${file.upload.path}") String uploadDirPath,
                         @Value("${spring.rabbitmq.queues.processing}") String processingQueue,
                         FailureNotificationRepository failureNotificationRepository) {
        this.fileRepository = fileRepository;
        this.uploadDir = Paths.get(uploadDirPath);
        this.rabbitTemplate = rabbitTemplate;
        this.processingQueue = processingQueue;
        this.failureNotificationRepository = failureNotificationRepository;
    }

    @PostConstruct
    private void initializeDirectory() {
        try {
            logger.info("Verificando existencia do diretório");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar o diretório de upload", e);
        }
    }

    public Mono<FileEntity> saveFile(FilePart filePart) {
        String fileName = filePart.filename();
        Path filePath = uploadDir.resolve(fileName);
        String contentType = Optional.ofNullable(filePart.headers().getContentType())
                .map(MediaType::toString)
                .orElse("application/octet-stream");

        return filePart.transferTo(filePath)
                .then(Mono.defer(() -> fileRepository.save(new FileEntity(fileName, contentType, StatusFile.PENDING.getDescription()))))
                .doOnNext(file -> rabbitTemplate.convertAndSend(processingQueue, new FileUploadedDto(file.getId(), fileName, filePath.toString())))
                .onErrorResume(error -> {
                    logger.error("Erro ao salvar arquivo {}: {}", fileName, error.getMessage(), error);
                    return Mono.error(
                            new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falha ao salvar o arquivo " + fileName, error));
                });

    }

    @RabbitListener(queues = "${spring.rabbitmq.queues.processed}")
    public Mono<Void> processFile(@Valid FileProcessedDto fileProcessed) {
        logger.info("Updating file: {}", fileProcessed.getFileId());

        return fileRepository.findById(fileProcessed.getFileId())
                .flatMap(file -> {
                    file.setStatus(StatusFile.PROCESSED.getDescription());
                    file.setCloudPath(fileProcessed.getCloudPath());
                    return fileRepository.save(file);
                })
                .doOnSuccess(f -> logger.info("File updated: {}", f.getFilename()))
                .onErrorResume(e -> {
                    logger.error("Failed to update file {}: {}", fileProcessed.getFileName(), e.getMessage(), e);
                    return Mono.empty();
                })
                .then();
    }

    @RabbitListener(queues = "${spring.rabbitmq.queues.notification_failure}")
    public Mono<Void> saveFailureNotification(FailureNotificationDto failure){
        return failureNotificationRepository
                .save(new FailureNotification(
                        failure.file_id(),
                        failure.status(),
                        failure.message(),
                        failure.queue(),
                        failure.error(),
                        failure.created_at()))
                .onErrorResume(e -> {
                    logger.error("Erro ao salvar falha de notificação: {}", e.getMessage(), e);
                    return Mono.empty();
                })
                .then();
    }
}

