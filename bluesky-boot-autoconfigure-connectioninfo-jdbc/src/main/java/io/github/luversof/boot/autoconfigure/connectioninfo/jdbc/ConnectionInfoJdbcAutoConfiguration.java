package io.github.luversof.boot.autoconfigure.connectioninfo.jdbc;

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
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import io.github.luversof.boot.connectioninfo.jdbc.MariaDbHikariDataSourceConnectionInfoLoader;
import io.github.luversof.boot.connectioninfo.jdbc.SQLServerHikariDataSourceConnectionInfoLoader;

@AutoConfiguration(
	value = "blueskyBootConnectionInfoJdbcAutoConfiguration", 
	before = {
		DataSourceAutoConfiguration.class,
		ConnectionInfoAutoConfiguration.class
	}
)
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })
@ConditionalOnProperty(prefix = "bluesky-boot.connection-info", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ConnectionInfoJdbcAutoConfiguration {

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass({ DataSource.class, JdbcTemplate.class, HikariDataSource.class, org.mariadb.jdbc.Driver.class })
	@ConditionalOnProperty(prefix = "bluesky-boot.connection-info.loaders", name = "mariadb-datasource.enabled", havingValue = "true")
	static class MariaDbDataSourceConnectionInfoConfiguration {
		
		@Bean
		MariaDbHikariDataSourceConnectionInfoLoader mariaDbHikariDataSourceConnectionInfoLoader(ConnectionInfoProperties connectionInfoProperties) {
			return new MariaDbHikariDataSourceConnectionInfoLoader(connectionInfoProperties);
		}
		
	}
	
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass({ DataSource.class, JdbcTemplate.class, HikariDataSource.class, org.mariadb.jdbc.Driver.class })
	@ConditionalOnProperty(prefix = "bluesky-boot.connection-info.loaders", name = "sqlserver-datasource.enabled", havingValue = "true")
	static class SQLServerDataSourceConnectionInfoConfiguration {
		
		@Bean
		SQLServerHikariDataSourceConnectionInfoLoader sqlServerHikariDataSourceConnectionInfoLoader(ConnectionInfoProperties connectionInfoProperties) {
			return new SQLServerHikariDataSourceConnectionInfoLoader(connectionInfoProperties);
		}
		
	}

}
