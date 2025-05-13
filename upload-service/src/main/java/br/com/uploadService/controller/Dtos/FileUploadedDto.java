package br.com.uploadService.controller.Dtos;

import java.io.Serial;
import java.io.Serializable;

public class FileUploadedDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 6079077912510985298L;

    private Long fileId;
    private String fileName;
    private String filePath;

    public FileUploadedDto() {}

    public FileUploadedDto(Long fileId, String fileName, String filePath){
        this.fileId = fileId;
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public Long getFileId() { return fileId; }

    public void setFileId(Long fileId) { this.fileId = fileId; }

    public String getFileName() { return fileName; }

    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }

    public void setFilePath(String filePath) { this.filePath = filePath; }
}
