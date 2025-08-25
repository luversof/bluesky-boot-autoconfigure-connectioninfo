package io.github.luversof.boot.connectioninfo.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import io.github.luversof.boot.connectioninfo.ConnectionConfigProperties;
import io.github.luversof.boot.connectioninfo.ConnectionConfigReader;
import io.github.luversof.boot.security.crypto.factory.TextEncryptorFactories;
import lombok.Getter;

public class MongoDbMongoClientConnectionConfigReader implements ConnectionConfigReader<MongoDbMongoClientConnectionConfig>{

	@Getter
	protected String readerKey = "mongodb-mongoclient";
	
	protected final ConnectionConfigProperties connectionConfigProperties;
	
	public MongoDbMongoClientConnectionConfigReader(ConnectionConfigProperties connectionConfigProperties) {
		this.connectionConfigProperties = connectionConfigProperties;
	}

	@Override
	public List<MongoDbMongoClientConnectionConfig> readConnectionConfigList(List<String> connectionList) {
		try(var loaderMongoClient = getLoaderMongoClient()) {
			var mongoDatabase = loaderMongoClient.getDatabase(getConfigProperties("database"));
			var mongoCollection = mongoDatabase.getCollection("ConnectionInfo");
			var query = new Document("connection", new Document("$in", connectionList));
			var mongoDbMongoClientConnectionConfigList = mongoCollection.find(query, MongoDbMongoClientConnectionConfig.class);
			return mongoDbMongoClientConnectionConfigList.into(new ArrayList<>());
		}
	}

	private MongoClient getLoaderMongoClient() {
		return MongoClients.create(getConfigProperties("connectionString"));
	}
	
	private String getConfigProperties(String key) {
		var encryptor = TextEncryptorFactories.getDelegatingTextEncryptor();
		var loaderProperties = connectionConfigProperties.getReaders().get(getReaderKey()).getProperties();
		return encryptor.decrypt(loaderProperties.get(key));
	}
}
