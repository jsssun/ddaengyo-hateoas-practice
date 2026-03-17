create database ddaengyo;
use ddaengyo;

-- =============================================
-- DDL
-- =============================================

CREATE TABLE user (
    user_id         BIGINT       AUTO_INCREMENT PRIMARY KEY,
    password        VARCHAR(255) NOT NULL,
    name            VARCHAR(100) NOT NULL,
    phone           VARCHAR(11)  NOT NULL,
    email           VARCHAR(100) NOT NULL,
    role            VARCHAR(100) NOT NULL,
    current_address VARCHAR(255),
    created_date    DATETIME     NOT NULL,
    modified_date   DATETIME     NOT NULL
);

CREATE TABLE store (
    store_id           BIGINT        AUTO_INCREMENT PRIMARY KEY,
    name               VARCHAR(255)  NOT NULL,
    category           VARCHAR(20)   NOT NULL,
    address            VARCHAR(255)  NOT NULL,
    phone              VARCHAR(20)   NOT NULL,
    content            VARCHAR(255),
    min_delivery_price INT           NOT NULL,
    delivery_tip       INT           NOT NULL DEFAULT 0,
    min_delivery_time  INT,
    max_delivery_time  INT,
    rating             DECIMAL(3, 1) NOT NULL DEFAULT 0.0,
    dibs_count         INT           NOT NULL DEFAULT 0,
    review_count       INT           NOT NULL DEFAULT 0,
    operation_hours    VARCHAR(255),
    closed_days        VARCHAR(255),
    delivery_address   VARCHAR(255),
    created_date       DATETIME      NOT NULL,
    modified_date      DATETIME      NOT NULL
);

CREATE TABLE menu (
    menu_id       BIGINT       AUTO_INCREMENT PRIMARY KEY,
    store_id      BIGINT       NOT NULL,
    category      VARCHAR(100) NOT NULL,
    name          VARCHAR(255) NOT NULL,
    price         INT          NOT NULL,
    popularity    BOOLEAN,
    created_date  DATETIME     NOT NULL,
    modified_date DATETIME     NOT NULL,
    CONSTRAINT fk_menu_store FOREIGN KEY (store_id) REFERENCES store (store_id)
);

CREATE TABLE orders (
    id             BIGINT       AUTO_INCREMENT PRIMARY KEY,
    store_id       BIGINT       NOT NULL,
    user_id        BIGINT       NOT NULL,
    payment_method VARCHAR(255) NOT NULL,
    total_price    INT          NOT NULL,
    requests       VARCHAR(255),
    status         VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    created_date   DATETIME     NOT NULL,
    modified_date  DATETIME     NOT NULL,
    CONSTRAINT fk_order_store FOREIGN KEY (store_id) REFERENCES store (store_id),
    CONSTRAINT fk_order_user  FOREIGN KEY (user_id)  REFERENCES user  (user_id)
);

ALTER TABLE orders
    ADD COLUMN product_id BIGINT NOT NULL DEFAULT 0 AFTER user_id,
    ADD COLUMN quantity   INT    NOT NULL DEFAULT 1 AFTER product_id;

-- =============================================
-- INSERT
-- =============================================

INSERT INTO user (password, name, phone, email, role, current_address, created_date, modified_date) VALUES
('$2a$10$hashedpw1', '김고객', '01011112222', 'customer1@test.com', 'CUSTOMER', '서울시 강남구 테헤란로 1길', NOW(), NOW()),
('$2a$10$hashedpw2', '이사장', '01033334444', 'owner1@test.com',    'OWNER',    '서울시 마포구 홍대입구역',   NOW(), NOW()),
('$2a$10$hashedpw3', '박고객', '01055556666', 'customer2@test.com', 'CUSTOMER', '서울시 송파구 잠실동 123',   NOW(), NOW());

INSERT INTO store (name, category, address, phone, content, min_delivery_price, delivery_tip,
                   min_delivery_time, max_delivery_time, rating, dibs_count, review_count,
                   operation_hours, closed_days, delivery_address, created_date, modified_date) VALUES
('맛있는 치킨집', '치킨', '서울시 마포구 홍대입구역 3번 출구', '02-1234-5678',
 '바삭한 튀김옷의 정통 치킨', 15000, 2000, 20, 40, 4.5, 120, 85,
 '11:00-23:00', '월요일', '서울시 마포구 일대', NOW(), NOW()),

('스시히로', '일식', '서울시 강남구 역삼동 456', '02-9876-5432',
 '신선한 재료로 만드는 스시', 20000, 3000, 30, 60, 4.8, 200, 142,
 '12:00-22:00', NULL, '서울시 강남구 일대', NOW(), NOW()),

('엄마손 분식', '분식', '서울시 송파구 잠실동 789', '02-5555-1234',
 '집밥 같은 분식', 8000, 1000, 15, 30, 4.2, 55, 30,
 '09:00-21:00', '일요일', '서울시 송파구 일대', NOW(), NOW());

INSERT INTO menu (store_id, category, name, price, popularity, created_date, modified_date) VALUES
(1, '후라이드', '황금올리브 후라이드', 18000, TRUE,  NOW(), NOW()),
(1, '양념',    '매콤달콤 양념치킨',   18000, TRUE,  NOW(), NOW()),
(1, '반반',    '반반치킨',           19000, FALSE, NOW(), NOW()),
(2, '롤',      '연어 아보카도 롤',    16000, TRUE,  NOW(), NOW()),
(2, '사시미',  '참치 사시미',         22000, FALSE, NOW(), NOW()),
(3, '분식',    '떡볶이',             7000,  TRUE,  NOW(), NOW()),
(3, '분식',    '순대국밥',           9000,  FALSE, NOW(), NOW());

INSERT INTO orders (store_id, user_id, payment_method, total_price, requests, status, created_date, modified_date) VALUES
(1, 1, 'CARD',       20000, '문 앞에 두고 가주세요', 'DELIVERED',  NOW(), NOW()),
(2, 1, 'KAKAO_PAY',  38000, '젓가락 많이 주세요',   'DELIVERING', NOW(), NOW()),
(3, 3, 'NAVER_PAY',  16000, NULL,                   'PENDING',    NOW(), NOW());
