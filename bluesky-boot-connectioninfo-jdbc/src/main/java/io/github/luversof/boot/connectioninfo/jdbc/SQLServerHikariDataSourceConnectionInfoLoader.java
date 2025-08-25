package io.github.luversof.boot.connectioninfo.jdbc;

import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import lombok.Getter;

public class SQLServerHikariDataSourceConnectionInfoLoader extends AbstractHikariDataSourceConnectionInfoLoader<SQLServerDataSourceConnectionConfig, SQLServerDataSourceConnectionConfigReader> {
	
	@Getter
	protected String loaderKey = "sqlserver-datasource";
	
	public SQLServerHikariDataSourceConnectionInfoLoader(ConnectionInfoProperties connectionInfoProperties, SQLServerDataSourceConnectionConfigReader connectionConfigReader) {
		super(connectionInfoProperties, connectionConfigReader);
	}

}
