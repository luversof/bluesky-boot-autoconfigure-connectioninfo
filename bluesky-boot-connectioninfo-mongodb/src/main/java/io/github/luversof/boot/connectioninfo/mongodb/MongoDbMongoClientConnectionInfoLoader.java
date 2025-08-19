package io.github.luversof.boot.connectioninfo.mongodb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bson.Document;
import org.springframework.boot.origin.SystemEnvironmentOrigin;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import io.github.luversof.boot.connectioninfo.ConnectionInfo;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoader;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import io.github.luversof.boot.security.crypto.factory.TextEncryptorFactories;
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
		
		
		try(var loaderMongoClient = getLoaderMongoClient()) {
			var database = loaderMongoClient.getDatabase(getProperties("connectionString"));
			MongoCollection<Document> collection = database.getCollection("ConnectionInfo");

				// query: { "connection": { "$in": [ "conn1", "conn2", "conn3" ] } }
				Document query = new Document("connection", new Document("$in", connectionList));

				FindIterable<Document> results = collection.find(query);

				// 결과 출력
				for (Document doc : results) {
					System.out.println("출력!!@!");
					System.out.println(doc.toJson());
				}
			
		}
		
		// DB 선택
//		MongoDatabase database = mongoClient.getDatabase("testdb");
//
//		// 컬렉션에서 문서 하나 읽기
//		Document doc = database.getCollection("testCollection").find().first();
//		System.out.println("First document: " + doc);


		
		// 어떤식으로 저장하고 호출하는게 좋을까?
		
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
	
	private MongoClient getLoaderMongoClient() {
		return MongoClients.create(getProperties("connectionString"));
	}
	
	private String getProperties(String key) {
		var encryptor = TextEncryptorFactories.getDelegatingTextEncryptor();
		var loaderProperties = connectionInfoProperties.getLoaders().get(getLoaderKey()).getProperties();
		return encryptor.decrypt(loaderProperties.get(key));
	}

}