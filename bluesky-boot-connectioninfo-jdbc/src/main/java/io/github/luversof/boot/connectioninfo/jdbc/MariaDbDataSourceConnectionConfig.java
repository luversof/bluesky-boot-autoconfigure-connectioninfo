package io.github.luversof.boot.connectioninfo.jdbc;

import java.util.Map;

public record MariaDbDataSourceConnectionConfig(String connection, String url, String username, String password, Map<String, Object> extradata) implements DataSourceConnectionConfig {

}
