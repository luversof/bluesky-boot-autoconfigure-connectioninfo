package io.github.luversof.boot.connectioninfo;

import java.util.List;

@FunctionalInterface
public interface ConnectionInfoRegistry<T> {
	
	List<ConnectionInfo<T>> getConnectionInfoList();
	
	default void addConnectionInfo(ConnectionInfo<T> connectionInfo) {
		getConnectionInfoList().add(connectionInfo);
	}
	
	default void addConnectionInfoList(List<ConnectionInfo<T>> connectionInfoList) {
		getConnectionInfoList().addAll(connectionInfoList);
	}
	
	default ConnectionInfo<T> getConnectionInfo(ConnectionInfoKey connectionInfoKey) {
		return getConnectionInfoList().stream().filter(connectionInfo -> connectionInfo.getKey().equals(connectionInfoKey)).findAny().orElseThrow(() -> new RuntimeException("NOT_EXIST_CONNECTIONINFO"));
	}  

}
