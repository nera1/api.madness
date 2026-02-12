CREATE TABLE refresh_tokens
(
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id    BIGINT UNSIGNED NOT NULL,
    token_hash VARCHAR(64)                            NOT NULL,
    expires_at TIMESTAMP                              NOT NULL,
    create_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP(6) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_refresh_token_hash(token_hash),
    KEY        ix_refresh_token_user_id (user_id),
    KEY        ix_refresh_token_expires_at (expires_at),

    CONSTRAINT fk_refresh_token_user foreign key (user_id) REFERENCES users (id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;