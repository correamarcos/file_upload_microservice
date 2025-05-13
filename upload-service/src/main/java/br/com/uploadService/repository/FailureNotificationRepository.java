package br.com.uploadService.repository;

import br.com.uploadService.model.FailureNotification;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface FailureNotificationRepository extends R2dbcRepository<FailureNotification, Long> {
}
