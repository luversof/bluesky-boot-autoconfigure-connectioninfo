package io.github.luversof.boot.connectioninfo.mongodb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import io.github.luversof.boot.connectioninfo.ConnectionInfo;
import io.github.luversof.boot.connectioninfo.ConnectionInfoKey;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoader;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MongoDbMongoClientConnectionInfoLoader implements ConnectionInfoLoader<MongoClient, MongoDbMongoClientConnectionConfig, MongoDbMongoClientConnectionConfigReader> {
	

	@Getter
	protected String loaderKey = "mongodb-mongoclient";
	
	protected final ConnectionInfoProperties connectionInfoProperties;
	
	@Getter
	protected final MongoDbMongoClientConnectionConfigReader connectionConfigReader;
	
	public MongoDbMongoClientConnectionInfoLoader(ConnectionInfoProperties connectionInfoProperties, MongoDbMongoClientConnectionConfigReader connectionConfigReader) {
		this.connectionInfoProperties = connectionInfoProperties;
		this.connectionConfigReader = connectionConfigReader;
	}

	@Override
	public List<ConnectionInfo<MongoClient>> load() {
		if (connectionInfoProperties == null 
				|| connectionInfoProperties.getLoaders() == null 
				|| !connectionInfoProperties.getLoaders().containsKey(getLoaderKey())
				|| connectionInfoProperties.getLoaders().get(getLoaderKey()).getConnections() == null) {
			return Collections.emptyList();
		}
		
		List<String> connectionList = connectionInfoProperties.getLoaders().get(getLoaderKey()).getConnections().values().stream().flatMap(List::stream).distinct().toList();
		
		return load(connectionList);
	}

	@Override
	public List<ConnectionInfo<MongoClient>> load(List<String> connectionList) {
		if (connectionList == null || connectionList.isEmpty()) {
			return Collections.emptyList();
		}
		
		

		var connectionConfigList = connectionConfigReader.readConnectionConfigList(connectionList);
		
		connectionList.forEach(connection -> {
			
			if (connectionConfigList.stream().anyMatch(connetionInfoResult -> connetionInfoResult.connection().equalsIgnoreCase(connection))) {
				log.debug("find database connection ({})", connection);
			} else {
				log.debug("cannot find database connection ({})", connection);
			}
		});
		
		if (connectionConfigList.isEmpty()) {
			return Collections.emptyList();
		}

		var connectionInfoList = new ArrayList<ConnectionInfo<MongoClient>>();
		for (var connectionConfig : connectionConfigList) {
			connectionInfoList.add(createConnectionInfo(connectionConfig));
		}
		return connectionInfoList;
	}
	
	private ConnectionInfo<MongoClient> createConnectionInfo(MongoDbMongoClientConnectionConfig connectionConfig) {
		MongoClientSettings.builder()
			.applyConnectionString(new ConnectionString(connectionConfig.connectionString()))
			// adjust database
			.build();
		
		var mongoClient = MongoClients.create(connectionConfig.connectionString());
		return new ConnectionInfo<MongoClient>(new ConnectionInfoKey(getLoaderKey(), connectionConfig.connection()), mongoClient);
	}

}