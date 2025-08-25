package io.github.luversof.boot.connectioninfo.jdbc;

import java.sql.Driver;

import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;

import io.github.luversof.boot.connectioninfo.ConnectionConfigProperties;
import lombok.Getter;

public class MariaDbDataSourceConnectionConfigReader extends AbstractDataSourceConnectionConfigReader<MariaDbDataSourceConnectionConfig> {
	
	@Getter
	protected String readerKey = "mariadb-datasource";
	
	@Getter
	protected Driver readerDriver = new org.mariadb.jdbc.Driver();
	
	public MariaDbDataSourceConnectionConfigReader(ConnectionConfigProperties connectionConfigProperties) {
		super(connectionConfigProperties);
	}

	@Override
	protected RowMapper<MariaDbDataSourceConnectionConfig> getConnectionConfigRowMapper() {
		return new DataClassRowMapper<>(MariaDbDataSourceConnectionConfig.class);
	}

}
