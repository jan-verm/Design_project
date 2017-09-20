 CREATE TABLE "Messages"
(
  "ID" integer NOT NULL,
  text text,
  CONSTRAINT "ID" PRIMARY KEY ("ID")
);

INSERT INTO "Messages" ("ID","text")
VALUES (1,'hello Classic!');