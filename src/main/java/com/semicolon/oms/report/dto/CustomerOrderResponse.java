package com.semicolon.oms.report.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CustomerOrderResponse {
    private Long userId;
    private String fullName;
    private String email;
    private long totalOrders;
    private BigDecimal totalSpent;
}
