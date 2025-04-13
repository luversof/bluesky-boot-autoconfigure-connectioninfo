package io.github.luversof.boot.connectioninfo;

import java.util.List;

public class DefaultConnectionInfoRegistry implements ConnectionInfoRegistry {
	
	private List<ConnectionInfo<?>> connectionInfoList;

	@Override
	public void addConnectionInfo(ConnectionInfo<?> connectionInfo) {
		this.connectionInfoList.add(connectionInfo);
	}
	
	@Override
	public void addConnectionInfoList(List<ConnectionInfo<?>> connectionInfoList) {
		this.connectionInfoList.addAll(connectionInfoList);
	}

	@Override
	public ConnectionInfo<?> getConnectionInfo(ConnectionInfoKey connectionInfoKey) {
		return this.connectionInfoList.stream().filter(connectionInfo -> connectionInfo.getKey().equals(connectionInfoKey)).findAny().orElseThrow(() -> new RuntimeException("NOT_EXIST_CONNECTIONINFO"));
	}

}
