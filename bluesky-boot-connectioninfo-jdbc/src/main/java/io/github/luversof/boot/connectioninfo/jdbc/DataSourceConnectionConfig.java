package io.github.luversof.boot.connectioninfo.jdbc;

import java.util.Map;

import io.github.luversof.boot.connectioninfo.ConnectionConfig;

public interface DataSourceConnectionConfig extends ConnectionConfig {
	
	String url();
	
	String username();
	
	String password();
	
	Map<String, Object> extradata();

}