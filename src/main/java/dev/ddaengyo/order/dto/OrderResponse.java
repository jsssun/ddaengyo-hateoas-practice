package dev.ddaengyo.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@AllArgsConstructor
public class OrderResponse extends RepresentationModel<OrderResponse> {
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private String status;
}