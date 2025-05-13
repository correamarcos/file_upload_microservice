package br.com.uploadService.controller;

import br.com.uploadService.controller.Dtos.FileResponseDto;
import br.com.uploadService.service.FileService;
import br.com.uploadService.service.UploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/upload")
public class UploadController {
    private final UploadService uploadService;
    private final FileService fileService;

    public UploadController(UploadService uploadService,
                            FileService fileService) {
        this.uploadService = uploadService;
        this.fileService = fileService;
    }

    @PostMapping
    public Mono<ResponseEntity<FileResponseDto>> uploadFile(@RequestPart("file") FilePart filePart) {
        return uploadService.saveFile(filePart)
                .map(file -> ResponseEntity.ok().body(new FileResponseDto(file)));
    }

    @GetMapping
    public Mono<ResponseEntity<List<FileResponseDto>>> getFiles() {
        return fileService.getAll()
                .map(FileResponseDto::new)
                .collectList()
                .map(files -> ResponseEntity.ok().body(files));
    }
}

