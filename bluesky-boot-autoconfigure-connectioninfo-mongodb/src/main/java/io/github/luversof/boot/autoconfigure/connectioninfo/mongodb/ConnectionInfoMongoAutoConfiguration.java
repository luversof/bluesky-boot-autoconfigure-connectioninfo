package io.github.luversof.boot.autoconfigure.connectioninfo.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.mongodb.client.MongoClient;

import io.github.luversof.boot.autoconfigure.connectioninfo.ConnectionInfoAutoConfiguration;
import io.github.luversof.boot.connectioninfo.ConnectionInfo;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoader;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import io.github.luversof.boot.connectioninfo.ConnectionInfoReader;
import io.github.luversof.boot.connectioninfo.ConnectionInfoRegistry;
import io.github.luversof.boot.connectioninfo.MongoClientConnectionConfig;
import io.github.luversof.boot.connectioninfo.mongodb.MongoDbMongoClientConnectionInfoLoader;
import io.github.luversof.boot.connectioninfo.mongodb.MongoDbMongoClientConnectionInfoReader;
import io.github.luversof.boot.connectioninfo.mongodb.PropertiesMongoClientConnectionInfoReader;

@AutoConfiguration(
		value = "blueskyBootConnectionInfoJdbcAutoConfiguration", 
		before = {
			MongoAutoConfiguration.class,
			ConnectionInfoAutoConfiguration.class
		}
	)
@ConditionalOnClass(MongoClient.class)
@PropertySource("classpath:connectioninfo-mongodb-defaults.properties")
public class ConnectionInfoMongoAutoConfiguration {

	@Bean
	@ConditionalOnProperty(prefix = "bluesky-boot.connection-info.readers", name = "mongo-mongoclient.enabled", havingValue = "true")
	MongoDbMongoClientConnectionInfoReader mongoDbMongoClientConnectionInfoReader(ConnectionInfoProperties connectionInfoProperties) {
		return new MongoDbMongoClientConnectionInfoReader(connectionInfoProperties);
	}
	
	@Bean
	@ConditionalOnProperty(prefix = "bluesky-boot.connection-info.readers", name = "properties-mongoclient.enabled", havingValue = "true")
	PropertiesMongoClientConnectionInfoReader propertiesMongoClientConnectionInfoReader(Environment environment, ConnectionInfoProperties connectionInfoProperties) {
		return new PropertiesMongoClientConnectionInfoReader(environment, connectionInfoProperties);
	}
	
	@Bean
	@ConditionalOnProperty(prefix = "bluesky-boot.connection-info.loaders", name = "mongoclient.enabled", havingValue = "true")
	MongoDbMongoClientConnectionInfoLoader mongoDbMongoClientConnectionInfoLoader(ConnectionInfoProperties connectionInfoProperties, List<ConnectionInfoReader<MongoClientConnectionConfig>> connectionInfoReaderList) {
		return new MongoDbMongoClientConnectionInfoLoader(connectionInfoProperties, connectionInfoReaderList);
	}

	@Bean
	ConnectionInfoRegistry<MongoClient> mongoClientConnectionInfoRegistry(List<ConnectionInfoLoader<MongoClient, MongoClientConnectionConfig>> connectionInfoLoaderList) {
		var connectionInfoList = new ArrayList<ConnectionInfo<MongoClient>>();
		connectionInfoLoaderList.forEach(connectionInfoLoader -> connectionInfoList.addAll(connectionInfoLoader.load()));
		return () -> connectionInfoList;
	}

}
