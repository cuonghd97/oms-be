-- V4: Order tables
CREATE TABLE orders (
    id           BIGSERIAL PRIMARY KEY,
    order_code   VARCHAR(50) NOT NULL UNIQUE,
    user_id      BIGINT NOT NULL REFERENCES users(id),
    status       VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    total_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    cancelled_at TIMESTAMP
);

CREATE TABLE order_items (
    id           BIGSERIAL PRIMARY KEY,
    order_id     BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id   BIGINT NOT NULL REFERENCES products(id),
    product_name VARCHAR(255) NOT NULL,
    product_sku  VARCHAR(100) NOT NULL,
    unit_price   DECIMAL(15,2) NOT NULL,
    quantity     INTEGER NOT NULL,
    total_price  DECIMAL(15,2) NOT NULL
);
