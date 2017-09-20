SET @A = CAST ((SELECT version FROM "Databaseversion") AS INTEGER); 
PRINT 'Current database version = ' + CAST (@A AS STRING);
IF @A = 2 
BEGIN
	PRINT 'Updating database';
	-- Comment
	ALTER TABLE public."Comment"
	  DROP COLUMN title;
	ALTER TABLE public."Comment"
	  ALTER COLUMN creationtime TYPE bigint;

	-- Video
	ALTER TABLE public."Video"
	  ADD COLUMN "userID" integer;
	ALTER TABLE public."Video"
	  ADD CONSTRAINT "user" FOREIGN KEY ("userID") REFERENCES public."User" ("userID") ON UPDATE NO ACTION ON DELETE CASCADE;

	-- Coursenotes
	ALTER TABLE public."Coursenotes"
	  ADD COLUMN "userID" integer;
	ALTER TABLE public."Coursenotes"
	  ADD CONSTRAINT "user" FOREIGN KEY ("userID") REFERENCES public."User" ("userID") ON UPDATE NO ACTION ON DELETE CASCADE;

	-- User
	ALTER TABLE public."User"
	  ADD COLUMN role integer NOT NULL DEFAULT 0;

	-- Location
	CREATE TABLE public."Location"(
	   "locationID" serial NOT NULL, 
	   "ccID" integer NOT NULL,
	   pagenumber integer NOT NULL, 
	   x1 double precision NOT NULL, 
	   y1 double precision NOT NULL, 
	   x2 double precision NOT NULL, 
	   y2 double precision NOT NULL, 
	   CONSTRAINT "PK_ID" PRIMARY KEY ("locationID"),
	   CONSTRAINT "Coursenotes_comment" FOREIGN KEY ("ccID") REFERENCES public."Coursenotes_comment" ("ccID") ON UPDATE NO ACTION ON DELETE CASCADE
	);

	-- Coursenotes_comment
	ALTER TABLE public."Coursenotes_comment"
	  DROP COLUMN pagenumber;
	ALTER TABLE public."Coursenotes_comment"
	  DROP COLUMN x1;
	ALTER TABLE public."Coursenotes_comment"
	  DROP COLUMN y1;
	ALTER TABLE public."Coursenotes_comment"
	  DROP COLUMN x2;
	ALTER TABLE public."Coursenotes_comment"
	  DROP COLUMN y2;	

	UPDATE "Databaseversion" SET "version"=3 WHERE "versionID"=1;
END
ELSE IF @A = 3
BEGIN
	PRINT 'Database is up to date';
END
ELSE
BEGIN
	SET @B = @A +1;
	PRINT 'Please execute script number ' + CAST (@B AS STRING);
END


