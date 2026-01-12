ALTER TABLE users
  DROP INDEX uk_users_username;

ALTER TABLE users
  DROP COLUMN username;


CREATE TABLE user_credentials (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id BIGINT UNSIGNED NOT NULL,

  email VARCHAR(128) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,

  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
    ON UPDATE CURRENT_TIMESTAMP(6),

  PRIMARY KEY (id),

  UNIQUE KEY uk_user_credentials_user_id (user_id),

  UNIQUE KEY uk_user_credentials_email (email),

  CONSTRAINT fk_user_credentials_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE user_oauth_accounts (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id BIGINT UNSIGNED NOT NULL,

  provider ENUM('GOOGLE','GITHUB','KAKAO','NAVER') NOT NULL,
  provider_user_id VARCHAR(128) NOT NULL,

  email VARCHAR(128) NULL,

  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
    ON UPDATE CURRENT_TIMESTAMP(6),

  PRIMARY KEY (id),

  UNIQUE KEY uk_user_oauth_provider_user (provider, provider_user_id),

  KEY ix_user_oauth_user_id (user_id),
  KEY ix_user_oauth_email (email),

  CONSTRAINT fk_user_oauth_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
