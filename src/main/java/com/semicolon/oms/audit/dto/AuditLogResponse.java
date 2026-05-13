package com.semicolon.oms.audit.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuditLogResponse {
    private Long id;
    private String eventType;
    private Long actorId;
    private String aggregateType;
    private String aggregateId;
    private String action;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
}
