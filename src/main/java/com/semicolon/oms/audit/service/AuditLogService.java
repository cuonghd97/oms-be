package com.semicolon.oms.audit.service;

import com.semicolon.oms.audit.dto.AuditLogResponse;
import com.semicolon.oms.audit.entity.AuditLog;
import com.semicolon.oms.audit.repository.AuditLogRepository;
import com.semicolon.oms.common.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void log(String eventType, Long actorId, String aggregateType, String aggregateId,
                    String action, Map<String, Object> metadata) {
        AuditLog auditLog = AuditLog.builder()
                .eventType(eventType).actorId(actorId).aggregateType(aggregateType)
                .aggregateId(aggregateId).action(action).metadata(metadata)
                .build();
        auditLogRepository.save(auditLog);
    }

    @Transactional(readOnly = true)
    public PagedResponse<AuditLogResponse> getAuditLogs(Pageable pageable) {
        Page<AuditLog> page = auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
        return PagedResponse.<AuditLogResponse>builder()
                .items(page.getContent().stream().map(this::toResponse).collect(Collectors.toList()))
                .page(page.getNumber()).size(page.getSize())
                .totalElements(page.getTotalElements()).totalPages(page.getTotalPages())
                .build();
    }

    private AuditLogResponse toResponse(AuditLog a) {
        return AuditLogResponse.builder()
                .id(a.getId()).eventType(a.getEventType()).actorId(a.getActorId())
                .aggregateType(a.getAggregateType()).aggregateId(a.getAggregateId())
                .action(a.getAction()).metadata(a.getMetadata()).createdAt(a.getCreatedAt())
                .build();
    }
}
