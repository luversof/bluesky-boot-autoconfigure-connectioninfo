package io.github.luversof.boot.connectioninfo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * connection info가 최종적으로 생성하려고 하는 대상 객체
 * reader를 통해 읽어들인 대상 connection 정보를 담고 있으며 List<ConnectionInfo> 를 최종적으로 구해서 ConnectionInfoRegistry에 저장함 
 * @param <T> connection 객체 타입
 */
@Data
@AllArgsConstructor
public class ConnectionInfo<T> {
	
	private final ConnectionInfoKey key;
	
	private final T connection;
	
}
