# Bluesky Boot AutoConfigure ConnectionInfo

Spring Boot 환경에서 다양한 외부 시스템(데이터베이스, MongoDB 등)과의 연결 정보를 동적으로 로드하고 관리하는 자동 구성 모듈입니다.

## 개요

이 모듈은 연결 정보를 메타데이터로 저장하고, 런타임에 동적으로 로드하여 실제 연결 객체를 생성하는 패턴을 제공합니다. 이를 통해 하드코딩된 연결 정보 없이 유연하고 확장 가능한 연결 관리를 할 수 있습니다.

## 핵심 아키텍처

### 주요 구성 요소와 관계

```
┌─────────────────────────┐    reads from    ┌──────────────────────┐
│   ConnectionConfig      │ ◄────────────── │  ConnectionInfoReader │
│   (연결 설정 정보)        │                  │  (설정 정보 리더)       │
└─────────────────────────┘                  └──────────────────────┘
           │                                              │
           │ used by                                      │ injected into
           ▼                                              ▼
┌─────────────────────────┐    creates      ┌──────────────────────┐
│   ConnectionInfo<T>     │ ◄────────────── │  ConnectionInfoLoader │
│   (실제 연결 객체)        │                  │  (연결 객체 로더)       │
└─────────────────────────┘                  └──────────────────────┘
           │                                              │
           │ managed by                                   │ loads
           ▼                                              │
┌─────────────────────────┐                              │
│ ConnectionInfoRegistry  │ ◄────────────────────────────┘
│ (연결 정보 레지스트리)     │
└─────────────────────────┘
```

### 1. ConnectionConfig
```java
public interface ConnectionConfig {
    String getConnection();
}
```
- **역할**: 연결에 필요한 기본 설정 정보를 정의하는 인터페이스
- **특징**: 각 연결 타입별로 구체적인 구현체가 존재 (DataSourceConnectionConfig, MongoClientConnectionConfig 등)
- **책임**: 연결 이름과 연결에 필요한 메타데이터 제공

### 2. ConnectionInfoReader<C extends ConnectionConfig>
```java
public interface ConnectionInfoReader<C extends ConnectionConfig> {
    String getReaderKey();
    List<C> readConnectionConfigList(List<String> connectionList);
}
```
- **역할**: 외부 저장소(DB, 파일 등)에서 연결 설정 정보를 읽어오는 인터페이스
- **특징**: 다양한 저장소 타입을 지원할 수 있도록 확장 가능
- **책임**: 연결 이름 목록을 받아 해당하는 ConnectionConfig 객체들을 반환

### 3. ConnectionInfo<T>
```java
@Data
@AllArgsConstructor
public class ConnectionInfo<T> {
    private final ConnectionInfoKey key;
    private final T connection;
}
```
- **역할**: 실제 연결 객체와 식별 키를 담는 컨테이너
- **특징**: 제네릭 타입으로 다양한 연결 객체 타입 지원 (DataSource, MongoClient 등)
- **책임**: 생성된 연결 객체를 키와 함께 관리

### 4. ConnectionInfoKey
```java
public record ConnectionInfoKey(String loaderKey, String connectionKey) {}
```
- **역할**: 연결 정보를 고유하게 식별하는 키
- **구성**: loaderKey (로더 타입) + connectionKey (연결 이름)
- **특징**: Java Record로 구현된 불변 객체

### 5. ConnectionInfoLoader<T, C extends ConnectionConfig>
```java
public interface ConnectionInfoLoader<T, C extends ConnectionConfig> {
    String getLoaderKey();
    List<ConnectionInfoReader<C>> getConnectionInfoReaderList();
    List<ConnectionInfo<T>> load();
    List<ConnectionInfo<T>> load(List<String> connectionList);
}
```
- **역할**: ConnectionInfoReader들을 활용하여 실제 연결 객체를 생성하고 관리
- **특징**: 여러 Reader를 주입받아 다양한 소스에서 설정 정보 수집 가능
- **책임**: 설정 정보를 바탕으로 실제 사용 가능한 연결 객체 생성

### 6. ConnectionInfoRegistry<T>
```java
@FunctionalInterface
public interface ConnectionInfoRegistry<T> {
    List<ConnectionInfo<T>> getConnectionInfoList();
    // ... default methods
}
```
- **역할**: 생성된 연결 정보들을 중앙에서 관리하는 레지스트리
- **특징**: 함수형 인터페이스로 구현이 간단함
- **책임**: 연결 정보 조회, 추가, 검색 기능 제공

## 동작 흐름

1. **설정 단계**: Properties에서 reader와 loader 설정 정보 로드
2. **Reader 단계**: ConnectionInfoReader가 외부 저장소에서 ConnectionConfig 정보 읽기
3. **Loader 단계**: ConnectionInfoLoader가 Reader들을 활용해 ConnectionInfo 객체 생성
4. **Registry 단계**: 생성된 ConnectionInfo들을 Registry에 등록
5. **사용 단계**: 애플리케이션에서 Registry를 통해 연결 객체 조회 및 사용

## 설정 구조

### application.yml 설정 예시

```yaml
bluesky-boot:
  connection-info:
    readers:
      mariadb-datasource:
        enabled: true
        properties:
          url: "jdbc:mariadb://localhost:3306/connection_meta"
          username: "{cipher}encrypted_username"
          password: "{cipher}encrypted_password"
      mongodb-mongoclient:
        enabled: true
        properties:
          connectionString: "mongodb://localhost:27017"
          database: "connection_meta"
    
    loaders:
      hikaridatasource:
        enabled: true
        connections:
          app1: ["db-primary", "db-secondary"]
          app2: ["db-reporting", "db-analytics"]
      mongoclient:
        enabled: true
        connections:
          service1: ["mongo-primary", "mongo-cache"]
```

### 설정 구조 설명

- **readers**: 연결 설정 정보를 읽어올 소스 정의
  - `enabled`: 해당 reader 활성화 여부
  - `properties`: reader별 연결 정보 (메타데이터 저장소 접근 정보)

- **loaders**: 실제 연결 객체를 생성할 loader 정의
  - `enabled`: 해당 loader 활성화 여부
  - `connections`: 애플리케이션별 사용할 연결 이름 목록

## 모듈 구성

### 1. 핵심 모듈
- **bluesky-boot-connectioninfo**: 핵심 인터페이스와 모델 정의
- **bluesky-boot-autoconfigure-connectioninfo**: Spring Boot 자동 구성

### 2. 구현 모듈
- **bluesky-boot-connectioninfo-jdbc**: JDBC DataSource 지원
- **bluesky-boot-autoconfigure-connectioninfo-jdbc**: JDBC 자동 구성
- **bluesky-boot-connectioninfo-mongodb**: MongoDB 지원
- **bluesky-boot-autoconfigure-connectioninfo-mongodb**: MongoDB 자동 구성

## 사용 예시

### 1. 기본 사용법

```java
@Service
public class DatabaseService {
    
    private final ConnectionInfoRegistry<HikariDataSource> dataSourceRegistry;
    
    public DatabaseService(ConnectionInfoRegistry<HikariDataSource> dataSourceRegistry) {
        this.dataSourceRegistry = dataSourceRegistry;
    }
    
    public void executeQuery(String connectionName) {
        ConnectionInfoKey key = new ConnectionInfoKey("hikaridatasource", connectionName);
        ConnectionInfo<HikariDataSource> connectionInfo = dataSourceRegistry.getConnectionInfo(key);
        
        try (Connection conn = connectionInfo.getConnection().getConnection()) {
            // 데이터베이스 작업 수행
        }
    }
}
```

### 2. 특정 연결 정보로 JdbcTemplate 생성

```java
@Configuration
public class DatabaseConfiguration {
    
    @Bean
    @Primary
    public JdbcTemplate primaryJdbcTemplate(ConnectionInfoRegistry<HikariDataSource> registry) {
        ConnectionInfoKey key = new ConnectionInfoKey("hikaridatasource", "db-primary");
        HikariDataSource dataSource = registry.getConnectionInfo(key).getConnection();
        return new JdbcTemplate(dataSource);
    }
}
```

### 3. 동적 연결 선택

```java
@Service
public class MultiTenantService {
    
    private final ConnectionInfoRegistry<HikariDataSource> registry;
    
    public void processForTenant(String tenantId) {
        String connectionName = "tenant-" + tenantId;
        ConnectionInfoKey key = new ConnectionInfoKey("hikaridatasource", connectionName);
        
        try {
            ConnectionInfo<HikariDataSource> connectionInfo = registry.getConnectionInfo(key);
            // 테넌트별 데이터베이스 작업
        } catch (RuntimeException e) {
            // 연결 정보가 없는 경우 처리
            log.warn("Connection not found for tenant: {}", tenantId);
        }
    }
}
```

## 확장 방법

### 1. 새로운 연결 타입 지원

```java
// 1. ConnectionConfig 구현
@Data
public class RedisConnectionConfig implements ConnectionConfig {
    private String connection;
    private String host;
    private int port;
    private String password;
}

// 2. ConnectionInfoReader 구현
@Component
public class RedisConnectionInfoReader implements ConnectionInfoReader<RedisConnectionConfig> {
    
    @Override
    public String getReaderKey() {
        return "redis";
    }
    
    @Override
    public List<RedisConnectionConfig> readConnectionConfigList(List<String> connectionList) {
        // Redis 연결 정보 읽기 구현
        return Collections.emptyList();
    }
}

// 3. ConnectionInfoLoader 구현
@Component
public class RedisConnectionInfoLoader implements ConnectionInfoLoader<JedisPool, RedisConnectionConfig> {
    
    @Override
    public String getLoaderKey() {
        return "redis";
    }
    
    @Override
    public List<ConnectionInfo<JedisPool>> load() {
        // JedisPool 생성 로직 구현
        return Collections.emptyList();
    }
    
    // ... 기타 메서드 구현
}
```

### 2. 커스텀 Reader 구현

```java
@Component
public class FileBasedConnectionInfoReader implements ConnectionInfoReader<DataSourceConnectionConfig> {
    
    @Override
    public String getReaderKey() {
        return "file-datasource";
    }
    
    @Override
    public List<DataSourceConnectionConfig> readConnectionConfigList(List<String> connectionList) {
        // 파일에서 연결 정보 읽기
        return readFromFile(connectionList);
    }
    
    private List<DataSourceConnectionConfig> readFromFile(List<String> connectionList) {
        // JSON, YAML, Properties 파일 등에서 읽기 구현
        return Collections.emptyList();
    }
}
```

## 보안 고려사항

### 1. 암호화된 자격증명
```yaml
bluesky-boot:
  connection-info:
    readers:
      mariadb-datasource:
        properties:
          username: "{cipher}AQB8S7xZ..." # 암호화된 값
          password: "{cipher}AQC9R8yA..." # 암호화된 값
```

### 2. 환경별 분리
- 개발/스테이징/운영 환경별로 다른 메타데이터 저장소 사용
- Spring Profiles를 활용한 환경별 설정 관리

## 모니터링 및 디버깅

### 1. 연결 정보 상태 확인
```java
@RestController
public class ConnectionInfoController {
    
    private final ConnectionInfoRegistry<HikariDataSource> registry;
    
    @GetMapping("/connection-info/status")
    public Map<String, Object> getConnectionStatus() {
        List<ConnectionInfo<HikariDataSource>> connections = registry.getConnectionInfoList();
        
        return connections.stream()
            .collect(Collectors.toMap(
                info -> info.getKey().toString(),
                info -> Map.of(
                    "active", info.getConnection().getHikariPoolMXBean().getActiveConnections(),
                    "idle", info.getConnection().getHikariPoolMXBean().getIdleConnections()
                )
            ));
    }
}
```

### 2. 로깅 설정
```properties
# 연결 정보 로딩 과정 로깅
logging.level.io.github.luversof.boot.connectioninfo=DEBUG

# 특정 구현체별 상세 로깅
logging.level.io.github.luversof.boot.connectioninfo.jdbc=TRACE
logging.level.io.github.luversof.boot.connectioninfo.mongodb=TRACE
```

## 주요 변경사항 (리팩토링 후)

1. **명명 개선**: `ConnectionConfigReader` → `ConnectionInfoReader`
2. **구조 최적화**: `ConnectionInfoKey`가 record 타입으로 구현
3. **설정 분리**: Properties가 `readers`와 `loaders`로 명확히 분리
4. **타입 안전성**: 제네릭 활용으로 컴파일 타임 타입 체크 강화

## 장점

1. **유연성**: 런타임에 연결 정보 변경 가능
2. **확장성**: 새로운 연결 타입을 쉽게 추가 가능
3. **보안성**: 암호화된 자격증명 지원
4. **중앙화**: 모든 연결 정보를 중앙에서 관리
5. **느슨한 결합**: Reader와 Loader의 독립적 구현 가능
6. **타입 안전성**: 제네릭을 활용한 컴파일 타임 타입 체크

## 주의사항

1. **메타데이터 저장소 의존성**: Reader용 데이터베이스가 항상 사용 가능해야 함
2. **초기화 순서**: ConnectionInfo 로드가 완료된 후 애플리케이션 로직 실행
3. **성능**: 대량의 연결 정보가 있을 경우 초기화 시간 고려 필요
4. **장애 처리**: Reader나 메타데이터 저장소 장애 시 대응 방안 필요

## 라이센스

Apache License 2.0