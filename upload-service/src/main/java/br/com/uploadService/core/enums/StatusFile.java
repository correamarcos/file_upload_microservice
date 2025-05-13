package br.com.uploadService.core.enums;

public enum StatusFile {
    PENDING("PENDING"),
    PROCESSED("PROCESSED");

    private final String description;

    StatusFile(String description){
        this.description = description;
    }

    public String getDescription() { return description; }
}
