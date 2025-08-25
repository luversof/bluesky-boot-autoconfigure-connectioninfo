package io.github.luversof.boot.connectioninfo.jdbc;

import java.util.List;

import io.github.luversof.boot.connectioninfo.ConnectionConfigReader;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import lombok.Getter;

public class MariaDbHikariDataSourceConnectionInfoLoader<C extends DataSourceConnectionConfig> extends AbstractHikariDataSourceConnectionInfoLoader<C> {

	@Getter
	protected String loaderKey = "mariadb-datasource";
	
	public MariaDbHikariDataSourceConnectionInfoLoader(ConnectionInfoProperties connectionInfoProperties, List<ConnectionConfigReader<C>> connectionConfigReaderList) {
		super(connectionInfoProperties, connectionConfigReaderList);
	}

}
