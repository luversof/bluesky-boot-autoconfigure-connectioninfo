package io.github.luversof.boot.connectioninfo.mongodb;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * MongoDB connection-map properties 바인딩
 * 
 * <p>
 * connection-info.mongodb.connection-map.{connectionName}.*
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "bluesky-boot.connection-info.mongodb")
public class MongoDbConnectionMapProperties {

	/**
	 * Key: connectionName
	 * Value: MongoConnectionConfig
	 */
	private Map<String, MongoConnectionConfig> connectionMap = new HashMap<>();

	/**
	 * 개별 MongoDB 연결 설정 (필수 정보만)
	 */
	@Getter
	@Setter
	public static class MongoConnectionConfig {

		/**
		 * 호스트:포트 목록
		 * 단일 서버: "localhost:27017"
		 * Replica Set: "host1:27017,host2:27017,host3:27017"
		 */
		private String hosts;

		/**
		 * 데이터베이스 이름
		 */
		private String database;

		/**
		 * 사용자명 (암호화 가능: {text}encrypted...)
		 */
		private String username;

		/**
		 * 비밀번호 (암호화 가능: {text}encrypted...)
		 */
		private String password;
	}
}
