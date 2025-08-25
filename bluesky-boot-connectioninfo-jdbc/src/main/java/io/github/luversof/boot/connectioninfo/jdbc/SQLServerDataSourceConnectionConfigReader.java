package io.github.luversof.boot.connectioninfo.jdbc;

import java.sql.Driver;

import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;

import io.github.luversof.boot.connectioninfo.ConnectionConfigProperties;
import lombok.Getter;

public class SQLServerDataSourceConnectionConfigReader extends AbstractDataSourceConnectionConfigReader<SQLServerDataSourceConnectionConfig> {

	@Getter
	protected String readerKey = "sqlserver-datasource";
	
	@Getter
	protected Driver readerDriver = new com.microsoft.sqlserver.jdbc.SQLServerDriver();
	
	public SQLServerDataSourceConnectionConfigReader(ConnectionConfigProperties connectionConfigProperties) {
		super(connectionConfigProperties);
	}
	
	@Override
	protected RowMapper<SQLServerDataSourceConnectionConfig> getConnectionConfigRowMapper() {
		return new DataClassRowMapper<>(SQLServerDataSourceConnectionConfig.class);
	}

}
