CREATE TABLE posts
(
    id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id        BIGINT UNSIGNED NOT NULL,
    headline_text  VARCHAR(512)    NOT NULL DEFAULT '',
    headline_level VARCHAR(4)      NOT NULL DEFAULT 'p',
    body           JSON            NOT NULL,
    created_at     TIMESTAMP(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at     TIMESTAMP(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),
    KEY ix_posts_user_id (user_id),
    KEY ix_posts_created_at (created_at),

    CONSTRAINT fk_posts_user FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
