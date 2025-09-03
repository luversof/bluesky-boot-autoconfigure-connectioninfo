package io.github.luversof.boot.connectioninfo.jdbc;

import java.sql.Driver;

import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;

import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import io.github.luversof.boot.connectioninfo.DataSourceConnectionConfig;
import lombok.Getter;

public class MariaDbDataSourceConnectionInfoReader extends AbstractDataSourceConnectionInfoReader<DataSourceConnectionConfig> {
	
	@Getter
	protected String readerKey = "mariadb-datasource";
	
	@Getter
	protected Driver readerDriver = new org.mariadb.jdbc.Driver();
	
	public MariaDbDataSourceConnectionInfoReader(ConnectionInfoProperties connectionInfoProperties) {
		super(connectionInfoProperties);
	}

	@Override
	protected RowMapper<DataSourceConnectionConfig> getConnectionConfigRowMapper() {
		return new DataClassRowMapper<>(DataSourceConnectionConfig.class);
	}

}
