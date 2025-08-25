package io.github.luversof.boot.autoconfigure.connectioninfo.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.zaxxer.hikari.HikariDataSource;

import io.github.luversof.boot.autoconfigure.connectioninfo.ConnectionInfoAutoConfiguration;
import io.github.luversof.boot.connectioninfo.ConnectionConfigProperties;
import io.github.luversof.boot.connectioninfo.ConnectionConfigReader;
import io.github.luversof.boot.connectioninfo.ConnectionInfo;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoader;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import io.github.luversof.boot.connectioninfo.ConnectionInfoRegistry;
import io.github.luversof.boot.connectioninfo.jdbc.DataSourceConnectionConfig;
import io.github.luversof.boot.connectioninfo.jdbc.MariaDbDataSourceConnectionConfigReader;
import io.github.luversof.boot.connectioninfo.jdbc.MariaDbHikariDataSourceConnectionInfoLoader;
import io.github.luversof.boot.connectioninfo.jdbc.SQLServerDataSourceConnectionConfigReader;
import io.github.luversof.boot.connectioninfo.jdbc.SQLServerHikariDataSourceConnectionInfoLoader;

@AutoConfiguration(
	value = "blueskyBootConnectionInfoJdbcAutoConfiguration", 
	before = {
		DataSourceAutoConfiguration.class,
		ConnectionInfoAutoConfiguration.class
	}
)
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class, HikariDataSource.class })
public class ConnectionInfoJdbcAutoConfiguration {

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass({ DataSource.class, JdbcTemplate.class, HikariDataSource.class, org.mariadb.jdbc.Driver.class })
	@ConditionalOnProperty(prefix = "bluesky-boot.connection-config.readers", name = "mariadb-datasource.enabled", havingValue = "true")
	@ConditionalOnProperty(prefix = "bluesky-boot.connection-info.loaders", name = "mariadb-datasource.enabled", havingValue = "true")
	static class MariaDbDataSourceConnectionInfoConfiguration {
		
		@Bean
		MariaDbDataSourceConnectionConfigReader mariaDbDataSourceConnectionConfigReader(ConnectionConfigProperties connectionConfigProperties) {
			return new MariaDbDataSourceConnectionConfigReader(connectionConfigProperties);
		}
		
		@Bean
		<C extends DataSourceConnectionConfig> MariaDbHikariDataSourceConnectionInfoLoader<C> mariaDbHikariDataSourceConnectionInfoLoader(ConnectionInfoProperties connectionInfoProperties, List<ConnectionConfigReader<C>> connectionConfigReaderList) {
			return new MariaDbHikariDataSourceConnectionInfoLoader<>(connectionInfoProperties, connectionConfigReaderList);
		}
		
	}
	
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass({ DataSource.class, JdbcTemplate.class, HikariDataSource.class, org.mariadb.jdbc.Driver.class })
	@ConditionalOnProperty(prefix = "bluesky-boot.connection-config.readers", name = "sqlserver-datasource.enabled", havingValue = "true")
	@ConditionalOnProperty(prefix = "bluesky-boot.connection-info.loaders", name = "sqlserver-datasource.enabled", havingValue = "true")
	static class SQLServerDataSourceConnectionInfoConfiguration {
		
		@Bean
		SQLServerDataSourceConnectionConfigReader sqlServerDataSourceConnectionConfigReader(ConnectionConfigProperties connectionConfigProperties) {
			return new SQLServerDataSourceConnectionConfigReader(connectionConfigProperties);
		}
		
		
		@Bean
		<C extends DataSourceConnectionConfig> SQLServerHikariDataSourceConnectionInfoLoader<C> sqlServerHikariDataSourceConnectionInfoLoader(ConnectionInfoProperties connectionInfoProperties, List<ConnectionConfigReader<C>> connectionConfigReaderList) {
			return new SQLServerHikariDataSourceConnectionInfoLoader<>(connectionInfoProperties, connectionConfigReaderList);
		}
		
	}
	
	@Bean
	<T extends HikariDataSource, C extends DataSourceConnectionConfig> ConnectionInfoRegistry<T> dataSourceConnectionInfoRegistry(List<ConnectionInfoLoader<T, C>> connectionInfoLoaderList) {
		var connectionInfoList = new ArrayList<ConnectionInfo<T>>();
		connectionInfoLoaderList.forEach(connectionInfoLoader -> connectionInfoList.addAll(connectionInfoLoader.load()));
		return () -> connectionInfoList;
	}

}
