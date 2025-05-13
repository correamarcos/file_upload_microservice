package br.com.uploadService.controller.Dtos;

import br.com.uploadService.model.FileEntity;

public record FileResponseDto(String filename,
                              String contentType,
                              String status) {

    public FileResponseDto(FileEntity file){
        this(file.getFilename(), file.getContentType(), file.getStatus());
    }
}
