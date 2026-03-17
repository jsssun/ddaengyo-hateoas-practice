package dev.ddaengyo.order.controller;

import dev.ddaengyo.order.dto.OrderRequest;
import dev.ddaengyo.order.dto.OrderResponse;
import dev.ddaengyo.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody OrderRequest request) {

        OrderResponse response = orderService.createOrder(email, request);

        response.add(
                linkTo(methodOn(OrderController.class).getOrder(email, response.getOrderId())).withSelfRel(),
                Link.of("/swagger-ui/index.html").withRel("profile"),
                Link.of("/api/products/" + response.getProductId()).withRel("product"),
                Link.of("/api/products").withRel("list-products")
        );

        return ResponseEntity
                .created(URI.create("/api/orders/" + response.getOrderId()))
                .body(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @AuthenticationPrincipal String email,
            @PathVariable Long orderId) {

        OrderResponse response = orderService.getOrder(email, orderId);

        response.add(
                linkTo(methodOn(OrderController.class).getOrder(email, orderId)).withSelfRel(),
                Link.of("/swagger-ui/index.html").withRel("profile"),
                Link.of("/api/products/" + response.getProductId()).withRel("product"),
                Link.of("/api/products").withRel("list-products")
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            @AuthenticationPrincipal String email) {

        List<OrderResponse> orders = orderService.getMyOrders(email);

        orders.forEach(response -> response.add(
                linkTo(methodOn(OrderController.class).getOrder(email, response.getOrderId())).withSelfRel(),
                Link.of("/swagger-ui/index.html").withRel("profile"),
                Link.of("/api/products/" + response.getProductId()).withRel("product"),
                Link.of("/api/products").withRel("list-products")
        ));

        return ResponseEntity.ok(orders);
    }
}