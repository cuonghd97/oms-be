-- V5: Payment table
CREATE TABLE payments (
    id         BIGSERIAL PRIMARY KEY,
    order_id   BIGINT NOT NULL UNIQUE REFERENCES orders(id) ON DELETE CASCADE,
    status     VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
    amount     DECIMAL(15,2) NOT NULL,
    paid_at    TIMESTAMP,
    failed_at  TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
