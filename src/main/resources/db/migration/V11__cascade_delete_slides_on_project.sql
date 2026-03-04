-- slides FK를 ON DELETE CASCADE로 변경
ALTER TABLE slides DROP FOREIGN KEY fk_slides_project;
ALTER TABLE slides ADD CONSTRAINT fk_slides_project FOREIGN KEY (project_id) REFERENCES projects (id)
    ON DELETE CASCADE ON UPDATE CASCADE;
