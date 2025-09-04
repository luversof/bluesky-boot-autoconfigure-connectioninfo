package io.github.luversof.boot.connectioninfo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * connection info가 최종적으로 생성하려고 하는 대상 객체
 * @param <T>
 */
@Data
@AllArgsConstructor
public class ConnectionInfo<T> {
	
	/**
	 * connection info key
	 */
	private final ConnectionInfoKey key;
	
	/**
	 * connection 객체
	 */
	private final T connection;
	
}
