package io.github.luversof.boot.connectioninfo.jdbc;

import java.sql.Driver;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import io.github.luversof.boot.connectioninfo.ConnectionConfigProperties;
import io.github.luversof.boot.security.crypto.factory.TextEncryptorFactories;
import lombok.Getter;

public abstract class AbstractDataSourceConnectionConfigReader<C extends DataSourceConnectionConfig> implements DataSourceConnectionConfigReader<C> {
	
	protected final ConnectionConfigProperties connectionConfigProperties;
	
	@Getter
	protected String loaderQuery = """
		SELECT connection, url, username, password, extradata 
		FROM DataSourceConfig
		WHERE ConnectionConfig IN ({0})
		""";
	
	protected AbstractDataSourceConnectionConfigReader(ConnectionConfigProperties connectionConfigProperties) {
		this.connectionConfigProperties = connectionConfigProperties;
	}
	
	/**
	 * Call the target Driver object to be used by the loader
	 * 
	 * @return Target Driver object
	 */
	protected abstract Driver getReaderDriver();

	@Override
	public List<C> readConnectionConfigList(List<String> connectionList) {
		String sql = MessageFormat.format(getLoaderQuery(), String.join(",", Collections.nCopies(connectionList.size(), "?")));
		return getJdbcTemplate().query(sql, new ArgumentPreparedStatementSetter(connectionList.toArray()), getConnectionConfigRowMapper());
	}
	
	protected abstract RowMapper<C> getConnectionConfigRowMapper();

	private JdbcTemplate getJdbcTemplate() {
		var encryptor = TextEncryptorFactories.getDelegatingTextEncryptor();
		
		var readerProperties = connectionConfigProperties.getReaders().get(getReaderKey()).getProperties();
		String url = readerProperties.get("url");
		String username = encryptor.decrypt(readerProperties.get("username"));
		String password = encryptor.decrypt(readerProperties.get("password"));
		
		return new JdbcTemplate(new SimpleDriverDataSource(getReaderDriver(), url, username, password));
	}
}
