-- 1) slides 테이블의 외래키/인덱스 제거
ALTER TABLE slides DROP FOREIGN KEY fk_slides_deck;
ALTER TABLE slides DROP INDEX ix_slides_deck_id;

-- 2) slides.deck_id → slides.project_id 컬럼 이름 변경
ALTER TABLE slides CHANGE COLUMN deck_id project_id BIGINT UNSIGNED NULL;

-- 3) decks 테이블 → projects 테이블 이름 변경
RENAME TABLE decks TO projects;

-- 4) projects 테이블: 기존 FK/인덱스 제거 → 새 인덱스 추가 → 새 FK 추가
--    (FK 추가 전에 새 인덱스를 먼저 만들어야 MySQL이 새 인덱스를 사용함)
ALTER TABLE projects DROP FOREIGN KEY fk_decks_user;
ALTER TABLE projects DROP INDEX ix_decks_user_id;
ALTER TABLE projects ADD INDEX ix_projects_user_id (user_id);
ALTER TABLE projects ADD CONSTRAINT fk_projects_user FOREIGN KEY (user_id) REFERENCES users (id)
    ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE projects DROP INDEX ix_decks_created_at;
ALTER TABLE projects ADD INDEX ix_projects_created_at (created_at);

-- 5) slides 테이블에 새 인덱스/외래키 추가
ALTER TABLE slides ADD INDEX ix_slides_project_id (project_id);
ALTER TABLE slides ADD CONSTRAINT fk_slides_project FOREIGN KEY (project_id) REFERENCES projects (id)
    ON DELETE SET NULL ON UPDATE CASCADE;
