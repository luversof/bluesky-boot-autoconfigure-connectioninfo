package io.github.luversof.boot.connectioninfo;

import java.util.List;

public interface ConnectionConfigReader<C extends ConnectionConfig> {
	
	String getReaderKey();

	List<C> readConnectionConfigList(List<String> connectionList);

}
