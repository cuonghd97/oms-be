package com.semicolon.oms.report.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderStatusSummaryResponse {
    private String status;
    private long count;
}
