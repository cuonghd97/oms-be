package com.semicolon.oms.messaging.consumer;

import com.semicolon.oms.messaging.config.KafkaTopicConfig;
import com.semicolon.oms.messaging.event.*;
import com.semicolon.oms.notification.entity.NotificationType;
import com.semicolon.oms.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = KafkaTopicConfig.ORDER_CREATED, groupId = "oms-notification")
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("Notification consumer: ORDER_CREATED for order {}", event.getOrderCode());
        notificationService.createNotification(event.getUserId(), NotificationType.ORDER_CREATED,
                "Order Created", "Your order " + event.getOrderCode() + " has been placed successfully.");
    }

    @KafkaListener(topics = KafkaTopicConfig.ORDER_CANCELLED, groupId = "oms-notification")
    public void onOrderCancelled(OrderCancelledEvent event) {
        log.info("Notification consumer: ORDER_CANCELLED for order {}", event.getOrderCode());
        notificationService.createNotification(event.getUserId(), NotificationType.ORDER_CANCELLED,
                "Order Cancelled", "Your order " + event.getOrderCode() + " has been cancelled.");
    }

    @KafkaListener(topics = KafkaTopicConfig.PAYMENT_SUCCEEDED, groupId = "oms-notification")
    public void onPaymentSucceeded(PaymentSucceededEvent event) {
        log.info("Notification consumer: PAYMENT_SUCCEEDED for order {}", event.getOrderId());
        // userId is not in payment event, log only
    }

    @KafkaListener(topics = KafkaTopicConfig.PAYMENT_FAILED, groupId = "oms-notification")
    public void onPaymentFailed(PaymentFailedEvent event) {
        log.info("Notification consumer: PAYMENT_FAILED for order {}", event.getOrderId());
    }
}
