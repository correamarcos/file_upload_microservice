package br.com.processing_service.model;

import java.io.Serial;
import java.io.Serializable;

public class NotificationMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 3674294739879418471L;

    private String status;
    private String message;

    public NotificationMessage(){}

    public NotificationMessage(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }
}
