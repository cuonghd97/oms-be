package com.semicolon.oms.report.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RevenueReportResponse {
    private BigDecimal totalRevenue;
    private long totalOrders;
    private long completedOrders;
    private long cancelledOrders;
}
