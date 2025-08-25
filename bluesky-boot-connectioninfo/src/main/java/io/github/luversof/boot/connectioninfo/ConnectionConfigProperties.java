package io.github.luversof.boot.connectioninfo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ConfigurationProperties(prefix = "bluesky-boot.connection-config")
public class ConnectionConfigProperties {
	
	private boolean enabled = true;
	
	private Map<String, ConnectionConfigReaderProperties> readers = new HashMap<>();
	
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ConnectionConfigReaderProperties {
		
		/**
		 * Whether to use this Loader or not
		 */
		private boolean enabled;
		
		/**
		 * Manage loader call information
		 * Currently used in an informal form.
		 */
		private Map<String, String> properties;
		
	}

}
