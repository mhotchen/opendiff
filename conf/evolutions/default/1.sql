# --- !Ups

CREATE TABLE "diff" ("id" VARCHAR(50) NOT NULL PRIMARY KEY, "diff" VARCHAR(10240) NOT NULL, "created_at" VARCHAR(25) NOT NULL);

# --- !Downs

DROP TABLE "diff";
