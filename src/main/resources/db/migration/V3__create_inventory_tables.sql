-- V3: Inventory table
CREATE TABLE inventories (
    id                BIGSERIAL PRIMARY KEY,
    product_id        BIGINT NOT NULL UNIQUE REFERENCES products(id) ON DELETE CASCADE,
    quantity          INTEGER NOT NULL DEFAULT 0,
    reserved_quantity INTEGER NOT NULL DEFAULT 0,
    version           BIGINT NOT NULL DEFAULT 0,
    created_at        TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_quantity_non_negative CHECK (quantity >= 0),
    CONSTRAINT chk_reserved_non_negative CHECK (reserved_quantity >= 0)
);
