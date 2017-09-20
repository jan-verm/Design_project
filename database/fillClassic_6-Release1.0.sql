CREATE TABLE public."User"
(
  "userID" serial NOT NULL,
  username text NOT NULL,
  role integer NOT NULL DEFAULT 0,
  password text NOT NULL DEFAULT 'test'::text,
  CONSTRAINT "User_pkey" PRIMARY KEY ("userID"),
  CONSTRAINT username_unique UNIQUE (username)
);

CREATE TABLE public."Lecture"
(
  "lectureID" serial NOT NULL,
  "courseID" integer,
  title text,
  "userID" integer,
  CONSTRAINT lecturekey PRIMARY KEY ("lectureID"),
  CONSTRAINT course FOREIGN KEY ("courseID")
      REFERENCES public."Lecture" ("lectureID") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT "user" FOREIGN KEY ("userID")
      REFERENCES public."User" ("userID") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE public."Subscription"
(
  "subscriptionID" serial NOT NULL,
  "userID" integer NOT NULL,
  "courseID" integer NOT NULL,
  managing boolean NOT NULL DEFAULT false,
  CONSTRAINT subscribtionkey PRIMARY KEY ("subscriptionID"),
  CONSTRAINT course FOREIGN KEY ("courseID")
      REFERENCES public."Lecture" ("lectureID") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT "user" FOREIGN KEY ("userID")
      REFERENCES public."User" ("userID") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE public."Video"
(
  "videoID" serial NOT NULL,
  title text NOT NULL,
  url text NOT NULL,
  duration integer NOT NULL,
  "parentID" integer,
  CONSTRAINT "Video_pkey" PRIMARY KEY ("videoID"),
  CONSTRAINT course FOREIGN KEY ("parentID")
      REFERENCES public."Lecture" ("lectureID") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE public."Coursenotes"
(
  "coursenotesID" serial NOT NULL,
  title text NOT NULL,
  url text NOT NULL,
  "parentID" integer,
  CONSTRAINT "Coursenotes_pkey" PRIMARY KEY ("coursenotesID"),
  CONSTRAINT course FOREIGN KEY ("parentID")
      REFERENCES public."Lecture" ("lectureID") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE public."Comment"
(
  "commentID" serial NOT NULL,
  "userID" integer NOT NULL,
  body text NOT NULL,
  creationtime bigint NOT NULL DEFAULT 0,
  approved boolean NOT NULL DEFAULT false,
  question boolean NOT NULL DEFAULT false,
  "parentID" integer,
  CONSTRAINT "Annotation_pkey" PRIMARY KEY ("commentID"),
  CONSTRAINT "Annotation_userID_fkey" FOREIGN KEY ("userID")
      REFERENCES public."User" ("userID") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT reply FOREIGN KEY ("parentID")
      REFERENCES public."Comment" ("commentID") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE public."Vote"
(
  "voteID" serial NOT NULL,
  "commentID" integer,
  "userID" integer,
  value integer,
  CONSTRAINT votekey PRIMARY KEY ("voteID"),
  CONSTRAINT comment FOREIGN KEY ("commentID")
      REFERENCES public."Comment" ("commentID") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT "user" FOREIGN KEY ("userID")
      REFERENCES public."User" ("userID") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE public."Coursenotes_comment"
(
  "ccID" serial NOT NULL,
  "coursenotesID" integer NOT NULL,
  "commentID" integer NOT NULL,
  visible boolean NOT NULL DEFAULT true,
  CONSTRAINT "Coursenotes_annotation_pkey" PRIMARY KEY ("ccID"),
  CONSTRAINT "Coursenotes_annotation_annotationID_fkey" FOREIGN KEY ("commentID")
      REFERENCES public."Comment" ("commentID") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT "Coursenotes_annotation_coursenotesID_fkey" FOREIGN KEY ("coursenotesID")
      REFERENCES public."Coursenotes" ("coursenotesID") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE public."Video_comment"
(
  "vcID" serial NOT NULL DEFAULT,
  "videoID" integer NOT NULL,
  "commentID" integer NOT NULL,
  "timestamp" integer NOT NULL DEFAULT 0,
  visible boolean NOT NULL DEFAULT true,
  CONSTRAINT "Video_annotation_pkey" PRIMARY KEY ("vcID"),
  CONSTRAINT "Video_annotation_annotationID_fkey" FOREIGN KEY ("commentID")
      REFERENCES public."Comment" ("commentID") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT "Video_annotation_videoID_fkey" FOREIGN KEY ("videoID")
      REFERENCES public."Video" ("videoID") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE public."Location"
(
  "locationID" serial NOT NULL,
  "ccID" integer NOT NULL,
  pagenumber integer NOT NULL,
  x1 double precision NOT NULL,
  y1 double precision NOT NULL,
  x2 double precision NOT NULL,
  y2 double precision NOT NULL,
  CONSTRAINT "PK_ID" PRIMARY KEY ("locationID"),
  CONSTRAINT "Coursenotes_comment" FOREIGN KEY ("ccID")
      REFERENCES public."Coursenotes_comment" ("ccID") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE public."Lti"
(
  key text NOT NULL,
  secret text,
  CONSTRAINT pkey PRIMARY KEY (key)
);

CREATE TABLE public."Databaseversion"
(
  "versionID" serial NOT NULL,
  version integer NOT NULL,
  CONSTRAINT "Databaseversion_pkey" PRIMARY KEY ("versionID")
);

INSERT INTO "Databaseversion" ("version") VALUES (6);