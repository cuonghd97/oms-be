package com.semicolon.oms.order.service;

import com.semicolon.oms.catalog.entity.Product;
import com.semicolon.oms.catalog.repository.ProductRepository;
import com.semicolon.oms.common.exception.*;
import com.semicolon.oms.common.response.PagedResponse;
import com.semicolon.oms.inventory.entity.Inventory;
import com.semicolon.oms.inventory.repository.InventoryRepository;
import com.semicolon.oms.messaging.event.*;
import com.semicolon.oms.messaging.producer.EventProducer;
import com.semicolon.oms.order.dto.*;
import com.semicolon.oms.order.entity.Order;
import com.semicolon.oms.order.entity.OrderItem;
import com.semicolon.oms.order.entity.OrderStatus;
import com.semicolon.oms.order.repository.OrderRepository;
import com.semicolon.oms.payment.entity.Payment;
import com.semicolon.oms.payment.entity.PaymentStatus;
import com.semicolon.oms.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final PaymentRepository paymentRepository;
    private final EventProducer eventProducer;

    @Transactional
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        String orderCode = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Order order = Order.builder()
                .orderCode(orderCode)
                .userId(userId)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemReq.getProductId()));

            Inventory inventory = inventoryRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", product.getId()));

            if (inventory.getAvailableQuantity() < itemReq.getQuantity()) {
                throw new InsufficientInventoryException(product.getName(), itemReq.getQuantity(), inventory.getAvailableQuantity());
            }

            // Deduct inventory
            inventory.setQuantity(inventory.getQuantity() - itemReq.getQuantity());
            inventoryRepository.save(inventory);

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(product.getId())
                    .productName(product.getName())
                    .productSku(product.getSku())
                    .unitPrice(product.getPrice())
                    .quantity(itemReq.getQuantity())
                    .totalPrice(itemTotal)
                    .build();

            order.getItems().add(orderItem);
            totalAmount = totalAmount.add(itemTotal);
        }

        order.setTotalAmount(totalAmount);
        order = orderRepository.save(order);

        // Create payment record
        Payment payment = Payment.builder()
                .orderId(order.getId())
                .amount(totalAmount)
                .build();
        paymentRepository.save(payment);

        // Publish Kafka event
        eventProducer.publishOrderCreated(order);

        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getCustomerOrders(Long userId, Pageable pageable) {
        Page<Order> page = orderRepository.findByUserId(userId, pageable);
        return toPagedResponse(page);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        if (!order.getUserId().equals(userId)) {
            throw new ForbiddenException("You can only view your own orders");
        }
        return toResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        if (!order.getUserId().equals(userId)) {
            throw new ForbiddenException("You can only cancel your own orders");
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException(order.getStatus().name(), OrderStatus.CANCELLED.name());
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());

        // Restore inventory
        for (OrderItem item : order.getItems()) {
            Inventory inventory = inventoryRepository.findByProductId(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", item.getProductId()));
            inventory.setQuantity(inventory.getQuantity() + item.getQuantity());
            inventoryRepository.save(inventory);
        }

        order = orderRepository.save(order);
        eventProducer.publishOrderCancelled(order);
        return toResponse(order);
    }

    // Admin methods
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getAllOrders(Pageable pageable) {
        Page<Order> page = orderRepository.findAll(pageable);
        return toPagedResponse(page);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderByIdAdmin(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        return toResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        validateStatusTransition(order.getStatus(), request.getStatus());

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(request.getStatus());

        if (request.getStatus() == OrderStatus.CANCELLED) {
            order.setCancelledAt(LocalDateTime.now());
            // Restore inventory
            for (OrderItem item : order.getItems()) {
                Inventory inventory = inventoryRepository.findByProductId(item.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", item.getProductId()));
                inventory.setQuantity(inventory.getQuantity() + item.getQuantity());
                inventoryRepository.save(inventory);
            }
        }

        order = orderRepository.save(order);
        eventProducer.publishOrderStatusChanged(order, oldStatus.name());
        return toResponse(order);
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus target) {
        Map<OrderStatus, Set<OrderStatus>> validTransitions = Map.of(
                OrderStatus.PENDING, Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
                OrderStatus.CONFIRMED, Set.of(OrderStatus.SHIPPING, OrderStatus.CANCELLED),
                OrderStatus.SHIPPING, Set.of(OrderStatus.COMPLETED),
                OrderStatus.COMPLETED, Set.of(),
                OrderStatus.CANCELLED, Set.of()
        );
        if (!validTransitions.getOrDefault(current, Set.of()).contains(target)) {
            throw new InvalidOrderStatusException(current.name(), target.name());
        }
    }

    private PagedResponse<OrderResponse> toPagedResponse(Page<Order> page) {
        return PagedResponse.<OrderResponse>builder()
                .items(page.getContent().stream().map(this::toResponse).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .userId(order.getUserId())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .items(order.getItems().stream().map(this::toItemResponse).collect(Collectors.toList()))
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .cancelledAt(order.getCancelledAt())
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .productSku(item.getProductSku())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .totalPrice(item.getTotalPrice())
                .build();
    }
}
