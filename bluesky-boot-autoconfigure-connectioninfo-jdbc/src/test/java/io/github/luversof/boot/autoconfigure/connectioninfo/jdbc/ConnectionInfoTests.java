package io.github.luversof.boot.autoconfigure.connectioninfo.jdbc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import io.github.luversof.boot.autoconfigure.connectioninfo.ConnectionInfoAutoConfiguration;
import io.github.luversof.boot.connectioninfo.ConnectionConfigProperties;
import io.github.luversof.boot.connectioninfo.ConnectionConfigProperties.ConnectionConfigReaderProperties;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties.ConnectionInfoLoaderProperties;
import io.github.luversof.boot.connectioninfo.ConnectionInfoRegistry;
import io.github.luversof.boot.connectioninfo.jdbc.MariaDbDataSourceConnectionConfigReader;
import io.github.luversof.boot.connectioninfo.jdbc.MariaDbHikariDataSourceConnectionInfoLoader;
import io.github.luversof.boot.security.crypto.env.DecryptEnvironmentPostProcessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Disabled
@ActiveProfiles("localDev")
class ConnectionInfoTests {


	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withInitializer(ConditionEvaluationReportLoggingListener.forLogLevel(LogLevel.INFO))
			.withInitializer(applicationContext -> new DecryptEnvironmentPostProcessor().postProcessEnvironment(applicationContext.getEnvironment(), null))
			.withPropertyValues("spring.profiles.active=localdev")
			.withPropertyValues(
				"bluesky-boot.connection-config.readers.mariadb-datasource.enabled=true",
				"bluesky-boot.connection-config.readers.mariadb-datasource.properties.url=jdbc:mariadb://mariadb.bluesky.local:3306/connection_info",
				"bluesky-boot.connection-config.readers.mariadb-datasource.properties.username={text}dd2d9a9a3735b9f9a63664dca900b04e34d92759a43d301c74dd60d235c9576c",
				"bluesky-boot.connection-config.readers.mariadb-datasource.properties.password={text}dd2d9a9a3735b9f9a63664dca900b04e34d92759a43d301c74dd60d235c9576c",
				"bluesky-boot.connection-info.loaders.mariadb-datasource.enabled=true",
				"bluesky-boot.connection-info.loaders.mariadb-datasource.connections.mapexample=test1",
				"bluesky-boot.connection-config.readers.sqlserver-datasource.enabled=true",
				"bluesky-boot.connection-config.readers.sqlserver-datasource.properties.url=jdbc:sqlserver://mssql.bluesky.local;encrypt=false;databaseName=connection_info",
				"bluesky-boot.connection-config.readers.sqlserver-datasource.properties.username={text}6dfa79bdb4311fe011683a2fbf1b281eb6bfe47523575919533e1c0a99986dfa",
				"bluesky-boot.connection-config.readers.sqlserver-datasource.properties.password={text}cd59e88989c267f8e68e5195fd9e8cc16110118a78f04f14da9f72aa4eda0b85",
				"bluesky-boot.connection-info.loaders.sqlserver-datasource.enabled=true",
				"bluesky-boot.connection-info.loaders.sqlserver-datasource.connections.mapexample=test1"
			)
			.withPropertyValues("bluesky-boot.core.modules.test.domain.web=http://localhost")
			.withPropertyValues("bluesky-boot.core.modules.test.core-module-info=T(io.github.luversof.boot.autoconfigure.core.constant.TestCoreModuleInfo).TEST")
			.withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class))
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
	void mariaDbDataSourceConnectionInfoTest() {
		
		var connectionConfigProperties = new ConnectionConfigProperties();
		connectionConfigProperties.setReaders(
				Map.of(
					"mariadb-datasource", 
					ConnectionConfigReaderProperties.builder()
						.properties(
							Map.of(
								"url", "jdbc:mariadb://mariadb.bluesky.local:3306/connection_info",
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
		
		var connectionInfoLoader = new MariaDbHikariDataSourceConnectionInfoLoader(connectionInfoProperties, connectionConfigReader);
		var connectionInfoList = connectionInfoLoader.load();
		
		log.debug("connectionInfoList : {}", connectionInfoList);
		
		
		var jdbcTemplate = new JdbcTemplate(connectionInfoList.stream().filter(x -> x.getKey().connectionKey().equals("test1")).findFirst().get().getConnection());
		var connectionInfo = jdbcTemplate.queryForList("SELECT * FROM ConnectionInfo");
		log.debug("connectionInfo : {}", connectionInfo);
		
	}
	
	
	@Test
	void mariaDbDataSourceConnectionInfoReaderTest() {
		this.contextRunner.run(context -> {
			var beanName = "mariaDbDataSourceConnectionInfoCollector";
			assertThat(context).hasBean(beanName);
			var connectionConfigProperties = context.getBean(ConnectionConfigProperties.class);
			
			log.debug("connectionConfigProperties : {}", connectionConfigProperties);
			log.debug("connectionConfigProperties username : {}", connectionConfigProperties.getReaders().get("mariadb-datasource").getProperties().get("username"));
			
			var connectionInfoRegistry = context.getBean(beanName, ConnectionInfoRegistry.class);
			log.debug("connectionInfoRegistry : {}", connectionInfoRegistry.getConnectionInfoList());
		});
	}
	
	@Test
	void sqlServerDataSourceConnectionInfoReaderTest() {
		this.contextRunner.run(context -> {
			var beanName = "sqlServerDataSourceConnectionInfoCollector";
			assertThat(context).hasBean(beanName);
			var connectionConfigProperties = context.getBean(ConnectionConfigProperties.class);
			
			log.debug("connectionConfigProperties : {}", connectionConfigProperties);
			log.debug("connectionConfigProperties username : {}", connectionConfigProperties.getReaders().get("sqlserver-datasource").getProperties().get("username"));
			
			var connectionInfoRegistry = context.getBean(beanName, ConnectionInfoRegistry.class);
			log.debug("connectionInfoRegistry : {}", connectionInfoRegistry.getConnectionInfoList());
		});
	}
}
