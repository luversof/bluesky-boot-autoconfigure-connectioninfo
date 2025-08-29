package io.github.luversof.boot.connectioninfo;

import lombok.Data;

@Data
public class MongoClientConnectionConfig implements ConnectionConfig {

	private String connection;
	
	private String connectionString;
	
	private String database;
	
	private String userName;
	
	private String password;

}
