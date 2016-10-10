# --- !Ups

CREATE INDEX idx_diff_created_at ON "diff" ("created_at");

# --- !Downs

DROP INDEX idx_diff_created_at;
