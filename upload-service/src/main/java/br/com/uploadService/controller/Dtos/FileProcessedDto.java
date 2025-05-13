package br.com.uploadService.controller.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class FileProcessedDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -2728317943880485126L;

    @NotNull
    @JsonProperty("fileId")
    private Long fileId;
    @NotNull
    @JsonProperty("fileName")
    private String fileName;
    @NotNull
    @JsonProperty("cloudPath")
    private String cloudPath;

    public FileProcessedDto(){}

    public FileProcessedDto(Long fileId, String fileName, String cloudPath) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.cloudPath = cloudPath;
    }

    public @NotNull Long getFileId() {
        return fileId;
    }

    public @NotNull String getFileName() {
        return fileName;
    }

    public @NotNull String getCloudPath() {
        return cloudPath;
    }
}
