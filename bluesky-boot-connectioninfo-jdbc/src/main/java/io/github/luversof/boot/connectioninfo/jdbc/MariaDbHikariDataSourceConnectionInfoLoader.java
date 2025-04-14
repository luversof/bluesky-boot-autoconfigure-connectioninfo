package io.github.luversof.boot.connectioninfo.jdbc;

import java.sql.Driver;

import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import lombok.Getter;

public class MariaDbHikariDataSourceConnectionInfoLoader extends AbstractHikariDataSourceConnectionInfoLoader {

	@Getter
	protected String loaderKey = "mariadb-datasource";
	
	public MariaDbHikariDataSourceConnectionInfoLoader(ConnectionInfoProperties connectionInfoProperties) {
		super(connectionInfoProperties);
	}

	@Override
	protected Driver getLoaderDriver() {
		return new org.mariadb.jdbc.Driver();
	}
	
}
