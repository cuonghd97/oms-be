package com.semicolon.oms.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.Instant;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseEvent {
    private String eventId;
    private String eventType;
    private String aggregateId;
    private String correlationId;
    private Instant occurredAt;

    public static String newEventId() { return UUID.randomUUID().toString(); }
    public static String newCorrelationId() { return UUID.randomUUID().toString(); }
}
