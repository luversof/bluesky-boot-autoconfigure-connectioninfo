package io.github.luversof.boot.connectioninfo;

import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Data;

@Data
public class MongoClientConnectionConfig implements ConnectionConfig {

	@BsonProperty("connection")
	private String connection;
	
	@BsonProperty("connectionString")
	private String connectionString;
	
	@BsonProperty("database")
	private String database;
	
	@BsonProperty("userName")
	private String userName;
	
	@BsonProperty("password")
	private String password;

}