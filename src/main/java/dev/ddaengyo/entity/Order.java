package dev.ddaengyo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 실제 주문 헤더 정보 -> Cart가 order_id로 연결
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long storeId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 255)
    private String paymentMethod;

    @Column(nullable = false)
    private int totalPrice;

    @Column(length = 255)
    private String requests;

    protected Order() {
    }

    private Order(Long storeId, Long userId, String paymentMethod, String requests) {
        this.storeId = storeId;
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.requests = requests;
        this.totalPrice = 0;
    }
}
