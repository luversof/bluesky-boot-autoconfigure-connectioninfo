package io.github.luversof.boot.connectioninfo.jdbc;

import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import lombok.Getter;

public class MariaDbHikariDataSourceConnectionInfoLoader extends AbstractHikariDataSourceConnectionInfoLoader<MariaDbDataSourceConnectionConfig, MariaDbDataSourceConnectionConfigReader> {

	@Getter
	protected String loaderKey = "mariadb-datasource";
	
	public MariaDbHikariDataSourceConnectionInfoLoader(ConnectionInfoProperties connectionInfoProperties, MariaDbDataSourceConnectionConfigReader connectionConfigReader) {
		super(connectionInfoProperties, connectionConfigReader);
	}

}
