package io.github.luversof.boot.autoconfigure.connectioninfo.mongodb;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;

import com.mongodb.client.MongoClient;

import io.github.luversof.boot.autoconfigure.connectioninfo.ConnectionInfoAutoConfiguration;
import io.github.luversof.boot.connectioninfo.ConnectionConfigProperties;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import io.github.luversof.boot.connectioninfo.mongodb.MongoDbMongoClientConnectionConfigReader;
import io.github.luversof.boot.connectioninfo.mongodb.MongoDbMongoClientConnectionInfoLoader;

@AutoConfiguration(
		value = "blueskyBootConnectionInfoJdbcAutoConfiguration", 
		before = {
			MongoAutoConfiguration.class,
			ConnectionInfoAutoConfiguration.class
		}
	)
@ConditionalOnClass(MongoClient.class)
@ConditionalOnProperty(prefix = "bluesky-boot.connection-config.readers", name = "mongo-mongoclient.enabled", havingValue = "true")
@ConditionalOnProperty(prefix = "bluesky-boot.connection-info.loaders", name = "mongo-mongoclient.enabled", havingValue = "true")
public class ConnectionInfoMongoAutoConfiguration {

	@Bean
	MongoDbMongoClientConnectionConfigReader mongoDbMongoClientConnectionConfigReader(ConnectionConfigProperties connectionConfigProperties) {
		return new MongoDbMongoClientConnectionConfigReader(connectionConfigProperties);
	}
	
	@Bean
	MongoDbMongoClientConnectionInfoLoader mongoDbMongoClientConnectionInfoLoader(ConnectionInfoProperties connectionInfoProperties, MongoDbMongoClientConnectionConfigReader mongoDbMongoClientConnectionConfigReader) {
		return new MongoDbMongoClientConnectionInfoLoader(connectionInfoProperties, mongoDbMongoClientConnectionConfigReader);
	}

}
