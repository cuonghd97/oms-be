-- V9: Seed data
-- Roles
INSERT INTO roles (name, description) VALUES ('ADMIN', 'Administrator');
INSERT INTO roles (name, description) VALUES ('STAFF', 'Staff member');
INSERT INTO roles (name, description) VALUES ('CUSTOMER', 'Customer');

-- Admin user (password: Admin@123)
INSERT INTO users (email, password, full_name, phone, status)
VALUES ('admin@semicolon.com', '$2a$10$jd.bnFt5pJFydQY9DzWSk.5TYacHtiHS8tIKq4X6/i.rTykSZ/fBu', 'Admin User', '0901000001', 'ACTIVE');

-- Staff user (password: Staff@123)
INSERT INTO users (email, password, full_name, phone, status)
VALUES ('staff@semicolon.com', '$2a$10$Mk4HLglw8QJdI7nlVqs/rOat8a0Ulw002Dz/HBZUWZVPIlWEsM9Ni', 'Staff User', '0901000002', 'ACTIVE');

-- Customer user (password: Customer@123)
INSERT INTO users (email, password, full_name, phone, status)
VALUES ('customer@semicolon.com', '$2a$10$LzTZuCvDGwfulpSOIDmJkeZPtx8nxfmJ2nAzz47cfHRWjAAgO82xC', 'Customer User', '0901000003', 'ACTIVE');

-- Assign roles
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1); -- admin -> ADMIN
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2); -- staff -> STAFF
INSERT INTO user_roles (user_id, role_id) VALUES (3, 3); -- customer -> CUSTOMER

-- Categories
INSERT INTO categories (name, slug, description, status) VALUES ('Electronics', 'electronics', 'Electronic devices and gadgets', 'ACTIVE');
INSERT INTO categories (name, slug, description, status) VALUES ('Clothing', 'clothing', 'Fashion and apparel', 'ACTIVE');
INSERT INTO categories (name, slug, description, status) VALUES ('Books', 'books', 'Books and publications', 'ACTIVE');
INSERT INTO categories (name, slug, description, status) VALUES ('Home & Garden', 'home-garden', 'Home and garden supplies', 'ACTIVE');

-- Products
INSERT INTO products (category_id, sku, name, slug, description, price, status)
VALUES (1, 'ELEC-001', 'Wireless Bluetooth Headphones', 'wireless-bluetooth-headphones', 'High quality wireless headphones with noise cancellation', 79.99, 'ACTIVE');

INSERT INTO products (category_id, sku, name, slug, description, price, status)
VALUES (1, 'ELEC-002', 'USB-C Charging Cable', 'usb-c-charging-cable', 'Fast charging USB-C cable 2m length', 12.99, 'ACTIVE');

INSERT INTO products (category_id, sku, name, slug, description, price, status)
VALUES (2, 'CLTH-001', 'Classic Cotton T-Shirt', 'classic-cotton-tshirt', 'Comfortable 100% cotton t-shirt', 24.99, 'ACTIVE');

INSERT INTO products (category_id, sku, name, slug, description, price, status)
VALUES (2, 'CLTH-002', 'Denim Jeans Slim Fit', 'denim-jeans-slim-fit', 'Premium slim fit denim jeans', 49.99, 'ACTIVE');

INSERT INTO products (category_id, sku, name, slug, description, price, status)
VALUES (3, 'BOOK-001', 'Clean Code', 'clean-code', 'A handbook of agile software craftsmanship by Robert C. Martin', 34.99, 'ACTIVE');

INSERT INTO products (category_id, sku, name, slug, description, price, status)
VALUES (4, 'HOME-001', 'LED Desk Lamp', 'led-desk-lamp', 'Adjustable LED desk lamp with USB charging port', 39.99, 'ACTIVE');

-- Inventories
INSERT INTO inventories (product_id, quantity, reserved_quantity) VALUES (1, 100, 0);
INSERT INTO inventories (product_id, quantity, reserved_quantity) VALUES (2, 500, 0);
INSERT INTO inventories (product_id, quantity, reserved_quantity) VALUES (3, 200, 0);
INSERT INTO inventories (product_id, quantity, reserved_quantity) VALUES (4, 150, 0);
INSERT INTO inventories (product_id, quantity, reserved_quantity) VALUES (5, 75, 0);
INSERT INTO inventories (product_id, quantity, reserved_quantity) VALUES (6, 60, 0);
