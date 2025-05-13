package br.com.processing_service.model;

import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class FileData implements Serializable {
    @Serial
    private static final long serialVersionUID = -2225826542963950793L;
    @NotNull
    private Long fileId;
    @NotNull
    private String fileName;
    @NotNull
    private String filePath;

    public FileData() {}

    public FileData(Long fileId, String fileName, String filePath) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public Long getFileId() { return fileId; }

    public String getFileName() { return fileName; }

    public String getFilePath() { return filePath; }
}

