package com.semicolon.oms.messaging.producer;

import com.semicolon.oms.messaging.config.KafkaTopicConfig;
import com.semicolon.oms.messaging.event.*;
import com.semicolon.oms.order.entity.Order;
import com.semicolon.oms.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderCreated(Order order) {
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .eventId(BaseEvent.newEventId())
                .eventType("ORDER_CREATED")
                .aggregateId(String.valueOf(order.getId()))
                .correlationId(BaseEvent.newCorrelationId())
                .occurredAt(Instant.now())
                .orderId(order.getId())
                .orderCode(order.getOrderCode())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .itemCount(order.getItems().size())
                .build();
        send(KafkaTopicConfig.ORDER_CREATED, order.getId().toString(), event);
    }

    public void publishOrderCancelled(Order order) {
        OrderCancelledEvent event = OrderCancelledEvent.builder()
                .eventId(BaseEvent.newEventId())
                .eventType("ORDER_CANCELLED")
                .aggregateId(String.valueOf(order.getId()))
                .correlationId(BaseEvent.newCorrelationId())
                .occurredAt(Instant.now())
                .orderId(order.getId())
                .orderCode(order.getOrderCode())
                .userId(order.getUserId())
                .build();
        send(KafkaTopicConfig.ORDER_CANCELLED, order.getId().toString(), event);
    }

    public void publishOrderStatusChanged(Order order, String fromStatus) {
        OrderStatusChangedEvent event = OrderStatusChangedEvent.builder()
                .eventId(BaseEvent.newEventId())
                .eventType("ORDER_STATUS_CHANGED")
                .aggregateId(String.valueOf(order.getId()))
                .correlationId(BaseEvent.newCorrelationId())
                .occurredAt(Instant.now())
                .orderId(order.getId())
                .orderCode(order.getOrderCode())
                .fromStatus(fromStatus)
                .toStatus(order.getStatus().name())
                .build();
        send(KafkaTopicConfig.ORDER_STATUS_CHANGED, order.getId().toString(), event);
    }

    public void publishPaymentSucceeded(Payment payment) {
        PaymentSucceededEvent event = PaymentSucceededEvent.builder()
                .eventId(BaseEvent.newEventId())
                .eventType("PAYMENT_SUCCEEDED")
                .aggregateId(String.valueOf(payment.getOrderId()))
                .correlationId(BaseEvent.newCorrelationId())
                .occurredAt(Instant.now())
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .build();
        send(KafkaTopicConfig.PAYMENT_SUCCEEDED, payment.getOrderId().toString(), event);
    }

    public void publishPaymentFailed(Payment payment) {
        PaymentFailedEvent event = PaymentFailedEvent.builder()
                .eventId(BaseEvent.newEventId())
                .eventType("PAYMENT_FAILED")
                .aggregateId(String.valueOf(payment.getOrderId()))
                .correlationId(BaseEvent.newCorrelationId())
                .occurredAt(Instant.now())
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .build();
        send(KafkaTopicConfig.PAYMENT_FAILED, payment.getOrderId().toString(), event);
    }

    private void send(String topic, String key, Object event) {
        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event to {}: {}", topic, ex.getMessage());
                    } else {
                        log.info("Published event to {}: key={}", topic, key);
                    }
                });
    }
}
