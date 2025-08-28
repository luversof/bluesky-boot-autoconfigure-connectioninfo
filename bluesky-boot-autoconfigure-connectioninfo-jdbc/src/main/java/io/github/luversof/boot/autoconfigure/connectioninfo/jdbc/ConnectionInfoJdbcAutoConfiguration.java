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

import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import com.zaxxer.hikari.HikariDataSource;

import io.github.luversof.boot.autoconfigure.connectioninfo.ConnectionInfoAutoConfiguration;
import io.github.luversof.boot.connectioninfo.ConnectionConfigProperties;
import io.github.luversof.boot.connectioninfo.ConnectionConfigReader;
import io.github.luversof.boot.connectioninfo.ConnectionInfo;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoader;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import io.github.luversof.boot.connectioninfo.ConnectionInfoRegistry;
import io.github.luversof.boot.connectioninfo.jdbc.DataSourceConnectionConfig;
import io.github.luversof.boot.connectioninfo.jdbc.HikariDataSourceConnectionInfoLoader;
import io.github.luversof.boot.connectioninfo.jdbc.MariaDbDataSourceConnectionConfigReader;
import io.github.luversof.boot.connectioninfo.jdbc.PostgreSQLDataSourceConnectionConfigReader;
import io.github.luversof.boot.connectioninfo.jdbc.SQLServerDataSourceConnectionConfigReader;

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
	static class MariaDbDataSourceConnectionInfoConfiguration {
		
		@Bean
		@ConditionalOnProperty(prefix = "bluesky-boot.connection-config.readers", name = "mariadb-datasource.enabled", havingValue = "true")
		MariaDbDataSourceConnectionConfigReader mariaDbDataSourceConnectionConfigReader(ConnectionConfigProperties connectionConfigProperties) {
			return new MariaDbDataSourceConnectionConfigReader(connectionConfigProperties);
		}
		
	}
	
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass({ DataSource.class, JdbcTemplate.class, HikariDataSource.class, SQLServerDriver.class })
	static class SQLServerDataSourceConnectionInfoConfiguration {
		
		@Bean
		@ConditionalOnProperty(prefix = "bluesky-boot.connection-config.readers", name = "sqlserver-datasource.enabled", havingValue = "true")
		SQLServerDataSourceConnectionConfigReader sqlServerDataSourceConnectionConfigReader(ConnectionConfigProperties connectionConfigProperties) {
			return new SQLServerDataSourceConnectionConfigReader(connectionConfigProperties);
		}
	}
	
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass({ DataSource.class, JdbcTemplate.class, HikariDataSource.class, org.postgresql.Driver.class })
	static class PostgreSQLDataSourceConnectionInfoConfiguration {
		
		@Bean
		@ConditionalOnProperty(prefix = "bluesky-boot.connection-config.readers", name = "postgresql-datasource.enabled", havingValue = "true")
		PostgreSQLDataSourceConnectionConfigReader postgreSQLDataSourceConnectionConfigReader(ConnectionConfigProperties connectionConfigProperties) {
			return new PostgreSQLDataSourceConnectionConfigReader(connectionConfigProperties);
		}
		
	}
	
	@Bean
	@ConditionalOnProperty(prefix = "bluesky-boot.connection-info.loaders", name = "hikaridatasource.enabled", havingValue = "true")
	HikariDataSourceConnectionInfoLoader hikariDataSourceConnectionInfoLoader(ConnectionInfoProperties connectionInfoProperties, List<ConnectionConfigReader<DataSourceConnectionConfig>> connectionConfigReaderList) {
		return new HikariDataSourceConnectionInfoLoader(connectionInfoProperties, connectionConfigReaderList);
	}
	
	@Bean
	<T extends HikariDataSource, C extends DataSourceConnectionConfig> ConnectionInfoRegistry<T> dataSourceConnectionInfoRegistry(List<ConnectionInfoLoader<T, C>> connectionInfoLoaderList) {
		var connectionInfoList = new ArrayList<ConnectionInfo<T>>();
		connectionInfoLoaderList.forEach(connectionInfoLoader -> connectionInfoList.addAll(connectionInfoLoader.load()));
		return () -> connectionInfoList;
	}

}
