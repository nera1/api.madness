-- 1) decks 테이블 생성
CREATE TABLE decks
(
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id    BIGINT UNSIGNED NOT NULL,
    title      VARCHAR(255)    NOT NULL DEFAULT '',
    created_at TIMESTAMP(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),
    KEY ix_decks_user_id (user_id),
    KEY ix_decks_created_at (created_at),

    CONSTRAINT fk_decks_user FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- 2) slides 테이블에 deck_id, sort_order 컬럼 추가
ALTER TABLE slides
    ADD COLUMN deck_id    BIGINT UNSIGNED NULL     AFTER user_id,
    ADD COLUMN sort_order INT             NOT NULL DEFAULT 0 AFTER deck_id;

ALTER TABLE slides
    ADD KEY ix_slides_deck_id (deck_id),
    ADD CONSTRAINT fk_slides_deck FOREIGN KEY (deck_id) REFERENCES decks (id)
        ON DELETE SET NULL ON UPDATE CASCADE;
