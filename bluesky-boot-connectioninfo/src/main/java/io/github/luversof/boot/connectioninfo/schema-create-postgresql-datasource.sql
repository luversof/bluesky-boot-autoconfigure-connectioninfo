CREATE TABLE "DataSourceConnectionConfig" (
	"connection" VARCHAR NOT NULL,
	"url" VARCHAR NOT NULL,
	"username" VARCHAR NOT NULL,
	"password" VARCHAR NOT NULL,
	"extradata" JSON NULL DEFAULT NULL,
	UNIQUE ("connection")
)