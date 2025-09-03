package io.github.luversof.boot.connectioninfo.mongodb;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties.ConnectionInfoLoaderProperties;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties.ConnectionInfoReaderProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class MongoDbMongoClientConnectionInfoLoaderTest {

	@Test
	void test() {
		var connectionInfoReaderProperties = new ConnectionInfoReaderProperties();
		connectionInfoReaderProperties.setProperties(Map.of("connectionString", "mongodb://localhost:27017", "database", "connection_info_localdev"));
		
		var connectionInfoLoaderProperties = new ConnectionInfoLoaderProperties();
		connectionInfoLoaderProperties.setEnabled(true);
		connectionInfoLoaderProperties.setConnections(Map.of("connExample", List.of("test1", "test2")));
		
		var connectionInfoProperties = new ConnectionInfoProperties();
		connectionInfoProperties.getReaders().put("mongodb-mongoclient", connectionInfoReaderProperties);
		connectionInfoProperties.getLoaders().put("mongoclient", connectionInfoLoaderProperties);
		
		MongoDbMongoClientConnectionInfoReader connectionConfigReader = new MongoDbMongoClientConnectionInfoReader(connectionInfoProperties);
		
		var mongoDbMongoClientConnectionInfoLoader = new MongoDbMongoClientConnectionInfoLoader(connectionInfoProperties, List.of(connectionConfigReader));
		mongoDbMongoClientConnectionInfoLoader.load();
		log.debug("Test :");
	}
}
