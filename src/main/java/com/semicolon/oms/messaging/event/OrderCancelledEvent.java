package com.semicolon.oms.messaging.event;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data @EqualsAndHashCode(callSuper = true)
@SuperBuilder @NoArgsConstructor @AllArgsConstructor
public class OrderCancelledEvent extends BaseEvent {
    private Long orderId;
    private String orderCode;
    private Long userId;
}
