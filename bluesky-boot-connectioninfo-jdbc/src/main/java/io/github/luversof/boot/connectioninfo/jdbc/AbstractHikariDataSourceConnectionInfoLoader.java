package io.github.luversof.boot.connectioninfo.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.github.luversof.boot.connectioninfo.ConnectionInfo;
import io.github.luversof.boot.connectioninfo.ConnectionInfoKey;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import io.github.luversof.boot.security.crypto.factory.TextEncryptorFactories;

public abstract class AbstractHikariDataSourceConnectionInfoLoader extends AbstractDataSourceConnectionInfoLoader<HikariDataSource>{

	protected AbstractHikariDataSourceConnectionInfoLoader(ConnectionInfoProperties connectionInfoProperties) {
		super(connectionInfoProperties);
	}

	@Override
	protected ConnectionInfo<HikariDataSource> createConnectionInfo(ConnectionInfoRowMapper connectionInfoRowMapper) {
		var config = new HikariConfig();
		var textEncryptor = TextEncryptorFactories.getDelegatingTextEncryptor();
		config.setJdbcUrl(connectionInfoRowMapper.url());
		config.setUsername(textEncryptor.decrypt(connectionInfoRowMapper.username()));
		config.setPassword(textEncryptor.decrypt(connectionInfoRowMapper.password()));
		var hikariDataSource = new HikariDataSource(config);
		
		return new ConnectionInfo<>(new ConnectionInfoKey(getLoaderKey(), connectionInfoRowMapper.connection()), hikariDataSource);
	}
}
