package io.github.luversof.boot.autoconfigure.connectioninfo.jdbc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import io.github.luversof.boot.autoconfigure.connectioninfo.ConnectionInfoAutoConfiguration;
import io.github.luversof.boot.connectioninfo.ConnectionConfigProperties;
import io.github.luversof.boot.connectioninfo.ConnectionConfigProperties.ConnectionConfigReaderProperties;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties.ConnectionInfoLoaderProperties;
import io.github.luversof.boot.connectioninfo.ConnectionInfoRegistry;
import io.github.luversof.boot.connectioninfo.jdbc.DataSourceConnectionConfig;
import io.github.luversof.boot.connectioninfo.jdbc.MariaDbDataSourceConnectionConfigReader;
import io.github.luversof.boot.connectioninfo.jdbc.MariaDbHikariDataSourceConnectionInfoLoader;
import io.github.luversof.boot.security.crypto.env.DecryptEnvironmentPostProcessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class ConnectionInfoTests {


	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withInitializer(ConditionEvaluationReportLoggingListener.forLogLevel(LogLevel.INFO))
			.withInitializer(applicationContext -> new DecryptEnvironmentPostProcessor().postProcessEnvironment(applicationContext.getEnvironment(), null))
			.withPropertyValues("spring.profiles.active=localdev")
			.withPropertyValues(
				"bluesky-boot.connection-config.readers.mariadb-datasource.enabled=true",
				"bluesky-boot.connection-config.readers.mariadb-datasource.properties.url=jdbc:mariadb://mariadb.bluesky.local:3306/connection_info_localdev",
				"bluesky-boot.connection-config.readers.mariadb-datasource.properties.username={text}df9b549831440b381434dee7d1e7169eb80e2453abb28e184358f552cf561b33",
				"bluesky-boot.connection-config.readers.mariadb-datasource.properties.password={text}df9b549831440b381434dee7d1e7169eb80e2453abb28e184358f552cf561b33",
				"bluesky-boot.connection-info.loaders.mariadb-datasource.enabled=true",
				"bluesky-boot.connection-info.loaders.mariadb-datasource.connections.mapexample=test1",
				"bluesky-boot.connection-config.readers.sqlserver-datasource.enabled=true",
				"bluesky-boot.connection-config.readers.sqlserver-datasource.properties.url=jdbc:sqlserver://mssql.bluesky.local;encrypt=false;databaseName=connection_info",
				"bluesky-boot.connection-config.readers.sqlserver-datasource.properties.username={text}402dc2850a816b6594e8b2d0293cb87251209160720a66b1f2a25c9760df58ac",
				"bluesky-boot.connection-config.readers.sqlserver-datasource.properties.password={text}0704d3da6333d293af85804a0ea58884734b4283026956835e8de40ce0d3480e",
				"bluesky-boot.connection-info.loaders.sqlserver-datasource.enabled=true",
				"bluesky-boot.connection-info.loaders.sqlserver-datasource.connections.mapexample=test1"
			)
			.withUserConfiguration(ConnectionInfoAutoConfiguration.class)
			.withUserConfiguration(ConnectionInfoJdbcAutoConfiguration.class)
			;
	
	@Test
	void connectionInfoProperties() {
		this.contextRunner.run(context -> {
			var connectionInfoProperties = context.getBean(ConnectionInfoProperties.class);
			assertThat(connectionInfoProperties).isNotNull();
		});
		
	}
	
	@Test
	<C extends DataSourceConnectionConfig> void mariaDbDataSourceConnectionInfoTest() {
		
		var connectionConfigProperties = new ConnectionConfigProperties();
		connectionConfigProperties.setReaders(
				Map.of(
					"mariadb-datasource", 
					ConnectionConfigReaderProperties.builder()
						.properties(
							Map.of(
								"url", "jdbc:mariadb://mariadb.bluesky.local:3306/connection_info_localdev",
								"username", "root",
								"password", "root"
							)
						)
					.build()
				)
			);
		
		var connectionConfigReader = new MariaDbDataSourceConnectionConfigReader(connectionConfigProperties);
		
		var connectionInfoProperties = new ConnectionInfoProperties();
		connectionInfoProperties.setLoaders(
				Map.of("mariadb-datasource", 
			ConnectionInfoLoaderProperties.builder()
					.connections(Map.of("test", List.of("test1")))
					.build()
				)
		);
		
		var connectionInfoLoader = new MariaDbHikariDataSourceConnectionInfoLoader<DataSourceConnectionConfig>(connectionInfoProperties, List.of(connectionConfigReader));
		var connectionInfoList = connectionInfoLoader.load();
		
		log.debug("connectionInfoList : {}", connectionInfoList);
		
		
		var jdbcTemplate = new JdbcTemplate(connectionInfoList.stream().filter(x -> x.getKey().connectionKey().equals("test1")).findFirst().get().getConnection());
		var connectionInfo = jdbcTemplate.queryForList("SELECT * FROM ConnectionInfo");
		log.debug("connectionInfo : {}", connectionInfo);
		
	}
	
	
	@Test
	void mariaDbDataSourceConnectionInfoReaderTest() {
		this.contextRunner.run(context -> {
			var connectionConfigProperties = context.getBean(ConnectionConfigProperties.class);
			
			log.debug("connectionConfigProperties : {}", connectionConfigProperties);
			log.debug("connectionConfigProperties username : {}", connectionConfigProperties.getReaders().get("mariadb-datasource").getProperties().get("username"));
			
			var connectionInfoRegistry = context.getBean(ConnectionInfoRegistry.class);
			log.debug("connectionInfoRegistry : {}", connectionInfoRegistry.getConnectionInfoList());
		});
	}
	
	@Test
	void sqlServerDataSourceConnectionInfoReaderTest() {
		this.contextRunner.run(context -> {
			var connectionConfigProperties = context.getBean(ConnectionConfigProperties.class);
			
			log.debug("connectionConfigProperties : {}", connectionConfigProperties);
			log.debug("connectionConfigProperties username : {}", connectionConfigProperties.getReaders().get("sqlserver-datasource").getProperties().get("username"));
			
			var connectionInfoRegistry = context.getBean(ConnectionInfoRegistry.class);
			log.debug("connectionInfoRegistry : {}", connectionInfoRegistry.getConnectionInfoList());
		});
	}
}
