CREATE TABLE tb_files (
                        id BIGSERIAL PRIMARY KEY,
                        filename VARCHAR(255) NOT NULL,
                        content_type VARCHAR(255) NOT NULL,
                        status VARCHAR(50) NOT NULL,
                        cloud_path VARCHAR(255) NULL
);