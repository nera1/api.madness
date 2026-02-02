CREATE TABLE email_verification_codes (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

    email VARCHAR(128) NOT NULL,
    code VARCHAR(6) NOT NULL,

    status ENUM('PENDING', 'VERIFIED', 'EXPIRED') NOT NULL DEFAULT 'PENDING',

    expires_at TIMESTAMP(6) NOT NULL,
    verified_at TIMESTAMP(6) NULL,

    attempt_count INT NOT NULL DEFAULT 0,

    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
        ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    KEY ix_email_verification_email (email),
    KEY ix_email_verification_code (code),
    KEY ix_email_verification_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;