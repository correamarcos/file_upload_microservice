package br.com.uploadService.repository;

import br.com.uploadService.model.FileEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface FileRepository extends R2dbcRepository<FileEntity, Long> {
    Mono<FileEntity> findByFilename(String filename);
    
}

