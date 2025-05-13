package br.com.uploadService.controller.v1;

import br.com.uploadService.controller.Dtos.FileResponseDto;
import br.com.uploadService.service.FileService;
import br.com.uploadService.service.UploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("upload/v1/files")
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

