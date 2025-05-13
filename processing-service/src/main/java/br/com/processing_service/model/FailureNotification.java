package br.com.processing_service.model;

import java.time.LocalDateTime;

public record FailureNotification(Long file_id,
                                  String status,
                                  String message,
                                  String queue,
                                  String error,
                                  LocalDateTime created_at) {
}
