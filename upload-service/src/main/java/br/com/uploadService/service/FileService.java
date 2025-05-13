package br.com.uploadService.service;

import br.com.uploadService.model.FileEntity;
import br.com.uploadService.repository.FileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FileService {
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository){
        this.fileRepository = fileRepository;
    }

    public Flux<FileEntity> getAll(){
        return fileRepository.findAll().onErrorResume(e -> {
            logger.error("Erro ao buscar arquivos: {}", e);
            return Mono.error(
                    new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao buscar arquivos: " + e.getMessage(), e));
        });
    }
}
