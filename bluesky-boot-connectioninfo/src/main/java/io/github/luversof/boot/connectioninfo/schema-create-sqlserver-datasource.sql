CREATE TABLE "DataSourceConnectionConfig" (
	"connection" VARCHAR(50) UNIQUE,
	url VARCHAR(255) NOT NULL,
	username VARCHAR(255) NOT NULL,
	password VARCHAR(255) NOT NULL,
	extradata NVARCHAR(MAX)
)
;
ALTER TABLE "DataSourceConnectionConfig" ADD CONSTRAINT CK_IsJSONData CHECK (ISJSON(extradata) > 0);
