package io.github.luversof.boot.connectioninfo.mongodb;

import io.github.luversof.boot.connectioninfo.ConnectionConfig;

public record MongoDbMongoClientConnectionConfig(String connection, String connectionString, String database, String userName, String password) implements ConnectionConfig {

}
