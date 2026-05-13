package com.semicolon.oms.messaging.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String ORDER_CREATED = "oms.order.created";
    public static final String ORDER_CANCELLED = "oms.order.cancelled";
    public static final String ORDER_STATUS_CHANGED = "oms.order.status-changed";
    public static final String PAYMENT_SUCCEEDED = "oms.payment.succeeded";
    public static final String PAYMENT_FAILED = "oms.payment.failed";

    @Bean public NewTopic orderCreatedTopic() { return TopicBuilder.name(ORDER_CREATED).partitions(3).replicas(1).build(); }
    @Bean public NewTopic orderCancelledTopic() { return TopicBuilder.name(ORDER_CANCELLED).partitions(3).replicas(1).build(); }
    @Bean public NewTopic orderStatusChangedTopic() { return TopicBuilder.name(ORDER_STATUS_CHANGED).partitions(3).replicas(1).build(); }
    @Bean public NewTopic paymentSucceededTopic() { return TopicBuilder.name(PAYMENT_SUCCEEDED).partitions(3).replicas(1).build(); }
    @Bean public NewTopic paymentFailedTopic() { return TopicBuilder.name(PAYMENT_FAILED).partitions(3).replicas(1).build(); }
}
