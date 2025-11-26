CREATE TABLE `DataSourceConnectionConfig` (
	`connection` VARCHAR(50) NOT NULL,
	`url` VARCHAR(255) NOT NULL,
	`username` VARCHAR(255) NOT NULL,
	`password` VARCHAR(255) NOT NULL,
	`extradata` JSON,
	UNIQUE KEY UIX_connection (`connection`)
);