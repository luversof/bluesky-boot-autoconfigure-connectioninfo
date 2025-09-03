package io.github.luversof.boot.connectioninfo;

import java.util.List;

public interface ConnectionInfoReader<C extends ConnectionConfig> {
	
	String getReaderKey();

	List<C> readConnectionConfigList(List<String> connectionList);

}
