package io.github.luversof.boot.connectioninfo;

import java.util.List;

public interface ConnectionInfoLoader<T> {
	
	ConnectionInfo<T> load(ConnectionInfoKey connectionInfoKey);

	List<ConnectionInfo<T>> load();
	
}
