package io.github.luversof.boot.connectioninfo.jdbc;

import java.sql.Driver;

import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import lombok.Getter;

public class SQLServerHikariDataSourceConnectionInfoLoader extends AbstractHikariDataSourceConnectionInfoLoader {
	
	@Getter
	protected String loaderKey = "sqlserver-datasource";
	
	public SQLServerHikariDataSourceConnectionInfoLoader(ConnectionInfoProperties connectionInfoProperties) {
		super(connectionInfoProperties);
	}

	@Override
	protected Driver getLoaderDriver() {
		return new com.microsoft.sqlserver.jdbc.SQLServerDriver();
	}

}
