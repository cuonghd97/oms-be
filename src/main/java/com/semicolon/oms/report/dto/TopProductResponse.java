package com.semicolon.oms.report.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TopProductResponse {
    private Long productId;
    private String productName;
    private long totalQuantitySold;
    private BigDecimal totalRevenue;
}
