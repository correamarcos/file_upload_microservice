CREATE TABLE failed_notifications (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(100),
    message TEXT,
    queue VARCHAR(255),
    error TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
