
CREATE TABLE "User"
(
  "userID" serial PRIMARY KEY,
  "username" text NOT NULL
);

CREATE TABLE "Annotation"
(
  "annotationID" serial PRIMARY KEY,
  "userID" integer NOT NULL REFERENCES "User" ON DELETE CASCADE,
  "title" text NOT NULL,
  "body" text NOT NULL  
);

CREATE TABLE "Coursenotes"
(
  "coursenotesID" serial PRIMARY KEY,
  "title" text NOT NULL,
  "url" text NOT NULL
);

CREATE TABLE "Coursenotes_annotation"
(
  "caID" serial PRIMARY KEY,
  "coursenotesID" integer NOT NULL REFERENCES "Coursenotes" ON DELETE CASCADE,
  "annotationID" integer NOT NULL REFERENCES "Annotation" ON DELETE CASCADE,
  "location" integer NOT NULL
);

CREATE TABLE "Databaseversion"
(
  "versionID" serial PRIMARY KEY,
  "version" integer NOT NULL
);

CREATE TABLE "Video"
(
  "videoID" serial PRIMARY KEY,
  "title" text NOT NULL,
  "url" text NOT NULL,
  "duration" integer NOT NULL
);

CREATE TABLE "Video_annotation"
(
  "vaID" serial PRIMARY KEY,
  "videoID" integer NOT NULL REFERENCES "Video" ON DELETE CASCADE,
  "annotationID" integer NOT NULL REFERENCES "Annotation" ON DELETE CASCADE,
  "timestamp" integer NOT NULL
);

INSERT INTO "Databaseversion" ("version")
VALUES (1);