-- V7: Audit log table
CREATE TABLE audit_logs (
    id             BIGSERIAL PRIMARY KEY,
    event_type     VARCHAR(100) NOT NULL,
    actor_id       BIGINT,
    aggregate_type VARCHAR(100),
    aggregate_id   VARCHAR(100),
    action         VARCHAR(255) NOT NULL,
    metadata       JSONB,
    created_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_logs_event_type ON audit_logs(event_type);
CREATE INDEX idx_audit_logs_actor_id ON audit_logs(actor_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);
