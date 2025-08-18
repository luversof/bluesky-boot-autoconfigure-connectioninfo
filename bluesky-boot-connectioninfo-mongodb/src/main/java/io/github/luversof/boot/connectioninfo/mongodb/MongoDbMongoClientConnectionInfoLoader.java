package io.github.luversof.boot.connectioninfo.mongodb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import io.github.luversof.boot.connectioninfo.ConnectionInfo;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoader;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import lombok.Getter;

public class MongoDbMongoClientConnectionInfoLoader implements ConnectionInfoLoader<MongoClient> {
	

	@Getter
	protected String loaderKey = "mongodb-mongoclient";
	
	protected final ConnectionInfoProperties connectionInfoProperties;
	
	public MongoDbMongoClientConnectionInfoLoader(ConnectionInfoProperties connectionInfoProperties) {
		this.connectionInfoProperties = connectionInfoProperties;
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
		List<ConnectionInfo<MongoClient>> result = new ArrayList<>();
//		for (String uri : connectionList) {
//			MongoClient client = MongoClients.create(uri);
//			ConnectionInfo<MongoClient> info = new ConnectionInfo<>();
//			info.setConnection(client);
//			info.setConnectionString(uri);
//			result.add(info);
//		}
		return result;
	}

}