package io.github.luversof.boot.autoconfigure.connectioninfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import io.github.luversof.boot.connectioninfo.ConnectionInfoDevCheckController;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoader;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import io.github.luversof.boot.connectioninfo.ConnectionInfoRegistry;
import io.github.luversof.boot.connectioninfo.DefaultConnectionInfoRegistry;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for ConnectionInfo support.
 * @author bluesky
 *
 */
@AutoConfiguration("blueskyBootConnectionInfoAutoConfiguration")
@EnableConfigurationProperties(ConnectionInfoProperties.class)
@ConditionalOnProperty(prefix = "bluesky-boot.connection-info", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ConnectionInfoAutoConfiguration {
	
	@Bean
	ConnectionInfoDevCheckController connectionInfoDevCheckController() {
		return new ConnectionInfoDevCheckController();
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	ConnectionInfoRegistry connectionInfoRegistry(List<ConnectionInfoLoader> connectionInfoLoaderList) {
		var connectionInfoRegistry = new DefaultConnectionInfoRegistry();
		connectionInfoLoaderList.forEach(x -> connectionInfoRegistry.addConnectionInfoList(x.load()));
		return connectionInfoRegistry;
	}
	
}
