package io.github.luversof.boot.connectioninfo.mongodb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import io.github.luversof.boot.connectioninfo.ConnectionConfigReader;
import io.github.luversof.boot.connectioninfo.ConnectionInfo;
import io.github.luversof.boot.connectioninfo.ConnectionInfoKey;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoader;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MongoDbMongoClientConnectionInfoLoader implements ConnectionInfoLoader<MongoClient, MongoClientConnectionConfig> {
	

	@Getter
	protected String loaderKey = "mongodb-mongoclient";
	
	protected final ConnectionInfoProperties connectionInfoProperties;
	
	@Getter
	protected final List<ConnectionConfigReader<MongoClientConnectionConfig>> connectionConfigReaderList;
	
	public MongoDbMongoClientConnectionInfoLoader(ConnectionInfoProperties connectionInfoProperties, List<ConnectionConfigReader<MongoClientConnectionConfig>> connectionConfigReaderList) {
		this.connectionInfoProperties = connectionInfoProperties;
		this.connectionConfigReaderList = connectionConfigReaderList;
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
		
		
		var connectionConfigReaderList = getConnectionConfigReaderList();
		
		if (connectionConfigReaderList == null || connectionConfigReaderList.isEmpty()) {
			return Collections.emptyList();
		}
		
		var connectionConfigList = new ArrayList<MongoClientConnectionConfig>();
		getConnectionConfigReaderList().forEach(connectionConfigReader -> {
			var readConnectionConfigList = connectionConfigReader.readConnectionConfigList(connectionList);
			if (!CollectionUtils.isEmpty(readConnectionConfigList)) {
				connectionConfigList.addAll(readConnectionConfigList);
			}
		});
		

		connectionList.forEach(connection -> {
			
			if (connectionConfigList.stream().anyMatch(connetionInfoResult -> connetionInfoResult.getConnection().equalsIgnoreCase(connection))) {
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
	
	private ConnectionInfo<MongoClient> createConnectionInfo(MongoClientConnectionConfig connectionConfig) {
		MongoClientSettings.builder()
			.applyConnectionString(new ConnectionString(connectionConfig.getConnectionString()))
			// adjust database
			.build();
		
		var mongoClient = MongoClients.create(connectionConfig.getConnectionString());
		return new ConnectionInfo<MongoClient>(new ConnectionInfoKey(getLoaderKey(), connectionConfig.getConnection()), mongoClient);
	}

}