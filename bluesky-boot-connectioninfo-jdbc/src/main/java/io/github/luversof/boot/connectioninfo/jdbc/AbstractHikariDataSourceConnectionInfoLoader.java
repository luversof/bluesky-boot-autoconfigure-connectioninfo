package io.github.luversof.boot.connectioninfo.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.github.luversof.boot.connectioninfo.ConnectionInfo;
import io.github.luversof.boot.connectioninfo.ConnectionInfoKey;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import io.github.luversof.boot.security.crypto.factory.TextEncryptorFactories;

public abstract class AbstractHikariDataSourceConnectionInfoLoader<C extends DataSourceConnectionConfig, R extends DataSourceConnectionConfigReader<C>> extends AbstractDataSourceConnectionInfoLoader<HikariDataSource, C, R>{

	protected AbstractHikariDataSourceConnectionInfoLoader(ConnectionInfoProperties connectionInfoProperties, R connectionConfigReader) {
		super(connectionInfoProperties, connectionConfigReader);
	}

	@Override
	protected ConnectionInfo<HikariDataSource> createConnectionInfo(DataSourceConnectionConfig connectionConfig) {
		var config = new HikariConfig();
		var textEncryptor = TextEncryptorFactories.getDelegatingTextEncryptor();
		config.setJdbcUrl(connectionConfig.url());
		config.setUsername(textEncryptor.decrypt(connectionConfig.username()));
		config.setPassword(textEncryptor.decrypt(connectionConfig.password()));
		var hikariDataSource = new HikariDataSource(config);
		
		return new ConnectionInfo<>(new ConnectionInfoKey(getLoaderKey(), connectionConfig.connection()), hikariDataSource);
	}
}
