package br.com.uploadService.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Table(name = "tb_failure_notification")
public class FailureNotification {
    @Id
    private Long id;
    private Long file_id;
    private String status;
    private String message;
    private String queue;
    private String error;
    private Date created_at;

    public FailureNotification(){}

    public FailureNotification(Long file_id, String status, String message, String queue, String error, Date created_at) {
        this.file_id = file_id;
        this.status = status;
        this.message = message;
        this.queue = queue;
        this.error = error;
        this.created_at = created_at;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFile_id() {
        return file_id;
    }

    public void setFile_id(Long file_id) {
        this.file_id = file_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
