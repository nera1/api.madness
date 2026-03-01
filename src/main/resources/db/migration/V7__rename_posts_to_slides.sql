RENAME TABLE posts TO slides;

ALTER TABLE slides DROP FOREIGN KEY fk_posts_user;
ALTER TABLE slides ADD CONSTRAINT fk_slides_user FOREIGN KEY (user_id) REFERENCES users (id)
    ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE slides RENAME INDEX ix_posts_user_id TO ix_slides_user_id;
ALTER TABLE slides RENAME INDEX ix_posts_created_at TO ix_slides_created_at;
