package io.github.luversof.boot.connectioninfo.jdbc;

import java.util.Map;

import io.github.luversof.boot.connectioninfo.ConnectionConfig;
import lombok.Data;

@Data
public class DataSourceConnectionConfig implements ConnectionConfig {
	
	private String connection;
	
	private String url;
	
	private String username;
	
	private String password;
	
	private Map<String, Object> extradata;
	

}