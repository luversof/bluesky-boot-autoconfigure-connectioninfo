package io.github.luversof.boot.connectioninfo;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;

public final class ConnectionInfoUtil {

	private static ApplicationContext applicationContext;

	private ConnectionInfoUtil() {
	}

	public static void setApplicationContext(ApplicationContext applicationContext) {
		ConnectionInfoUtil.applicationContext = applicationContext;
	}

	public static <T> T getConnection(String connectionKey) {
		// ConnectionInfoRegistry에서 기본 ConnectionInfo를 가져오고
		
		ObjectProvider<ConnectionInfoRegistry<T>> connectionInfoRegistryProvider = applicationContext.getBeanProvider(ResolvableType.forType(new ParameterizedTypeReference<ConnectionInfoRegistry<T>>() {}));
		// connectionKey에 해당하는 ConnectionInfo가 있으면 반환
		ConnectionInfoRegistry<T> targetRegistry = null;
		for (var registry : connectionInfoRegistryProvider) {
			targetRegistry = registry;
			var connectionInfoList = registry.getConnectionInfo(connectionKey);
			if (connectionInfoList != null && !connectionInfoList.isEmpty()) {
				return connectionInfoList.getFirst().getConnection();
			}
		}
		
		// Registry가 없으면 null 반환
		if (targetRegistry == null) {
			return null;
		}
		
		// 없으면 Loader를 통해 LazyLoad를 시도
		ObjectProvider<ConnectionInfoLoader<T, ?>> connectionInfoLoaderProvider = applicationContext.getBeanProvider(ResolvableType.forType(new ParameterizedTypeReference<ConnectionInfoLoader<T, ?>>() {}));
		for (var loader : connectionInfoLoaderProvider) {
			var connectionInfoList = loader.load(List.of(connectionKey));
			if (connectionInfoList != null && !connectionInfoList.isEmpty()) {
				targetRegistry.addConnectionInfoList(connectionInfoList);
				return connectionInfoList.getFirst().getConnection();
			}
		}
		
		// 그래도 없으면 null 반환
		return null;
	}
	
}
