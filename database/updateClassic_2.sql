SET @A = CAST ((SELECT version FROM "Databaseversion") AS INTEGER); 
PRINT 'Current databese version = ' + CAST (@A AS STRING);
IF @A = 1 
BEGIN
	PRINT 'Updating database';
	--Replies

	CREATE TABLE "Comment_reply"
	(
	  "crID" serial NOT NULL,
	  "commentID" integer NOT NULL,
	  "replyID" integer NOT NULL,
	  CONSTRAINT "Primary Key" PRIMARY KEY ("crID"),
	  CONSTRAINT "Child" FOREIGN KEY ("replyID")
	      REFERENCES "Annotation" ("annotationID") MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE CASCADE,
	  CONSTRAINT "Parent" FOREIGN KEY ("commentID")
	      REFERENCES "Annotation" ("annotationID") MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE CASCADE
	);

	--Comment

	ALTER TABLE "Annotation" RENAME "annotationID"  TO "commentID";
	ALTER TABLE "Annotation"
	  ADD COLUMN creationtime integer NOT NULL DEFAULT 0;
	ALTER TABLE "Annotation"
	  ADD COLUMN approved boolean NOT NULL DEFAULT False;
	ALTER TABLE "Annotation"
	  ADD COLUMN votes integer NOT NULL DEFAULT 0;
	ALTER TABLE "Annotation"
	  ADD COLUMN question boolean NOT NULL DEFAULT False;
	ALTER TABLE "Annotation"
	  RENAME TO "Comment";

	--Coursenotesreference

	ALTER TABLE "Coursenotes_annotation" RENAME "caID"  TO "ccID";
	ALTER TABLE "Coursenotes_annotation" RENAME "annotationID"  TO "commentID";
	ALTER TABLE "Coursenotes_annotation" RENAME location  TO pagenumber;
	ALTER TABLE "Coursenotes_annotation"
	   ALTER COLUMN pagenumber SET DEFAULT 0;
	ALTER TABLE "Coursenotes_annotation"
	  ADD COLUMN x1 integer NOT NULL DEFAULT 0;
	ALTER TABLE "Coursenotes_annotation"
	  ADD COLUMN y1 integer NOT NULL DEFAULT 0;
	ALTER TABLE "Coursenotes_annotation"
	  ADD COLUMN x2 integer NOT NULL DEFAULT 0;
	ALTER TABLE "Coursenotes_annotation"
	  ADD COLUMN y2 integer NOT NULL DEFAULT 0;
	ALTER TABLE "Coursenotes_annotation"
	  RENAME TO "Coursenotes_comment";

	--Videoreference
  
	ALTER TABLE "Video_annotation" RENAME "vaID"  TO "vcID";
	ALTER TABLE "Video_annotation" RENAME "annotationID"  TO "commentID";
	ALTER TABLE "Video_annotation"
	  ALTER COLUMN "timestamp" SET DEFAULT 0;
	ALTER TABLE "Video_annotation"
	  RENAME TO "Video_comment";

	UPDATE "Databaseversion" SET "version"=2 WHERE "versionID"=1;
END
ELSE IF @A = 2 
BEGIN
	PRINT 'Database is up to date';
END
ELSE
BEGIN
	SET @B = @A +1;
	PRINT 'Please execute script number ' + CAST (@B AS STRING);
END


