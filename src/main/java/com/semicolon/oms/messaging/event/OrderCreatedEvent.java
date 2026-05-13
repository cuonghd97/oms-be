package com.semicolon.oms.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;

@Data @EqualsAndHashCode(callSuper = true)
@SuperBuilder @NoArgsConstructor @AllArgsConstructor
public class OrderCreatedEvent extends BaseEvent {
    private Long orderId;
    private String orderCode;
    private Long userId;
    private BigDecimal totalAmount;
    private int itemCount;
}
