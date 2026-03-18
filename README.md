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

### 메뉴
- 전체 메뉴 조회 (인증 불필요)
- 메뉴 단건 조회 (인증 불필요)
- 메뉴 등록 / 수정 / 삭제 (OWNER 전용)
- 모든 조회 응답에 HATEOAS `_links` 포함

### 주문
- 인증된 사용자만 주문 가능
- 주문 단건 조회 (본인 주문만 접근 가능)
- 내 주문 목록 조회
- 모든 주문 응답에 HATEOAS `_links` 포함
  - `self` : 해당 주문 상세 조회 링크
  - `profile` : API 문서 링크 (Swagger UI)
  - `product` : 주문한 상품 상세 조회 링크
  - `list-products` : 전체 상품 목록 조회 링크

---

## 주요 엔드포인트

### 인증 (auth-controller)

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | `/api/auth/signup` | 회원가입 | 불필요 |
| POST | `/api/auth/login` | 로그인 (JWT 발급) | 불필요 |

### 메뉴 (menu-controller)

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| GET | `/api/store/{storeId}/menu` | 메뉴 목록 조회 | 불필요 |
| GET | `/api/store/{storeId}/menu/{menuId}` | 메뉴 단건 조회 | 불필요 |
| POST | `/api/store/{storeId}/menu` | 메뉴 등록 | OWNER |
| PUT | `/api/store/{storeId}/menu/{menuId}` | 메뉴 수정 | OWNER |
| DELETE | `/api/store/{storeId}/menu/{menuId}` | 메뉴 삭제 | OWNER |

### 주문 (order-controller)

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| GET | `/api/orders` | 내 주문 목록 조회 | CUSTOMER |
| GET | `/api/orders/{orderId}` | 주문 단건 조회 | CUSTOMER |
| POST | `/api/orders` | 주문 생성 | CUSTOMER |

---

## 인증 방법

### 1. 회원가입

```http
POST /api/auth/signup
Content-Type: application/json

{
  "email": "user@test.com",
  "password": "1234",
  "name": "홍길동",
  "phone": "01012345678",
  "role": "CUSTOMER"
}
```

### 2. 로그인 → 토큰 발급

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@test.com",
  "password": "1234"
}
```

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

### 3. 인증이 필요한 API 호출

발급받은 토큰을 `Authorization` 헤더에 포함해서 요청한다.

```http
POST /api/orders
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
Content-Type: application/json
```

### Swagger UI에서 인증하는 방법

1. `http://localhost:8080/swagger-ui/index.html` 접속
2. 우측 상단 **Authorize** 버튼 클릭
3. 로그인 응답에서 받은 `accessToken` 값 입력
4. 이후 모든 요청에 자동으로 Bearer 토큰이 포함됨

> `Bearer ` 접두어 없이 토큰 값만 입력하면 된다.

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
![ddaengyo_erd](https://github.com/user-attachments/assets/1a53ec05-7279-414b-b43b-c335d509965f)
원활한 실습 진행을 위해 찜, 장바구니, 리뷰 등은 생략함.


---

## HATEOAS

### 개념

HATEOAS(Hypermedia as the Engine of Application State)는 REST API 응답에 관련 리소스 링크를 포함시켜 클라이언트가 다음 행동을 API 문서 없이도 알 수 있게 하는 설계 원칙이다.

### 구현 방식

`EntityModel` / `CollectionModel` 로 응답을 감싸고 Controller에서 링크를 추가한다.

```java
// Controller에서 링크 조립
EntityModel<MenuResponse> model = EntityModel.of(response,
    linkTo(methodOn(MenuController.class).getMenu(storeId, menuId)).withSelfRel(),
    linkTo(methodOn(MenuController.class).getMenus(storeId)).withRel("menus")
);
```

### 응답 예시

```json
{
  "menuId": 1,
  "name": "황금올리브 후라이드",
  "price": 18000,
  "_links": {
    "self":  { "href": "http://localhost:8080/api/store/1/menu/1" },
    "menus": { "href": "http://localhost:8080/api/store/1/menu" }
  }
}
```

### 목록 응답 예시 (URI Template)

```json
{
  "_embedded": {
    "menuResponseList": [
      { "menuId": 1, "name": "황금올리브 후라이드" },
      { "menuId": 2, "name": "매콤달콤 양념치킨" }
    ]
  },
  "_links": {
    "self": { "href": "http://localhost:8080/api/store/1/menu" },
    "menu": { "href": "http://localhost:8080/api/store/1/menu/{menuId}", "templated": true }
  }
}
```

### 링크 종류

| rel | 설명 |
|-----|------|
| self | 현재 리소스 URI |
| menus | 메뉴 목록 URI |
| menu | 단건 조회용 URI Template (`{menuId}` 치환) |
| profile | API 문서 링크 (Swagger UI) |
