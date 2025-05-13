package br.com.processing_service.service;

import br.com.processing_service.model.FileData;
import jakarta.annotation.PostConstruct;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
public class FileProcessorService {
    private static final Logger logger = LoggerFactory.getLogger(FileProcessorService.class);
    private final Path tempPathFile;

    public FileProcessorService(@Value("${file.upload.path}") String tempDirPath){
        this.tempPathFile = Paths.get(tempDirPath);
    }

    @PostConstruct
    private void initializeTempDirectory() {
        try {
            logger.info("Verificando diretorio temporario;");
            if (!Files.exists(tempPathFile)) {
                Files.createDirectories(tempPathFile);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar o diretório temporário", e);
        }
    }

    public Mono<Path> processAndSaveTempFile(FileData fileData) {
        return Mono.fromCallable(() -> {
                    Path path = Paths.get(fileData.getFilePath());
                    if (!Files.exists(path) || !Files.isRegularFile(path))
                        throw new IllegalArgumentException("Arquivo inválido ou não encontrado: " + fileData.getFilePath());

                    BufferedImage originalImage = ImageIO.read(path.toFile());
                    if (originalImage == null)
                        throw new IllegalArgumentException("Arquivo inválido ou não encontrado: " + fileData.getFilePath());

                    BufferedImage processedImage = Thumbnails.of(originalImage)
                            .size(800, 600)
                            .outputQuality(0.8)
                            .asBufferedImage();

                    Path tempPath = tempPathFile.resolve(fileData.getFileName());
                    ImageIO.write(processedImage, getFileExtension(fileData.getFileName()), tempPath.toFile());
                    return tempPath;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(throwable -> {
                    logger.error("Erro no processamento do arquivo", throwable);
                    return new RuntimeException("Erro ao processar o arquivo", throwable);
                });
    }

    public Mono<Void> deleteTemp(Path filePath) {
        return Mono.fromRunnable(() -> {
            try {
                Files.deleteIfExists(filePath);
                logger.info("Arquivo temporário deletado: {}", filePath);
            } catch (IOException e) {
                logger.error("Erro ao deletar o arquivo temporário: {}", filePath, e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    public String getFileExtension(String fileName) {
        if (!fileName.contains("."))
            throw new IllegalArgumentException("Nome do arquivo inválido: " + fileName);

        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
