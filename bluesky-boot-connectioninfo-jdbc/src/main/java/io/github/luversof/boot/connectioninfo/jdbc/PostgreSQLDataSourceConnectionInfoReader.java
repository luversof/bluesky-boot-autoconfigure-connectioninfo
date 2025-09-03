package io.github.luversof.boot.connectioninfo.jdbc;

import java.sql.Driver;

import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;

import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import io.github.luversof.boot.connectioninfo.DataSourceConnectionConfig;
import lombok.Getter;

public class PostgreSQLDataSourceConnectionInfoReader extends AbstractDataSourceConnectionInfoReader<DataSourceConnectionConfig> {
	
	@Getter
	protected String readerKey = "postgresql-datasource";
	
	@Getter
	protected Driver readerDriver = new org.postgresql.Driver();
	
	public PostgreSQLDataSourceConnectionInfoReader(ConnectionInfoProperties connectionInfoProperties) {
		super(connectionInfoProperties);
	}

	@Override
	protected RowMapper<DataSourceConnectionConfig> getConnectionConfigRowMapper() {
		return new DataClassRowMapper<>(DataSourceConnectionConfig.class);
	}

}
