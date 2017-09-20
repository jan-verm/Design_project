SET @A = CAST ((SELECT version FROM "Databaseversion") AS INTEGER); 
SET @V = 4;
PRINT 'Current database version = ' + CAST (@A AS STRING);
IF @A = @V - 1
BEGIN
	PRINT 'Updating database to version ' + CAST (@V AS STRING);

	-- Coursenotes
	ALTER TABLE "Coursenotes"
	  DROP CONSTRAINT "user";
	ALTER TABLE "Coursenotes"
	  DROP COLUMN "userID";
	  	
	--Videos
	ALTER TABLE "Video"
	  DROP CONSTRAINT "user";
	ALTER TABLE "Video"
	  DROP COLUMN "userID";

	--User
	ALTER TABLE "User"
	  ADD CONSTRAINT username_unique UNIQUE (username);

	--Coursenotes_comment
	ALTER TABLE "Coursenotes_comment"
	  ADD COLUMN visible boolean NOT NULL DEFAULT TRUE;

	--Video_comment
	ALTER TABLE "Video_comment"
	  ADD COLUMN visible boolean NOT NULL DEFAULT TRUE;

	--Comment
	ALTER TABLE "Comment"
	  DROP COLUMN votes;

	CREATE TABLE "Vote"
	(
	  "voteID" serial NOT NULL,
	  "commentID" integer,
	  "userID" integer,
	  value integer,
	  CONSTRAINT votekey PRIMARY KEY ("voteID"),
	  CONSTRAINT comment FOREIGN KEY ("commentID")
	      REFERENCES "Comment" ("commentID") MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE CASCADE,
	  CONSTRAINT "user" FOREIGN KEY ("userID")
	      REFERENCES "User" ("userID") MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE CASCADE
	);

	-- Course/Lecture
	CREATE TABLE public."Lecture"
	(
	   "lectureID" serial, 
	   "courseID" integer,
	   title text, 
	   CONSTRAINT lecturekey PRIMARY KEY ("lectureID"),
	   CONSTRAINT course FOREIGN KEY ("courseID") REFERENCES "Lecture" ("lectureID") MATCH SIMPLE ON UPDATE NO ACTION ON DELETE CASCADE
	);

	ALTER TABLE "Coursenotes"
	  ADD COLUMN "parentID" integer;
	ALTER TABLE "Coursenotes"
	  ADD CONSTRAINT course FOREIGN KEY ("parentID") REFERENCES "Lecture" ("lectureID") ON UPDATE NO ACTION ON DELETE CASCADE;

	ALTER TABLE "Video"
	  ADD COLUMN "parentID" integer;
	ALTER TABLE "Video"
	  ADD CONSTRAINT course FOREIGN KEY ("parentID") REFERENCES "Lecture" ("lectureID") ON UPDATE NO ACTION ON DELETE CASCADE;

	--Subscription
	CREATE TABLE public."Subscription"
	(
	   "subscriptionID" serial, 
	   "userID" integer, 
	   "courseID" integer, 
	   managing boolean, 
	   CONSTRAINT subscribtionkey PRIMARY KEY ("subscriptionID"), 
	   CONSTRAINT "user" FOREIGN KEY ("userID") REFERENCES "User" ("userID") ON UPDATE NO ACTION ON DELETE CASCADE, 
	   CONSTRAINT course FOREIGN KEY ("courseID") REFERENCES "Lecture" ("lectureID") ON UPDATE NO ACTION ON DELETE CASCADE
	);

	UPDATE "Databaseversion" SET "version"=@V WHERE "versionID"=1;
END
ELSE IF @A = @V
BEGIN
	PRINT 'Database is up to date';
END
ELSE
BEGIN
	SET @B = @A +1;
	PRINT 'Please execute script number ' + CAST (@B AS STRING);
END

