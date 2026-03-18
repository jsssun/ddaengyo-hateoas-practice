# 🛵 땡겨요 - 음식 배달 서비스 HATEOAS 실습 프로젝트

> Spring Boot 기반 음식 배달 서비스 REST API  
> JWT 인증 + Spring HATEOAS 실습 프로젝트

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 4.0.3 |
| ORM | Spring Data JPA, Hibernate 7 |
| Security | Spring Security 7, JWT (jjwt 0.12.3) |
| API 문서 | SpringDoc OpenAPI 3.0 (Swagger UI) |
| HATEOAS | Spring HATEOAS 3.0 |
| DB | MySQL 8.0 |
| Build | Gradle |
| 기타 | Lombok, Validation |

---

## 프로젝트 구조

```
src/main/java/dev/ddaengyo/
├── auth/
│   ├── controller/    AuthController.java
│   ├── dto/           LoginRequest, SignUpRequest, TokenResponse
│   ├── jwt/           JwtTokenProvider, JwtAuthenticationFilter
│   └── service/       AuthService, CustomUserDetailsService
├── config/
│   └── SecurityConfig.java
├── entity/
│   ├── BaseEntity.java
│   ├── User.java
│   ├── Store.java
│   ├── Menu.java
│   ├── Order.java
│   ├── Role.java (enum)
│   └── OrderStatus.java (enum)
├── menu/
│   ├── controller/    MenuController.java
│   ├── dto/           MenuRequest, MenuResponse
│   ├── repository/    MenuRepository
│   └── service/       MenuService.java
├── order/
│   ├── controller/    OrderController.java
│   ├── dto/           OrderRequest, OrderResponse
│   └── service/       OrderService.java
├── repository/
│   ├── UserRepository.java
│   ├── StoreRepository.java
│   └── OrderRepository.java
└── Application.java

src/main/resources/
├── static/
│   └── index.html     (시연용 UI)
├── application.properties
└── init.sql
```

---

## 주요 기능

### 인증
- 회원가입 시 이메일 중복 체크 및 BCrypt 비밀번호 암호화 저장
- 로그인 성공 시 JWT 액세스 토큰 발급
- 모든 쓰기 요청(등록/수정/삭제/주문)은 JWT Bearer 토큰 인증 필요
- `CUSTOMER` / `OWNER` 역할 기반 권한 분리

### 상품
- 전체 상품 조회 (카테고리 필터링, 페이징 포함)
- 상품 상세 조회
- 상품 등록 / 수정 / 삭제 (OWNER 전용)
- 모든 조회 응답에 HATEOAS `_links` 포함

### 주문
- 인증된 사용자만 상품 주문 가능
- 주문 단건 조회 (본인 주문만 접근 가능)
- 내 주문 목록 조회
- 모든 주문 응답에 HATEOAS `_links` 포함
  - `self` : 해당 주문 상세 조회 링크
  - `profile` : API 문서 링크 (Swagger UI)
  - `product` : 주문한 상품 상세 조회 링크
  - `list-products` : 전체 상품 목록 조회 링크
 
---

## 실행 방법

### 1. DB 설정

MySQL에서 아래 순서로 실행한다.

```sql
CREATE DATABASE ddaengyo;
USE ddaengyo;
-- init.sql 실행
```

### 2. application.properties 설정

```properties
spring.application.name=ddaengyo

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/ddaengyo?serverTimezone=UTC&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=1234

spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=none

jwt.secret=ddaengyo-secret-key-must-be-at-least-32-characters-long
jwt.expiration-ms=86400000

management.endpoints.web.exposure.include=health
management.endpoint.health.show-details=always
```

### 3. 실행

```bash
./gradlew bootRun
```

실행 후 접속 가능한 URL은 다음과 같다.

| URL | 설명 |
|-----|------|
| http://localhost:8080 | 시연용 UI |
| http://localhost:8080/swagger-ui/index.html | API 문서 |
| http://localhost:8080/actuator/health | 헬스 체크 |

---

## DB 설계

```
user
├── user_id (PK)
├── email, password, name, phone
├── role (CUSTOMER / OWNER)
└── current_address

store
├── store_id (PK)
├── name, category, address, phone, content
├── min_delivery_price, delivery_tip
├── min_delivery_time, max_delivery_time
├── rating, dibs_count, review_count
└── operation_hours, closed_days, delivery_address

menu
├── menu_id (PK)
├── store_id (FK → store)
├── category, name, price
└── popularity

orders
├── id (PK)
├── store_id (FK → store)
├── user_id (FK → user)
├── product_id, quantity
├── payment_method, total_price, requests
└── status (PENDING / DELIVERING / DELIVERED)
```

---

## HATEOAS

### 개념

HATEOAS(Hypermedia as the Engine of Application State)는 REST API 응답에 관련 리소스 링크를 포함시켜 클라이언트가 다음 행동을 API 문서 없이도 알 수 있게 하는 설계 원칙이다.

### 구현 방식

`RepresentationModel`을 상속한 응답 클래스에 `.add()`로 링크를 추가하면 응답 JSON에 `_links` 블록이 자동으로 생성된다.

```java
// OrderResponse가 RepresentationModel을 상속
public class OrderResponse extends RepresentationModel<OrderResponse> { ... }

// Controller에서 링크 추가
response.add(
    linkTo(methodOn(OrderController.class).getOrder(email, orderId)).withSelfRel(),
    Link.of("/swagger-ui/index.html").withRel("profile"),
    Link.of("/api/products/" + productId).withRel("product"),
    Link.of("/api/products").withRel("list-products")
);
```

### 링크 종류

| rel | 설명 |
|-----|------|
| self | 현재 리소스 URI |
| profile | API 문서 링크 (Swagger UI) |
| product | 주문한 상품 상세 조회 링크 |
| list-products | 전체 상품 목록 조회 링크 |
