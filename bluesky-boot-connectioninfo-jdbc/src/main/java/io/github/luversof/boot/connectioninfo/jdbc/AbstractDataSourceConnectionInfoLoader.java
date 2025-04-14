package io.github.luversof.boot.connectioninfo.jdbc;

import java.sql.Driver;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.util.CollectionUtils;

import io.github.luversof.boot.connectioninfo.ConnectionInfo;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoader;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import io.github.luversof.boot.security.crypto.factory.TextEncryptorFactories;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Used when obtaining DataSource by loading connectionInfo from DB
 * @author bluesky
 * 
 * @param <T> The type of DataSource to be loaded via Loader
 */
@Slf4j
public abstract class AbstractDataSourceConnectionInfoLoader<T extends DataSource> implements ConnectionInfoLoader<T> {
	
	protected final ConnectionInfoProperties connectionInfoProperties;
	
	@Getter
	protected String loaderQuery = """
		SELECT connection, url, username, password, extradata 
		FROM ConnectionInfo
		WHERE connection IN ({0})
		""";
	
	protected AbstractDataSourceConnectionInfoLoader(ConnectionInfoProperties connectionInfoProperties) {
		this.connectionInfoProperties= connectionInfoProperties;
	}

	@Override
	public List<ConnectionInfo<T>> load() {
		if (connectionInfoProperties == null 
				|| connectionInfoProperties.getLoaders() == null 
				|| !connectionInfoProperties.getLoaders().containsKey(getLoaderKey())
				|| CollectionUtils.isEmpty(connectionInfoProperties.getLoaders().get(getLoaderKey()).getConnections())) {
			return Collections.emptyList();
		}
		
		List<String> connectionList = connectionInfoProperties.getLoaders().get(getLoaderKey()).getConnections().values().stream().flatMap(List::stream).distinct().toList();
		
		return load(connectionList);
	}

	@Override
	public List<ConnectionInfo<T>> load(List<String> connectionList) {
		var loaderJdbcTemplate = getJdbcTemplate();
		
		String sql = MessageFormat.format(getLoaderQuery(), String.join(",", Collections.nCopies(connectionList.size(), "?")));
		
		List<ConnectionInfoRowMapper> connectionInfoRowMapperList = loaderJdbcTemplate.query(sql, new ArgumentPreparedStatementSetter(connectionList.toArray()), new DataClassRowMapper<ConnectionInfoRowMapper>(ConnectionInfoRowMapper.class));
		
		connectionList.forEach(connection -> {
			if (connectionInfoRowMapperList.stream().anyMatch(connetionInfoResult -> connetionInfoResult.connection().equalsIgnoreCase(connection))) {
				log.debug("find database connection ({})", connection);
			} else {
				log.debug("cannot find database connection ({})", connection);
			}
		});
		
		if (CollectionUtils.isEmpty(connectionInfoRowMapperList)) {
			return Collections.emptyList();
		}

		var connectionInfoList = new ArrayList<ConnectionInfo<T>>();
		for (var connectionInfoRowMapper : connectionInfoRowMapperList) {
			connectionInfoList.add(createConnectionInfo(connectionInfoRowMapper));
		}
		
		return connectionInfoList;
	}
	
	/**
	 * Call the target Driver object to be used by the loader
	 * 
	 * @return Target Driver object
	 */
	protected abstract Driver getLoaderDriver();
	
	protected abstract ConnectionInfo<T> createConnectionInfo(ConnectionInfoRowMapper connectionInfoRowMapper);
	
	private JdbcTemplate getJdbcTemplate() {
		var encryptor = TextEncryptorFactories.getDelegatingTextEncryptor();
		
		var loaderProperties = connectionInfoProperties.getLoaders().get(getLoaderKey()).getProperties();
		String url = loaderProperties.get("url");
		String username = encryptor.decrypt(loaderProperties.get("username"));
		String password = encryptor.decrypt(loaderProperties.get("password"));
		
		return new JdbcTemplate(new SimpleDriverDataSource(getLoaderDriver(), url, username, password));
	}
	
	/**
	 * ConnectionInfoResult information obtained through the loader
	 * 
	 * @param connection connection
	 * @param url url
	 * @param username username
	 * @param password password
	 * @param extradata extradata
	 */
	public static record ConnectionInfoRowMapper(String connection, String url, String username, String password, String extradata) {
	}

}
