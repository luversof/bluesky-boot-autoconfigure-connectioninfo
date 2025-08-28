package io.github.luversof.boot.connectioninfo.jdbc;

import java.sql.Driver;

import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;

import io.github.luversof.boot.connectioninfo.ConnectionConfigProperties;
import lombok.Getter;

public class PostgreSQLDataSourceConnectionConfigReader extends AbstractDataSourceConnectionConfigReader<DataSourceConnectionConfig> {
	
	@Getter
	protected String readerKey = "postgresql-datasource";
	
	@Getter
	protected Driver readerDriver = new org.postgresql.Driver();
	
	public PostgreSQLDataSourceConnectionConfigReader(ConnectionConfigProperties connectionConfigProperties) {
		super(connectionConfigProperties);
	}

	@Override
	protected RowMapper<DataSourceConnectionConfig> getConnectionConfigRowMapper() {
		return new DataClassRowMapper<>(DataSourceConnectionConfig.class);
	}

}
