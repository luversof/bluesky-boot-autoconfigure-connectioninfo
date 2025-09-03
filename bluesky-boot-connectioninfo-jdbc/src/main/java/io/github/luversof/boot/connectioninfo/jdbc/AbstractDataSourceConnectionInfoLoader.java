package io.github.luversof.boot.connectioninfo.jdbc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.util.CollectionUtils;

import io.github.luversof.boot.connectioninfo.ConnectionInfoReader;
import io.github.luversof.boot.connectioninfo.ConnectionInfo;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoader;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import io.github.luversof.boot.connectioninfo.DataSourceConnectionConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Used when obtaining DataSource by loading connectionInfo from DB
 * @author bluesky
 * 
 * @param <T> The type of DataSource to be loaded via Loader
 */
@Slf4j
public abstract class AbstractDataSourceConnectionInfoLoader<T extends DataSource, C extends DataSourceConnectionConfig> implements ConnectionInfoLoader<T, C> {
	
	protected final ConnectionInfoProperties connectionInfoProperties;
	
	@Getter
	protected final List<ConnectionInfoReader<C>> connectionInfoReaderList;
	
	protected AbstractDataSourceConnectionInfoLoader(ConnectionInfoProperties connectionInfoProperties, List<ConnectionInfoReader<C>> connectionInfoReaderList) {
		this.connectionInfoProperties= connectionInfoProperties;
		this.connectionInfoReaderList = connectionInfoReaderList;
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
		
		log.debug("connectionInfoReaderKeyList : {}", getConnectionInfoReaderList().stream().map(reader -> reader.getReaderKey()).toList());
		
		var connectionConfigList = new ArrayList<C>();
		getConnectionInfoReaderList().forEach(connectionInfoReader -> {
			var readConnectionConfigList = connectionInfoReader.readConnectionConfigList(connectionList);
			if (!CollectionUtils.isEmpty(readConnectionConfigList)) {
				connectionConfigList.addAll(readConnectionConfigList);
			}
		});
		
		connectionList.forEach(connection -> {
			if (connectionConfigList.stream().anyMatch(connetionInfoResult -> connetionInfoResult.getConnection().equalsIgnoreCase(connection))) {
				log.debug("find database connection ({})", connection);
			} else {
				log.debug("cannot find database connection ({})", connection);
			}
		});
		
		if (CollectionUtils.isEmpty(connectionConfigList)) {
			return Collections.emptyList();
		}

		var connectionInfoList = new ArrayList<ConnectionInfo<T>>();
		for (var connectionConfig : connectionConfigList) {
			connectionInfoList.add(createConnectionInfo(connectionConfig));
		}
		
		return connectionInfoList;
	}
	
	protected abstract ConnectionInfo<T> createConnectionInfo(DataSourceConnectionConfig connectionConfig);
	
}
