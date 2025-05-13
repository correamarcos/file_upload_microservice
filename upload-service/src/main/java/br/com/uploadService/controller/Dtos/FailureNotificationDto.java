package br.com.uploadService.controller.Dtos;

import java.util.Date;

public record FailureNotificationDto(Long file_id,
                                     String status,
                                     String message,
                                     String queue,
                                     String error,
                                     Date created_at) {
}
