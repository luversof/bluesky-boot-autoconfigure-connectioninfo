package io.github.luversof.boot.connectioninfo.jdbc;

import java.sql.Driver;

import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;

import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import io.github.luversof.boot.connectioninfo.DataSourceConnectionConfig;
import lombok.Getter;
import lombok.SneakyThrows;

public class MysqlDataSourceConnectionInfoReader extends AbstractDataSourceConnectionInfoReader<DataSourceConnectionConfig> {

	@Getter
	protected String readerKey = "mysql-datasource";
	
	@Override
	@SneakyThrows
	protected Driver getReaderDriver() {
		return new com.mysql.jdbc.Driver();
	}
	
	public MysqlDataSourceConnectionInfoReader(ConnectionInfoProperties connectionInfoProperties) {
		super(connectionInfoProperties);
	}

	@Override
	protected RowMapper<DataSourceConnectionConfig> getConnectionConfigRowMapper() {
		return new DataClassRowMapper<>(DataSourceConnectionConfig.class);
	}
}
