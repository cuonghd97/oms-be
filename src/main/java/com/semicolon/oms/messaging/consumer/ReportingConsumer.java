package com.semicolon.oms.messaging.consumer;

import com.semicolon.oms.messaging.config.KafkaTopicConfig;
import com.semicolon.oms.messaging.event.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReportingConsumer {

    @KafkaListener(topics = KafkaTopicConfig.ORDER_CREATED, groupId = "oms-reporting")
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("Reporting consumer: new order {} with amount {}", event.getOrderCode(), event.getTotalAmount());
    }

    @KafkaListener(topics = KafkaTopicConfig.PAYMENT_SUCCEEDED, groupId = "oms-reporting")
    public void onPaymentSucceeded(PaymentSucceededEvent event) {
        log.info("Reporting consumer: payment succeeded for order {} amount {}", event.getOrderId(), event.getAmount());
    }
}
