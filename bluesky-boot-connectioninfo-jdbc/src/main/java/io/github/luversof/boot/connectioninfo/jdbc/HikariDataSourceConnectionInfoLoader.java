package io.github.luversof.boot.connectioninfo.jdbc;

import java.util.List;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.github.luversof.boot.connectioninfo.ConnectionConfigReader;
import io.github.luversof.boot.connectioninfo.ConnectionInfo;
import io.github.luversof.boot.connectioninfo.ConnectionInfoKey;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import io.github.luversof.boot.connectioninfo.DataSourceConnectionConfig;
import io.github.luversof.boot.security.crypto.factory.TextEncryptorFactories;
import lombok.Getter;

public class HikariDataSourceConnectionInfoLoader extends AbstractDataSourceConnectionInfoLoader<HikariDataSource, DataSourceConnectionConfig> {

	@Getter
	protected String loaderKey = "hikaridatasource";

	public HikariDataSourceConnectionInfoLoader(ConnectionInfoProperties connectionInfoProperties, List<ConnectionConfigReader<DataSourceConnectionConfig>> connectionConfigReaderList) {
		super(connectionInfoProperties, connectionConfigReaderList);
	}

	@Override
	protected ConnectionInfo<HikariDataSource> createConnectionInfo(DataSourceConnectionConfig connectionConfig) {
		var config = new HikariConfig();
		var textEncryptor = TextEncryptorFactories.getDelegatingTextEncryptor();
		config.setJdbcUrl(connectionConfig.getUrl());
		config.setUsername(textEncryptor.decrypt(connectionConfig.getUsername()));
		config.setPassword(textEncryptor.decrypt(connectionConfig.getPassword()));
		var hikariDataSource = new HikariDataSource(config);
		
		return new ConnectionInfo<>(new ConnectionInfoKey(getLoaderKey(), connectionConfig.getConnection()), hikariDataSource);
	}
}
