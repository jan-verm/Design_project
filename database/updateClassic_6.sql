SET @A = CAST ((SELECT version FROM "Databaseversion") AS INTEGER); 
SET @V = 6;
PRINT 'Current database version = ' + CAST (@A AS STRING);
IF @A = @V - 1
BEGIN
	PRINT 'Updating database to version ' + CAST (@V AS STRING);
	
	CREATE TABLE public."Lti"
	(
	   key text, 
	   secret text, 
	   CONSTRAINT pkey PRIMARY KEY (key)
	) ;

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

