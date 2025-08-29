# bluesky-boot-autoconfigure-connectioninfo

## 소개
`bluesky-boot-autoconfigure-connectioninfo`는 Bluesky Boot 기반 애플리케이션에서 다양한 외부 시스템(예: DB, MongoDB 등)과의 연결 정보를 자동으로 구성하고 관리할 수 있도록 지원하는 Spring Boot AutoConfiguration 모듈입니다.

## 주요 기능
- 다양한 연결 정보(ConnectionInfo) 자동 구성
- JDBC, MongoDB 등 여러 연결 타입 지원
- 연결 정보 로더 및 리더 구조로 확장성 제공
- 느슨한 결합 구조로 다양한 연결 리더 주입 가능
- Spring Boot 환경에서 손쉬운 통합 및 확장

## 구조
- **ConnectionInfoLoader**: 연결 정보를 로드하는 역할. 다양한 리더를 주입받아 느슨한 결합 구조로 동작.
- **ConnectionConfigReader**: 실제 연결 설정을 읽어오는 인터페이스. JDBC, MongoDB 등 구현체 제공.
- **ConnectionInfo**: 연결 대상에 대한 정보 객체.

## 사용법
1. 의존성 추가

```xml
<dependency>
  <groupId>io.github.luversof</groupId>
  <artifactId>bluesky-boot-autoconfigure-connectioninfo</artifactId>
</dependency>
```

2. application.yml 또는 application.properties에 연결 정보 설정
3. 필요한 연결 리더(JDBC, MongoDB 등) Bean 등록 또는 자동 주입
4. ConnectionInfoLoader를 통해 연결 정보 로드 및 사용

## 확장 방법
- 새로운 연결 타입이 필요할 경우, `ConnectionConfigReader` 인터페이스를 구현하여 Bean으로 등록하면 자동으로 연동 가능
- 느슨한 결합 구조로 다양한 외부 시스템 연결 지원

## 예시

```java
@Autowired
private ConnectionInfoLoader<MyType, MyConfig> connectionInfoLoader;

List<ConnectionInfo<MyType>> infos = connectionInfoLoader.load();
```

