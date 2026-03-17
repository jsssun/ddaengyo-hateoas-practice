package dev.ddaengyo.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "payment_method", nullable = false, length = 255)
    private String paymentMethod;

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    @Column(name = "requests", length = 255)
    private String requests;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    protected Order() {}

    public static Order create(Long userId, Long productId, Long storeId,
                               int quantity, int totalPrice, String paymentMethod, String requests) {
        Order order = new Order();
        order.userId = userId;
        order.productId = productId;
        order.storeId = storeId;
        order.quantity = quantity;
        order.totalPrice = totalPrice;
        order.paymentMethod = paymentMethod;
        order.requests = requests;
        order.status = "ORDERED";
        return order;
    }

    public void updateTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}