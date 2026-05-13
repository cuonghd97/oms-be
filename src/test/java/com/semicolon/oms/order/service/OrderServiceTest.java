package com.semicolon.oms.order.service;

import com.semicolon.oms.catalog.entity.Product;
import com.semicolon.oms.catalog.entity.ProductStatus;
import com.semicolon.oms.catalog.repository.ProductRepository;
import com.semicolon.oms.common.exception.InsufficientInventoryException;
import com.semicolon.oms.common.exception.InvalidOrderStatusException;
import com.semicolon.oms.inventory.entity.Inventory;
import com.semicolon.oms.inventory.repository.InventoryRepository;
import com.semicolon.oms.messaging.producer.EventProducer;
import com.semicolon.oms.order.dto.CreateOrderRequest;
import com.semicolon.oms.order.dto.OrderItemRequest;
import com.semicolon.oms.order.dto.OrderResponse;
import com.semicolon.oms.order.dto.UpdateOrderStatusRequest;
import com.semicolon.oms.order.entity.Order;
import com.semicolon.oms.order.entity.OrderStatus;
import com.semicolon.oms.order.repository.OrderRepository;
import com.semicolon.oms.payment.entity.Payment;
import com.semicolon.oms.payment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private ProductRepository productRepository;
    @Mock private InventoryRepository inventoryRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private EventProducer eventProducer;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_shouldSucceed() {
        Product product = Product.builder()
                .id(1L).name("Test Product").sku("SKU-001").price(BigDecimal.valueOf(25.00))
                .status(ProductStatus.ACTIVE).build();
        Inventory inventory = Inventory.builder()
                .id(1L).productId(1L).quantity(100).reservedQuantity(0).build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> {
            Order o = i.getArgument(0);
            o.setId(1L);
            return o;
        });
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setProductId(1L);
        itemReq.setQuantity(2);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setItems(List.of(itemReq));

        OrderResponse result = orderService.createOrder(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
        assertThat(inventory.getQuantity()).isEqualTo(98); // deducted
        verify(eventProducer).publishOrderCreated(any());
    }

    @Test
    void createOrder_insufficientStock_shouldThrow() {
        Product product = Product.builder()
                .id(1L).name("Test Product").sku("SKU-001").price(BigDecimal.TEN).build();
        Inventory inventory = Inventory.builder()
                .id(1L).productId(1L).quantity(1).reservedQuantity(0).build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));

        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setProductId(1L);
        itemReq.setQuantity(5);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setItems(List.of(itemReq));

        assertThatThrownBy(() -> orderService.createOrder(1L, request))
                .isInstanceOf(InsufficientInventoryException.class);
    }

    @Test
    void updateOrderStatus_invalidTransition_shouldThrow() {
        Order order = Order.builder().id(1L).status(OrderStatus.COMPLETED).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setStatus(OrderStatus.PENDING);

        assertThatThrownBy(() -> orderService.updateOrderStatus(1L, request))
                .isInstanceOf(InvalidOrderStatusException.class);
    }
}
