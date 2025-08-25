package io.github.luversof.boot.connectioninfo.mongodb;

import io.github.luversof.boot.connectioninfo.ConnectionConfig;
import lombok.Data;

@Data
public class MongoDbMongoClientConnectionConfig implements ConnectionConfig {

	private String connection;
	
	private String connectionString;
	
	private String database;
	
	private String userName;
	
	private String password;

}
