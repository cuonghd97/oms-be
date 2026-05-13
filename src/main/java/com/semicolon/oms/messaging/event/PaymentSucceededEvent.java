package com.semicolon.oms.messaging.event;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;

@Data @EqualsAndHashCode(callSuper = true)
@SuperBuilder @NoArgsConstructor @AllArgsConstructor
public class PaymentSucceededEvent extends BaseEvent {
    private Long paymentId;
    private Long orderId;
    private BigDecimal amount;
}
