package br.com.processing_service.service;

import io.minio.*;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

@Service
public class MinioService {
    private static final Logger logger = LoggerFactory.getLogger(MinioService.class);

    private final MinioClient minioClient;
    private final String bucketName;

    public MinioService(@Value("${minio.url}") String minioUrl,
                        @Value("${minio.access-key}") String accessKey,
                        @Value("${minio.secret-key}") String secretKey,
                        @Value("${minio.bucket-name}") String bucketName) {
        this.minioClient = MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, secretKey)
                .build();
        this.bucketName = bucketName;
    }

    @PostConstruct
    public void ensureBucketExists() {
        checkAndCreateBucketIfNotExists(bucketName).block();
    }

    private Mono<Void> checkAndCreateBucketIfNotExists(String bucketName) {
        return checkBucketExists(bucketName)
                .flatMap(bucketExists -> createNewBucket(bucketName, bucketExists))
                .doOnError(error -> System.err.println("Erro ao verificar/criar bucket: " + error.getMessage()));
    }

    private Mono<Boolean> checkBucketExists(String bucketName) {
        return Mono.fromCallable(() -> {
            try {
                return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            } catch (Exception e) {
                logger.error("Erro ao verificar bucket: " + e.getMessage());
                throw new RuntimeException("Erro ao verificar bucket", e);
            }
        });
    }

    private Mono<Void> createNewBucket(String bucketName, Boolean bucketExists) {
        if (!bucketExists) {
            return Mono.fromCallable(() -> {
                try {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                    logger.error("Bucket criado: {}", bucketName);
                    return bucketName;
                } catch (Exception e) {
                    logger.error("Erro ao criar bucket: {}", e.getMessage());
                    throw new RuntimeException("Erro ao criar bucket", e);
                }
            }).then();
        } else {
            logger.info("Bucket já existe: {}", bucketName);
            return Mono.empty();
        }
    }

    public Mono<String> uploadFile(File file, String objectName) {
        return validateFile(file)
                .flatMap(contentType -> uploadObject(file, objectName, contentType))
                .map(savedObject -> bucketName.concat("/").concat(savedObject))
                .doOnSuccess(success -> System.out.println("Arquivo salvo no Minio: " + success))
                .doOnError(error -> System.err.println("Erro ao salvar no Minio: " + error.getMessage()));
    }

    private Mono<String> validateFile(File file) {
        return Mono.fromCallable(() -> {
            if (!Files.exists(file.toPath()) || !Files.isRegularFile(file.toPath())) {
                throw new IllegalArgumentException("Arquivo inválido ou não encontrado: " + file.toPath());
            }
            return Files.probeContentType(file.toPath());
        });
    }

    private Mono<String> uploadObject(File file, String objectName, String contentType) {
        return Mono.fromCallable(() -> {
            try (InputStream inputStream = Files.newInputStream(file.toPath())) {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(inputStream, Files.size(file.toPath()), -1)
                        .contentType(contentType)
                        .build());
                return objectName;
            }
        });
    }
}

