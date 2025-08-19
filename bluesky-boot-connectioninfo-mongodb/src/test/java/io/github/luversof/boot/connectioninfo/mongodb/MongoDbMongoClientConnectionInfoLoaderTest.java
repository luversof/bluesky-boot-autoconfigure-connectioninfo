package io.github.luversof.boot.connectioninfo.mongodb;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties.ConnectionInfoLoaderProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class MongoDbMongoClientConnectionInfoLoaderTest {

	@Test
	void test() {
		var connectionInfoLoaderProperties = new ConnectionInfoLoaderProperties();
		connectionInfoLoaderProperties.setEnabled(true);
		connectionInfoLoaderProperties.setProperties(Map.of("connectionString", "mongodb://localhost:27017", "database", "connection_info_localdev"));
		connectionInfoLoaderProperties.setConnections(Map.of("connExample", List.of("test1", "test2")));
		
		var connectionInfoProperties = new ConnectionInfoProperties();
		connectionInfoProperties.getLoaders().put("mongodb-mongoclient", connectionInfoLoaderProperties);
		
		
		var mongoDbMongoClientConnectionInfoLoader = new MongoDbMongoClientConnectionInfoLoader(connectionInfoProperties);
		mongoDbMongoClientConnectionInfoLoader.load();
		log.debug("Test :");
	}
}
