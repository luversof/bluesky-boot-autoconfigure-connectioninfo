package io.github.luversof.boot.connectioninfo.mongodb;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * MongoDB 기본 설정값 Properties
 * 
 * <p>
 * connection-info.mongodb.* (connection-map 제외)
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "bluesky-boot.connection-info.mongodb")
public class MongoDbDefaultProperties {

	/**
	 * 기본 호스트
	 */
	private String host = "localhost";

	/**
	 * 기본 포트
	 */
	private Integer port = 27017;

	/**
	 * 인증 데이터베이스
	 */
	private String authenticationDatabase = "admin";

	/**
	 * ReadPreference: primary, primaryPreferred, secondary, secondaryPreferred,
	 * nearest
	 */
	private String readPreference = "secondaryPreferred";

	/**
	 * ReadConcern level: local, majority, linearizable
	 */
	private String readConcernLevel = "local";

	/**
	 * UUID Representation: JAVA_LEGACY, STANDARD, PYTHON_LEGACY, C_SHARP_LEGACY,
	 * UNSPECIFIED
	 */
	private String uuidRepresentation = "JAVA_LEGACY";

	/**
	 * Connection Pool 설정
	 */
	private ConnectionPool connectionPool = new ConnectionPool();

	/**
	 * Write Concern 설정
	 */
	private WriteConcern writeConcern = new WriteConcern();

	/**
	 * Connection Pool 설정
	 */
	@Getter
	@Setter
	public static class ConnectionPool {
		private Integer maxSize = 30;
		private Integer minSize = 10;
		private Long maxWaitTimeMs = 120000L;
		private Long maxConnectionLifeTimeMs = 0L;
		private Long maxConnectionIdleTimeMs = 0L;
	}

	/**
	 * Write Concern 설정
	 */
	@Getter
	@Setter
	public static class WriteConcern {
		/**
		 * W1, W2, W3, MAJORITY, ACKNOWLEDGED, UNACKNOWLEDGED
		 */
		private String w = "W1";
		private Integer wTimeoutMs = 5000;
		private Boolean journal = false;
	}
}
