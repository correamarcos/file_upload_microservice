package br.com.uploadService.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "tb_files")
public class FileEntity {
    @Id
    private Long id;
    private String filename;
    private String contentType;
    private String status;
    private String cloudPath;

    public FileEntity(){}

    public FileEntity(String filename, String contentType, String status) {
        this.filename = filename;
        this.contentType = contentType;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getCloudPath() { return cloudPath; }

    public void setCloudPath(String cloudPath) { this.cloudPath = cloudPath; }
}

