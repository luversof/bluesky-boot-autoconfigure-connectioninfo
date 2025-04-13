package io.github.luversof.boot.connectioninfo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConnectionInfo<T> {
	
	private final ConnectionInfoKey key;
	
	private final T connection;
	
}
