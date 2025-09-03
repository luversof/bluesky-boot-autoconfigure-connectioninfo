package io.github.luversof.boot.connectioninfo.jdbc;

import java.sql.Driver;

import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;

import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import io.github.luversof.boot.connectioninfo.DataSourceConnectionConfig;
import lombok.Getter;

public class SQLServerDataSourceConnectionConfigReader extends AbstractDataSourceConnectionConfigReader<DataSourceConnectionConfig> {

	@Getter
	protected String readerKey = "sqlserver-datasource";
	
	@Getter
	protected Driver readerDriver = new com.microsoft.sqlserver.jdbc.SQLServerDriver();
	
	public SQLServerDataSourceConnectionConfigReader(ConnectionInfoProperties connectionInfoProperties) {
		super(connectionInfoProperties);
	}
	
	@Override
	protected RowMapper<DataSourceConnectionConfig> getConnectionConfigRowMapper() {
		return new DataClassRowMapper<>(DataSourceConnectionConfig.class);
	}

}
