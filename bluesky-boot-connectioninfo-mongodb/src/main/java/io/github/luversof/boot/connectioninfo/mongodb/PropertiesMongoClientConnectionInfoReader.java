package io.github.luversof.boot.connectioninfo.mongodb;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bson.UuidRepresentation;
import org.springframework.core.env.Environment;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ReadConcern;
import com.mongodb.ReadConcernLevel;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.connection.ClusterConnectionMode;

import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import io.github.luversof.boot.connectioninfo.ConnectionInfoReader;
import io.github.luversof.boot.connectioninfo.MongoClientConnectionConfig;
import io.github.luversof.boot.security.crypto.factory.TextEncryptorFactories;
import lombok.Getter;

/**
 * Properties 파일에서 MongoDB 연결 정보를 읽어오는 Reader
 * 
 * <p>기본 설정은 connection-info.mongodb.* 에서 읽으며, 프로젝트의 application.properties에서 override 가능합니다.
 * 
 * <p>기본 설정 예시:
 * <pre>
 * # 기본 연결 설정
 * bluesky-boot.connection-info.mongodb.host=localhost
 * bluesky-boot.connection-info.mongodb.port=27017
 * bluesky-boot.connection-info.mongodb.authentication-database=admin
 * 
 * # Connection Pool 설정
 * bluesky-boot.connection-info.mongodb.connection-pool.max-size=30
 * bluesky-boot.connection-info.mongodb.connection-pool.min-size=10
 * bluesky-boot.connection-info.mongodb.connection-pool.max-wait-time-ms=120000
 * 
 * # Read/Write 설정
 * bluesky-boot.connection-info.mongodb.read-concern-level=local
 * bluesky-boot.connection-info.mongodb.read-preference=secondaryPreferred
 * bluesky-boot.connection-info.mongodb.write-concern.w=W1
 * bluesky-boot.connection-info.mongodb.write-concern.w-timeout-ms=5000
 * bluesky-boot.connection-info.mongodb.write-concern.journal=false
 * 
 * # UUID Representation
 * bluesky-boot.connection-info.mongodb.uuid-representation=JAVA_LEGACY
 * </pre>
 * 
 * <p>개별 연결 정보:
 * <pre>
 * bluesky-boot.connection-info.mongodb.connection-map.{connectionName}.hosts=host1:port1,host2:port2
 * bluesky-boot.connection-info.mongodb.connection-map.{connectionName}.database=dbname
 * bluesky-boot.connection-info.mongodb.connection-map.{connectionName}.username={text}encryptedUser
 * bluesky-boot.connection-info.mongodb.connection-map.{connectionName}.password={text}encryptedPassword
 * </pre>
 */
public class PropertiesMongoClientConnectionInfoReader implements ConnectionInfoReader<MongoClientConnectionConfig> {

	@Getter
	protected String readerKey = "properties-mongoclient";
	
	private static final String BASE_PREFIX = "bluesky-boot.connection-info.mongodb";
	private static final String CONNECTION_MAP_PREFIX = BASE_PREFIX + ".connection-map";
	
	protected final Environment environment;
	protected final ConnectionInfoProperties connectionInfoProperties;
	
	public PropertiesMongoClientConnectionInfoReader(Environment environment, ConnectionInfoProperties connectionInfoProperties) {
		this.environment = environment;
		this.connectionInfoProperties = connectionInfoProperties;
	}

	@Override
	public List<MongoClientConnectionConfig> readConnectionConfigList(List<String> connectionList) {
		List<MongoClientConnectionConfig> configList = new ArrayList<>();
		var encryptor = TextEncryptorFactories.getDelegatingTextEncryptor();
		
		for (String connectionName : connectionList) {
			String prefix = CONNECTION_MAP_PREFIX + "." + connectionName;
			
			// 필수 속성 확인
			String hostsStr = environment.getProperty(prefix + ".hosts");
			String host = environment.getProperty(prefix + ".host");
			Integer port = environment.getProperty(prefix + ".port", Integer.class);
			String database = environment.getProperty(prefix + ".database");
			
			if (database == null || (hostsStr == null && host == null)) {
				// 해당 connection 정보가 properties에 없으면 skip
				continue;
			}
			
			MongoClientConnectionConfig config = new MongoClientConnectionConfig();
			config.setConnection(connectionName);
			config.setDatabase(database);
			
			// username/password는 암호화되어 있을 수 있으므로 복호화
			String username = environment.getProperty(prefix + ".username");
			if (username != null) {
				config.setUserName(encryptor.decrypt(username));
			}
			
			String password = environment.getProperty(prefix + ".password");
			if (password != null) {
				config.setPassword(encryptor.decrypt(password));
			}
			
			// MongoDB connectionString을 MongoUtil 방식으로 생성
			String connectionString = buildMongoClientSettings(connectionName, hostsStr, host, port, 
					database, config.getUserName(), config.getPassword());
			
			config.setConnectionString(connectionString);
			
			configList.add(config);
		}
		
		return configList;
	}
	
	/**
	 * MongoUtil의 getMongoClient 로직을 참고하여 MongoClientSettings를 생성하고 connectionString으로 반환
	 * 
	 * @param connectionName 연결 이름
	 * @param hostsStr 호스트:포트 목록 (쉼표 구분)
	 * @param host 단일 호스트
	 * @param port 단일 포트
	 * @param database 데이터베이스 이름
	 * @param username 사용자명 (복호화 완료)
	 * @param password 비밀번호 (복호화 완료)
	 * @return MongoDB connectionString
	 */
	private String buildMongoClientSettings(String connectionName, String hostsStr, String host, Integer port,
			String database, String username, String password) {
		
		// MongoClientSettings builder 생성 (기본 설정 적용)
		MongoClientSettings.Builder builder = MongoClientSettings.builder();
		
		// 1. 기본 설정 적용
		applyDefaultSettings(builder);
		
		// 2. 개별 연결별 override 설정 적용
		applyConnectionSpecificSettings(builder, connectionName);
		
		// 3. Cluster 설정 (hosts 또는 host/port)
		if (hostsStr != null) {
			// 여러 서버 (replica set)
			List<ServerAddress> serverAddressList = new ArrayList<>();
			String[] hosts = hostsStr.split(",");
			for (String h : hosts) {
				h = h.trim();
				if (h.contains(":")) {
					String[] parts = h.split(":");
					serverAddressList.add(new ServerAddress(parts[0].trim(), Integer.parseInt(parts[1].trim())));
				} else {
					serverAddressList.add(new ServerAddress(h, getDefaultPort()));
				}
			}
			builder.applyToClusterSettings(cluster -> 
				cluster.hosts(serverAddressList).mode(ClusterConnectionMode.MULTIPLE));
		} else if (host != null) {
			// 단일 서버
			int finalPort = port != null ? port : getDefaultPort();
			builder.applyToClusterSettings(cluster -> 
				cluster.hosts(List.of(new ServerAddress(host, finalPort))));
		}
		
		// Authentication Database 가져오기
		String authDatabase = getAuthenticationDatabase(connectionName);
		
		// 4. Credential 설정
		if (username != null && password != null) {
			builder.credential(MongoCredential.createCredential(username, authDatabase, password.toCharArray()));
		}
		
		// 5. ReplicaSet 이름 설정 (있는 경우)
		String replicaSetName = getConnectionProperty(connectionName, "replica-set-name");
		if (replicaSetName != null) {
			builder.applyToClusterSettings(cluster -> cluster.requiredReplicaSetName(replicaSetName));
		}
		
		// MongoClientSettings를 connectionString 형식으로 변환
		// 실제로는 MongoClientSettings를 직접 사용하는 것이 더 좋지만, 
		// 기존 ConnectionConfig 구조에 맞추기 위해 간단한 connectionString 생성
		return buildConnectionString(hostsStr, host, port, database, username, password, authDatabase);
	}
	
	/**
	 * 기본 설정을 MongoClientSettings.Builder에 적용
	 */
	private void applyDefaultSettings(MongoClientSettings.Builder builder) {
		// Connection Pool 설정
		Integer maxSize = getDefaultProperty("connection-pool.max-size", Integer.class);
		Integer minSize = getDefaultProperty("connection-pool.min-size", Integer.class);
		Long maxWaitTimeMs = getDefaultProperty("connection-pool.max-wait-time-ms", Long.class);
		Long maxLifeTimeMs = getDefaultProperty("connection-pool.max-connection-life-time-ms", Long.class);
		Long maxIdleTimeMs = getDefaultProperty("connection-pool.max-connection-idle-time-ms", Long.class);
		
		builder.applyToConnectionPoolSettings(poolBuilder -> {
			if (maxSize != null) poolBuilder.maxSize(maxSize);
			if (minSize != null) poolBuilder.minSize(minSize);
			if (maxWaitTimeMs != null) poolBuilder.maxWaitTime(maxWaitTimeMs, TimeUnit.MILLISECONDS);
			if (maxLifeTimeMs != null) poolBuilder.maxConnectionLifeTime(maxLifeTimeMs, TimeUnit.MILLISECONDS);
			if (maxIdleTimeMs != null) poolBuilder.maxConnectionIdleTime(maxIdleTimeMs, TimeUnit.MILLISECONDS);
		});
		
		// ReadConcern 설정
		String readConcernLevel = getDefaultProperty("read-concern-level", String.class);
		if (readConcernLevel != null) {
			builder.readConcern(new ReadConcern(ReadConcernLevel.fromString(readConcernLevel)));
		}
		
		// ReadPreference 설정
		String readPreference = getDefaultProperty("read-preference", String.class);
		if (readPreference != null) {
			builder.readPreference(ReadPreference.valueOf(readPreference));
		}
		
		// WriteConcern 설정
		String writeConcernW = getDefaultProperty("write-concern.w", String.class);
		Integer writeConcernTimeout = getDefaultProperty("write-concern.w-timeout-ms", Integer.class);
		Boolean writeConcernJournal = getDefaultProperty("write-concern.journal", Boolean.class);
		
		if (writeConcernW != null) {
			WriteConcern writeConcern = WriteConcern.valueOf(writeConcernW);
			if (writeConcernTimeout != null) {
				writeConcern = writeConcern.withWTimeout(writeConcernTimeout, TimeUnit.MILLISECONDS);
			}
			if (writeConcernJournal != null) {
				writeConcern = writeConcern.withJournal(writeConcernJournal);
			}
			builder.writeConcern(writeConcern);
		}
		
		// UUID Representation 설정
		String uuidRepresentation = getDefaultProperty("uuid-representation", String.class);
		if (uuidRepresentation != null) {
			builder.uuidRepresentation(UuidRepresentation.valueOf(uuidRepresentation));
		} else {
			builder.uuidRepresentation(UuidRepresentation.JAVA_LEGACY);
		}
	}
	
	/**
	 * 개별 연결별 override 설정 적용
	 */
	private void applyConnectionSpecificSettings(MongoClientSettings.Builder builder, String connectionName) {
		String prefix = CONNECTION_MAP_PREFIX + "." + connectionName;
		
		// Connection Pool override
		Integer maxSize = environment.getProperty(prefix + ".connection-pool-max-size", Integer.class);
		if (maxSize != null) {
			builder.applyToConnectionPoolSettings(poolBuilder -> poolBuilder.maxSize(maxSize));
		}
		
		// ReadPreference override
		String readPreference = environment.getProperty(prefix + ".read-preference");
		if (readPreference != null) {
			builder.readPreference(ReadPreference.valueOf(readPreference));
		}
		
		// ReadConcern override
		String readConcernLevel = environment.getProperty(prefix + ".read-concern-level");
		if (readConcernLevel != null) {
			builder.readConcern(new ReadConcern(ReadConcernLevel.fromString(readConcernLevel)));
		}
	}
	
	/**
	 * 기본 설정 properties에서 값을 가져옴
	 * connection-info.mongodb.{key} 형식으로 읽음
	 */
	private <T> T getDefaultProperty(String key, Class<T> targetType) {
		return environment.getProperty(BASE_PREFIX + "." + key, targetType);
	}
	
	/**
	 * 개별 연결 설정 값 가져오기
	 */
	private String getConnectionProperty(String connectionName, String key) {
		return environment.getProperty(CONNECTION_MAP_PREFIX + "." + connectionName + "." + key);
	}
	
	/**
	 * 기본 포트 가져오기
	 */
	private int getDefaultPort() {
		Integer port = getDefaultProperty("port", Integer.class);
		return port != null ? port : 27017;
	}
	
	/**
	 * Authentication Database 가져오기
	 */
	private String getAuthenticationDatabase(String connectionName) {
		String authDb = getConnectionProperty(connectionName, "authentication-database");
		if (authDb != null) {
			return authDb;
		}
		
		String defaultAuthDb = getDefaultProperty("authentication-database", String.class);
		return defaultAuthDb != null ? defaultAuthDb : "admin";
	}
	
	/**
	 * MongoDB connectionString을 생성합니다.
	 */
	private String buildConnectionString(String hostsStr, String host, Integer port,
			String database, String username, String password, String authDatabase) {
		
		StringBuilder connectionString = new StringBuilder("mongodb://");
		
		// username/password가 있으면 추가
		if (username != null && password != null) {
			connectionString.append(username)
				.append(":")
				.append(password)
				.append("@");
		}
		
		// hosts 추가
		if (hostsStr != null) {
			connectionString.append(hostsStr);
		} else if (host != null) {
			connectionString.append(host);
			if (port != null) {
				connectionString.append(":").append(port);
			} else {
				connectionString.append(":").append(getDefaultPort());
			}
		}
		
		// database 추가
		connectionString.append("/").append(database);
		
		// authSource 추가
		if (username != null && authDatabase != null) {
			connectionString.append("?authSource=").append(authDatabase);
		}
		
		return connectionString.toString();
	}
}
