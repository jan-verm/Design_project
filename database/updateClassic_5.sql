SET @A = CAST ((SELECT version FROM "Databaseversion") AS INTEGER); 
SET @V = 5;
PRINT 'Current database version = ' + CAST (@A AS STRING);
IF @A = @V - 1
BEGIN
	PRINT 'Updating database to version ' + CAST (@V AS STRING);
	
	-- USER	
	ALTER TABLE "User"
	  ADD COLUMN "password" text NOT NULL DEFAULT 'password';

	-- COMMENT
	ALTER TABLE "Comment"
	  ADD COLUMN "parentID" integer;
	ALTER TABLE "Comment"
	  ADD CONSTRAINT reply FOREIGN KEY ("parentID") REFERENCES "Comment" ("commentID") 		ON UPDATE NO ACTION ON DELETE CASCADE;
	
	-- REPLY
	UPDATE "Comment" SET "parentID"="Comment_reply"."commentID" 
	FROM "Comment_reply" WHERE "Comment"."commentID"="Comment_reply"."replyID";

	DROP TABLE "Comment_reply";

	-- LECTURE

	ALTER TABLE "Lecture"
	  ADD COLUMN "userID" integer;
	ALTER TABLE "Lecture"
	  ADD CONSTRAINT "user" FOREIGN KEY ("userID") REFERENCES "User" ("userID") ON UPDATE NO ACTION ON DELETE CASCADE;

	ALTER TABLE public."Subscribtion"
	  RENAME TO "Subscription";
	ALTER TABLE public."Subscription"
	   ALTER COLUMN "userID" SET NOT NULL;
	ALTER TABLE public."Subscription"
 	  ALTER COLUMN "courseID" SET NOT NULL;
	ALTER TABLE public."Subscription"
	   ALTER COLUMN managing SET DEFAULT FALSE;
	ALTER TABLE public."Subscription"
 	  ALTER COLUMN managing SET NOT NULL;



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

