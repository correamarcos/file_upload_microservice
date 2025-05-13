package br.com.processing_service.model;

import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class ProcessedFile implements Serializable {
    @Serial
    private static final long serialVersionUID = 4361232558964656048L;

    private Long fileId;
    private String fileName;
    private String cloudPath;

    public ProcessedFile(){}

    public ProcessedFile(Long fileId, String fileName, String cloudPath) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.cloudPath = cloudPath;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCloudPath() {
        return cloudPath;
    }

    public void setCloudPath(String cloudPath) {
        this.cloudPath = cloudPath;
    }
}
