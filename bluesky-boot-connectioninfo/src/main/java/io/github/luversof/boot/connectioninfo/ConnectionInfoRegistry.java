package io.github.luversof.boot.connectioninfo;

import java.util.List;

public interface ConnectionInfoRegistry {
	
	void addConnectionInfo(ConnectionInfo<?> connectionInfo);
	
	void addConnectionInfoList(List<ConnectionInfo<?>> connectionInfoList);
	
	ConnectionInfo<?> getConnectionInfo(ConnectionInfoKey connectionInfoKey);  

}
