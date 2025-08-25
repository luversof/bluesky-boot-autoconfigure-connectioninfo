package io.github.luversof.boot.connectioninfo.mongodb;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.github.luversof.boot.connectioninfo.ConnectionConfigProperties;
import io.github.luversof.boot.connectioninfo.ConnectionConfigProperties.ConnectionConfigReaderProperties;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties.ConnectionInfoLoaderProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class MongoDbMongoClientConnectionInfoLoaderTest {

	@Test
	void test() {
		var connectionConfigReaderProperties = new ConnectionConfigReaderProperties();
		connectionConfigReaderProperties.setProperties(Map.of("connectionString", "mongodb://localhost:27017", "database", "connection_info_localdev"));
		
		var connectionConfigProperties = new ConnectionConfigProperties();
		connectionConfigProperties.getReaders().put("mongodb-mongoclient", connectionConfigReaderProperties);
		
		var connectionInfoLoaderProperties = new ConnectionInfoLoaderProperties();
		connectionInfoLoaderProperties.setEnabled(true);
		connectionInfoLoaderProperties.setConnections(Map.of("connExample", List.of("test1", "test2")));
		
		var connectionInfoProperties = new ConnectionInfoProperties();
		connectionInfoProperties.getLoaders().put("mongodb-mongoclient", connectionInfoLoaderProperties);
		
		MongoDbMongoClientConnectionConfigReader connectionConfigReader = new MongoDbMongoClientConnectionConfigReader(connectionConfigProperties);
		
		var mongoDbMongoClientConnectionInfoLoader = new MongoDbMongoClientConnectionInfoLoader(connectionInfoProperties, connectionConfigReader);
		mongoDbMongoClientConnectionInfoLoader.load();
		log.debug("Test :");
	}
}
