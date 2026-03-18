package dev.ddaengyo.order.service;

import dev.ddaengyo.entity.Menu;
import dev.ddaengyo.entity.Order;
import dev.ddaengyo.entity.User;
import dev.ddaengyo.menu.respository.MenuRepository;
import dev.ddaengyo.order.dto.OrderRequest;
import dev.ddaengyo.order.dto.OrderResponse;
import dev.ddaengyo.repository.OrderRepository;
import dev.ddaengyo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public OrderResponse createOrder(String email, OrderRequest request) {
        // 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 메뉴(상품) 조회
        Menu menu = menuRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        int totalPrice = menu.getPrice() * request.getQuantity();

        Order order = Order.create(
                user.getUserId(),
                menu.getMenuId(),
                menu.getStore().getStoreId(),  // getStoreId() → getStore().getStoreId()
                request.getQuantity(),
                totalPrice,
                "CARD",
                null
        );

        Order saved = orderRepository.save(order);

        return new OrderResponse(
                saved.getId(),
                saved.getProductId(),
                saved.getQuantity(),
                saved.getStatus()
        );
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(String email, Long orderId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        Order order = orderRepository.findByIdAndUserId(orderId, user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("주문 내역을 찾을 수 없거나 접근 권한이 없습니다."));

        return new OrderResponse(
                order.getId(),
                order.getProductId(),
                order.getQuantity(),
                order.getStatus()
        );
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return orderRepository.findByUserId(user.getUserId())
                .stream()
                .map(order -> new OrderResponse(
                        order.getId(),
                        order.getProductId(),
                        order.getQuantity(),
                        order.getStatus()
                ))
                .toList();
    }
}