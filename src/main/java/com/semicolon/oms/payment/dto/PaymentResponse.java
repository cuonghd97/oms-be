package com.semicolon.oms.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private String status;
    private BigDecimal amount;
    private LocalDateTime paidAt;
    private LocalDateTime failedAt;
    private LocalDateTime createdAt;
}
