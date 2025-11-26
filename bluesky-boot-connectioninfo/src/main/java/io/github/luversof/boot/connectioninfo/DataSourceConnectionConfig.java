package io.github.luversof.boot.connectioninfo;

import java.util.Map;

import lombok.Data;

@Data
public class DataSourceConnectionConfig implements ConnectionConfig {
	
	private String connection;
	
	private String url;
	
	private String username;
	
	private String password;
	
	private Map<String, Object> extradata;
	

}