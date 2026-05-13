package com.semicolon.oms.messaging.consumer;

import com.semicolon.oms.audit.service.AuditLogService;
import com.semicolon.oms.messaging.config.KafkaTopicConfig;
import com.semicolon.oms.messaging.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditConsumer {

    private final AuditLogService auditLogService;

    @KafkaListener(topics = KafkaTopicConfig.ORDER_CREATED, groupId = "oms-audit")
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("Audit consumer: ORDER_CREATED {}", event.getOrderCode());
        auditLogService.log(event.getEventType(), event.getUserId(), "ORDER",
                event.getAggregateId(), "Order created: " + event.getOrderCode(),
                Map.of("totalAmount", event.getTotalAmount(), "itemCount", event.getItemCount()));
    }

    @KafkaListener(topics = KafkaTopicConfig.ORDER_CANCELLED, groupId = "oms-audit")
    public void onOrderCancelled(OrderCancelledEvent event) {
        log.info("Audit consumer: ORDER_CANCELLED {}", event.getOrderCode());
        auditLogService.log(event.getEventType(), event.getUserId(), "ORDER",
                event.getAggregateId(), "Order cancelled: " + event.getOrderCode(), Map.of());
    }

    @KafkaListener(topics = KafkaTopicConfig.ORDER_STATUS_CHANGED, groupId = "oms-audit")
    public void onOrderStatusChanged(OrderStatusChangedEvent event) {
        log.info("Audit consumer: ORDER_STATUS_CHANGED {} -> {}", event.getFromStatus(), event.getToStatus());
        auditLogService.log(event.getEventType(), null, "ORDER",
                event.getAggregateId(), "Status changed: " + event.getFromStatus() + " -> " + event.getToStatus(),
                Map.of("fromStatus", event.getFromStatus(), "toStatus", event.getToStatus()));
    }

    @KafkaListener(topics = KafkaTopicConfig.PAYMENT_SUCCEEDED, groupId = "oms-audit")
    public void onPaymentSucceeded(PaymentSucceededEvent event) {
        auditLogService.log(event.getEventType(), null, "PAYMENT",
                event.getAggregateId(), "Payment succeeded for order " + event.getOrderId(),
                Map.of("amount", event.getAmount()));
    }

    @KafkaListener(topics = KafkaTopicConfig.PAYMENT_FAILED, groupId = "oms-audit")
    public void onPaymentFailed(PaymentFailedEvent event) {
        auditLogService.log(event.getEventType(), null, "PAYMENT",
                event.getAggregateId(), "Payment failed for order " + event.getOrderId(),
                Map.of("amount", event.getAmount()));
    }
}
