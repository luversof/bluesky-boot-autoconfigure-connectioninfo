package io.github.luversof.boot.connectioninfo;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.luversof.boot.devcheck.annotation.DevCheckController;
import io.github.luversof.boot.devcheck.annotation.DevCheckDescription;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@DevCheckController
@RequestMapping(value = "/blueskyBoot/connectionInfo", produces = MediaType.APPLICATION_JSON_VALUE)
public class ConnectionInfoDevCheckController {
	
	private List<ConnectionInfoRegistry<?>> connectionInfoRegistryList;
	
	@DevCheckDescription("connectionInfoRegistryList")
	@GetMapping("/connectionInfoRegistryList")
	List<ConnectionInfoRegistry<?>> connectionInfoRegistryList() {
		return connectionInfoRegistryList;
	}
	
}
