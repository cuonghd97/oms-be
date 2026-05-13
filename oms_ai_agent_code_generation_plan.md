# AI Agent Code Generation Plan
# Project: OMS Spring Boot Backend

## 1. Project Goal

Build a personal backend project named **OMS - Order Management System** to demonstrate Java backend skills matching this JD:

- Java backend development
- OOP, data structures, algorithms
- REST API design
- Spring Boot, Spring Core, Spring MVC, Spring Data JPA, Spring Security
- ORM with JPA/Hibernate
- PostgreSQL/MySQL database design
- Complex SQL queries and optimization
- Maven
- Docker
- Jenkins
- Unit testing and integration testing with JUnit and Mockito
- Git workflow
- Redis caching
- Kafka message queue
- Optional Kubernetes deployment

The project must be production-like, clean, testable, and easy to run locally with Docker Compose.

---

## 2. Tech Stack

Use the following stack:

- Java 17
- Spring Boot 3.x
- Spring Web / Spring MVC
- Spring Data JPA
- Hibernate
- Spring Security
- JWT authentication
- PostgreSQL
- Redis
- Kafka
- Maven
- Docker
- Docker Compose
- Jenkins
- JUnit 5
- Mockito
- Testcontainers
- OpenAPI / Swagger
- MapStruct
- Lombok
- Flyway
- Kubernetes manifests as optional deployment files

---

## 3. Main Business Domain

The system is an **Order Management System** for an e-commerce backend.

Main flows:

1. Admin manages products, categories, and inventory.
2. Customer registers and logs in.
3. Customer views product list and product detail.
4. Customer creates an order.
5. System validates stock.
6. System reserves or deducts inventory.
7. System publishes order events to Kafka.
8. Kafka consumers process notification, audit log, and reporting.
9. Admin can search orders and view reports.
10. Redis caches frequently accessed product data.

---

## 4. Feature List

### 4.1 Authentication & Authorization

Implement:

- Register customer
- Login
- JWT access token
- Refresh token
- Logout
- Role-based access control

Roles:

- `ADMIN`
- `STAFF`
- `CUSTOMER`

APIs:

```http
POST /api/v1/auth/register
POST /api/v1/auth/login
POST /api/v1/auth/refresh-token
POST /api/v1/auth/logout
GET  /api/v1/auth/me
```

Requirements:

- Password must be hashed using BCrypt.
- Use Spring Security filter chain.
- Use JWT for stateless authentication.
- Admin APIs require `ADMIN`.
- Staff APIs require `ADMIN` or `STAFF`.
- Customer APIs require authenticated user.

---

### 4.2 User Management

Implement:

- Get current user profile
- Admin lists users
- Admin updates user status
- Admin assigns role

APIs:

```http
GET /api/v1/users/me
GET /api/v1/admin/users
GET /api/v1/admin/users/{id}
PATCH /api/v1/admin/users/{id}/status
PATCH /api/v1/admin/users/{id}/roles
```

User statuses:

- `ACTIVE`
- `LOCKED`
- `DELETED`

---

### 4.3 Category Management

Implement:

- Create category
- Update category
- Delete category
- Get category tree/list
- Search categories

APIs:

```http
GET    /api/v1/categories
GET    /api/v1/categories/{id}
POST   /api/v1/admin/categories
PUT    /api/v1/admin/categories/{id}
DELETE /api/v1/admin/categories/{id}
```

---

### 4.4 Product Management

Implement:

- Create product
- Update product
- Delete product
- Get product detail
- Search product
- Filter product
- Pagination
- Sorting
- Cache product detail with Redis

APIs:

```http
GET    /api/v1/products
GET    /api/v1/products/{id}
POST   /api/v1/admin/products
PUT    /api/v1/admin/products/{id}
DELETE /api/v1/admin/products/{id}
```

Product filters:

```text
keyword
categoryId
minPrice
maxPrice
status
page
size
sort
```

Product status:

- `ACTIVE`
- `INACTIVE`
- `DELETED`

Redis cache:

- Cache key: `product:{productId}`
- Cache category list: `categories:all`
- Evict cache when product/category is updated or deleted.

---

### 4.5 Inventory Management

Implement:

- View product inventory
- Admin increases stock
- Admin decreases stock
- Admin adjusts stock
- Prevent overselling
- Use optimistic locking

APIs:

```http
GET   /api/v1/admin/inventories
GET   /api/v1/admin/inventories/{productId}
POST  /api/v1/admin/inventories/{productId}/increase
POST  /api/v1/admin/inventories/{productId}/decrease
POST  /api/v1/admin/inventories/{productId}/adjust
```

Rules:

- Inventory cannot be negative.
- Order creation must fail if stock is insufficient.
- Use `@Version` for optimistic locking.
- Use `@Transactional` on inventory update and order creation.

---

### 4.6 Order Management

Implement:

- Customer creates order
- Customer views own order history
- Customer views order detail
- Customer cancels pending order
- Admin searches all orders
- Admin updates order status

APIs:

```http
POST  /api/v1/orders
GET   /api/v1/orders
GET   /api/v1/orders/{id}
POST  /api/v1/orders/{id}/cancel

GET   /api/v1/admin/orders
GET   /api/v1/admin/orders/{id}
PATCH /api/v1/admin/orders/{id}/status
```

Order statuses:

```text
PENDING
CONFIRMED
SHIPPING
COMPLETED
CANCELLED
```

Order flow:

```text
PENDING -> CONFIRMED -> SHIPPING -> COMPLETED
PENDING -> CANCELLED
CONFIRMED -> CANCELLED
```

Rules:

- Customer can only see their own orders.
- Customer can only cancel `PENDING` orders.
- Admin can update order status.
- Creating order must deduct/reserve inventory.
- Cancelling order must restore inventory if stock was deducted.
- Publish Kafka event after order is created.
- Publish Kafka event after order is cancelled.

Kafka events:

- `OrderCreatedEvent`
- `OrderCancelledEvent`
- `OrderStatusChangedEvent`

---

### 4.7 Payment Simulation

Implement mock payment flow.

APIs:

```http
POST /api/v1/orders/{orderId}/payments/pay
POST /api/v1/orders/{orderId}/payments/fail
GET  /api/v1/orders/{orderId}/payments
```

Payment statuses:

```text
UNPAID
PAID
FAILED
REFUNDED
```

Rules:

- New order starts with `UNPAID`.
- Successful payment changes status to `PAID`.
- Failed payment changes status to `FAILED`.
- Publish Kafka event after payment success/failure.

Kafka events:

- `PaymentSucceededEvent`
- `PaymentFailedEvent`

---

### 4.8 Kafka Event Processing

Use Kafka as the message queue.

Topics:

```text
oms.order.created
oms.order.cancelled
oms.order.status-changed
oms.payment.succeeded
oms.payment.failed
oms.notification.requested
oms.audit.logged
```

Producer responsibilities:

- Publish event after order creation.
- Publish event after order cancellation.
- Publish event after payment success/failure.
- Publish audit events for important actions.

Consumer responsibilities:

1. Notification Consumer
   - Listen to order/payment events.
   - Create mock notification records.
   - Log notification result.

2. Audit Consumer
   - Listen to business events.
   - Save audit logs into database.

3. Reporting Consumer
   - Listen to order/payment events.
   - Update reporting tables if needed.

Requirements:

- Use JSON serialization.
- Add event ID.
- Add event timestamp.
- Add event type.
- Add aggregate ID.
- Add correlation ID.
- Add retry handling.
- Add dead-letter topic if possible.

Example event structure:

```json
{
  "eventId": "uuid",
  "eventType": "ORDER_CREATED",
  "aggregateId": "orderId",
  "correlationId": "uuid",
  "occurredAt": "2026-05-13T10:00:00Z",
  "payload": {}
}
```

---

### 4.9 Notification Module

Implement mock notification system.

Features:

- Create notification record from Kafka consumer
- List notifications for user
- Mark notification as read

APIs:

```http
GET   /api/v1/notifications
PATCH /api/v1/notifications/{id}/read
```

Notification types:

```text
ORDER_CREATED
ORDER_CANCELLED
PAYMENT_SUCCEEDED
PAYMENT_FAILED
```

---

### 4.10 Audit Log Module

Implement audit logging.

Features:

- Save audit logs from Kafka consumer
- Admin searches audit logs

APIs:

```http
GET /api/v1/admin/audit-logs
```

Fields:

```text
id
eventType
actorId
aggregateType
aggregateId
action
metadata
createdAt
```

---

### 4.11 Reporting Module

Implement reporting APIs to demonstrate complex SQL.

APIs:

```http
GET /api/v1/admin/reports/revenue?from=2026-01-01&to=2026-01-31
GET /api/v1/admin/reports/top-products?from=2026-01-01&to=2026-01-31
GET /api/v1/admin/reports/customer-orders?from=2026-01-01&to=2026-01-31
GET /api/v1/admin/reports/order-status-summary
```

Requirements:

- Use JPQL or native SQL.
- Use joins.
- Use group by.
- Use aggregation.
- Use pagination where needed.
- Add indexes for reporting columns.

Indexes:

```sql
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);
CREATE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_payments_status ON payments(status);
```

---

### 4.12 Cache Management

Implement Redis cache.

Features:

- Cache product detail.
- Cache category list.
- Evict product cache on product update/delete.
- Evict category cache on category update/delete.

Admin APIs:

```http
DELETE /api/v1/admin/cache/products
DELETE /api/v1/admin/cache/categories
DELETE /api/v1/admin/cache/all
```

---

### 4.13 Error Handling

Implement global exception handling.

Use:

- `@RestControllerAdvice`
- Custom exception classes
- Standard error response

Standard error response:

```json
{
  "timestamp": "2026-05-13T10:00:00Z",
  "status": 400,
  "error": "BAD_REQUEST",
  "message": "Insufficient inventory",
  "path": "/api/v1/orders",
  "traceId": "uuid"
}
```

Common exceptions:

- `ResourceNotFoundException`
- `BadRequestException`
- `UnauthorizedException`
- `ForbiddenException`
- `InsufficientInventoryException`
- `InvalidOrderStatusException`
- `DuplicateResourceException`

---

### 4.14 Validation

Use Bean Validation.

Examples:

- Email must be valid.
- Password must have minimum length.
- Product price must be positive.
- Quantity must be positive.
- Order items must not be empty.

---

### 4.15 API Documentation

Use Swagger/OpenAPI.

Path:

```http
/api-docs
/swagger-ui.html
```

Document:

- Auth APIs
- Product APIs
- Order APIs
- Admin APIs
- Error response format

---

## FRONTEND WEB APPLICATION - REQUIRED

Build a simple frontend app named:

```text
oms-web
```

This frontend is required. It must consume the backend REST APIs and provide a simple UI for both customer and admin users.

### Frontend Tech Stack

Use:

- React 18+
- TypeScript
- Vite
- React Router
- TanStack Query
- Axios
- Tailwind CSS
- shadcn/ui or simple reusable components
- React Hook Form
- Zod
- Zustand or React Context for auth state

Optional:

- Recharts for dashboard charts
- Vitest + React Testing Library

### Frontend Main Features

Customer features:

- Register
- Login
- Logout
- View product list
- Search/filter/sort products
- View product detail
- Add product to cart
- Update cart quantity
- Remove item from cart
- Create order
- View order history
- View order detail
- Cancel pending order
- Mock payment
- View notifications
- Mark notification as read

Admin features:

- Login as admin
- View dashboard
- Manage products
- Manage categories
- Manage inventory
- Manage orders
- Update order status
- View revenue report
- View top products report
- View customer order report
- View audit logs
- Clear product/category cache

### Frontend Pages

Create these routes:

```text
/login
/register
/products
/products/:id
/cart
/orders
/orders/:id
/notifications

/admin/dashboard
/admin/products
/admin/products/new
/admin/products/:id/edit
/admin/categories
/admin/inventories
/admin/orders
/admin/orders/:id
/admin/reports
/admin/audit-logs
/admin/cache
```

### Public Pages

#### Login Page

Features:

- Email input
- Password input
- Login button
- Show validation errors
- Store JWT access token
- Redirect after login:
  - `ADMIN` or `STAFF` -> `/admin/dashboard`
  - `CUSTOMER` -> `/products`

Use API:

```http
POST /api/v1/auth/login
```

#### Register Page

Features:

- Full name input
- Email input
- Phone input
- Password input
- Confirm password input
- Register button
- Redirect to login after success

Use API:

```http
POST /api/v1/auth/register
```

### Customer Pages

#### Product List Page

Features:

- Product grid/table
- Keyword search
- Category filter
- Price filter
- Status filter
- Pagination
- Sorting
- Add to cart button

Use APIs:

```http
GET /api/v1/products
GET /api/v1/categories
```

#### Product Detail Page

Features:

- Product name
- Product SKU
- Product category
- Product price
- Product description
- Stock status
- Quantity selector
- Add to cart button

Use API:

```http
GET /api/v1/products/{id}
```

#### Cart Page

Features:

- List cart items
- Update quantity
- Remove item
- Clear cart
- Show total amount
- Create order button

Cart can be stored in local storage.

Use API:

```http
POST /api/v1/orders
```

#### Order History Page

Features:

- List current user's orders
- Filter by status
- Pagination
- Open order detail

Use API:

```http
GET /api/v1/orders
```

#### Order Detail Page

Features:

- Show order code
- Show order status
- Show order items
- Show total amount
- Show payment status
- Cancel pending order
- Mock payment success
- Mock payment failure

Use APIs:

```http
GET  /api/v1/orders/{id}
POST /api/v1/orders/{id}/cancel
POST /api/v1/orders/{orderId}/payments/pay
POST /api/v1/orders/{orderId}/payments/fail
```

#### Notification Page

Features:

- List notifications
- Show read/unread status
- Mark notification as read

Use APIs:

```http
GET   /api/v1/notifications
PATCH /api/v1/notifications/{id}/read
```

### Admin Pages

#### Admin Dashboard Page

Features:

- Revenue summary card
- Total orders card
- Total customers card
- Order status summary
- Top selling products
- Recent orders

Use APIs:

```http
GET /api/v1/admin/reports/revenue
GET /api/v1/admin/reports/top-products
GET /api/v1/admin/reports/order-status-summary
GET /api/v1/admin/orders
```

#### Admin Product Management Page

Features:

- Product table
- Search products
- Filter products
- Pagination
- Create product
- Edit product
- Delete product

Use APIs:

```http
GET    /api/v1/products
POST   /api/v1/admin/products
PUT    /api/v1/admin/products/{id}
DELETE /api/v1/admin/products/{id}
```

#### Admin Category Management Page

Features:

- Category table
- Create category
- Edit category
- Delete category

Use APIs:

```http
GET    /api/v1/categories
POST   /api/v1/admin/categories
PUT    /api/v1/admin/categories/{id}
DELETE /api/v1/admin/categories/{id}
```

#### Admin Inventory Page

Features:

- Inventory table
- Search by product name/SKU
- Increase stock
- Decrease stock
- Adjust stock

Use APIs:

```http
GET  /api/v1/admin/inventories
POST /api/v1/admin/inventories/{productId}/increase
POST /api/v1/admin/inventories/{productId}/decrease
POST /api/v1/admin/inventories/{productId}/adjust
```

#### Admin Order Management Page

Features:

- Order table
- Search by order code
- Filter by status
- View order detail
- Update order status

Use APIs:

```http
GET   /api/v1/admin/orders
GET   /api/v1/admin/orders/{id}
PATCH /api/v1/admin/orders/{id}/status
```

#### Admin Reports Page

Features:

- Revenue report
- Top products report
- Customer orders report
- Order status summary chart

Use APIs:

```http
GET /api/v1/admin/reports/revenue
GET /api/v1/admin/reports/top-products
GET /api/v1/admin/reports/customer-orders
GET /api/v1/admin/reports/order-status-summary
```

#### Admin Audit Logs Page

Features:

- Audit log table
- Filter by event type
- Filter by aggregate ID
- Filter by actor ID

Use API:

```http
GET /api/v1/admin/audit-logs
```

#### Admin Cache Management Page

Features:

- Clear product cache
- Clear category cache
- Clear all cache

Use APIs:

```http
DELETE /api/v1/admin/cache/products
DELETE /api/v1/admin/cache/categories
DELETE /api/v1/admin/cache/all
```

### Frontend Auth Requirements

Implement:

- Store token after login
- Persist token in local storage
- Axios interceptor adds `Authorization: Bearer <token>`
- Auto logout on `401 Unauthorized`
- Protected routes
- Role-based route guard
- Hide admin menu from customer
- Hide customer pages from unauthenticated user

Suggested files:

```text
src/features/auth/authStore.ts
src/features/auth/authApi.ts
src/features/auth/ProtectedRoute.tsx
src/features/auth/RoleGuard.tsx
```

### Frontend Folder Structure

Use this folder structure:

```text
oms-web/
  src/
    app/
      router.tsx
      providers.tsx
    components/
      ui/
      layout/
      common/
    features/
      auth/
      products/
      cart/
      orders/
      notifications/
      admin/
        dashboard/
        products/
        categories/
        inventories/
        orders/
        reports/
        auditLogs/
        cache/
    lib/
      apiClient.ts
      queryClient.ts
      constants.ts
      utils.ts
    types/
      api.ts
      auth.ts
      product.ts
      order.ts
      user.ts
    main.tsx
    index.css
  package.json
  vite.config.ts
  tailwind.config.js
  Dockerfile
  nginx.conf
```

### Frontend Layout

Customer layout menu:

```text
Products
Cart
Orders
Notifications
Logout
```

Admin layout menu:

```text
Dashboard
Products
Categories
Inventory
Orders
Reports
Audit Logs
Cache
Logout
```

### Frontend API Client

Create:

```text
src/lib/apiClient.ts
```

Requirements:

- Base URL from environment variable:

```text
VITE_API_BASE_URL=http://localhost:8080
```

- Add JWT token automatically.
- Handle 401 globally.
- Parse backend standard response.
- Show error toast/message when API fails.

### Frontend Docker

Create frontend `Dockerfile`.

Use:

- Node image for build
- Nginx image for serving static files

Expose frontend on:

```text
3000
```

Update root `docker-compose.yml` with:

```text
frontend
app
postgres
redis
kafka
zookeeper
```

Frontend URL:

```text
http://localhost:3000
```

Backend URL:

```text
http://localhost:8080
```

### Frontend Definition of Done

Frontend is complete when:

- User can register.
- User can login.
- Token is saved and attached to API requests.
- Customer can view products.
- Customer can add products to cart.
- Customer can create order.
- Customer can view order history.
- Customer can view order detail.
- Customer can cancel pending order.
- Customer can mock payment.
- Customer can view notifications.
- Admin can manage products.
- Admin can manage categories.
- Admin can manage inventory.
- Admin can manage orders.
- Admin can view reports.
- Admin can view audit logs.
- Admin can clear cache.
- Frontend runs with Docker Compose.


---

## 5. Database Design

Create entities:

### User

Fields:

```text
id
email
password
fullName
phone
status
createdAt
updatedAt
```

Relations:

- Many-to-many with Role
- One-to-many with Order
- One-to-many with Notification

### Role

Fields:

```text
id
name
description
```

### Category

Fields:

```text
id
name
slug
description
status
createdAt
updatedAt
```

Relations:

- One-to-many with Product

### Product

Fields:

```text
id
categoryId
sku
name
slug
description
price
status
createdAt
updatedAt
```

Relations:

- Many-to-one with Category
- One-to-one with Inventory
- One-to-many with OrderItem

### Inventory

Fields:

```text
id
productId
quantity
reservedQuantity
version
createdAt
updatedAt
```

Relations:

- One-to-one with Product

### Order

Fields:

```text
id
orderCode
userId
status
totalAmount
createdAt
updatedAt
cancelledAt
```

Relations:

- Many-to-one with User
- One-to-many with OrderItem
- One-to-one with Payment

### OrderItem

Fields:

```text
id
orderId
productId
productName
productSku
unitPrice
quantity
totalPrice
```

Relations:

- Many-to-one with Order
- Many-to-one with Product

### Payment

Fields:

```text
id
orderId
status
amount
paidAt
failedAt
createdAt
updatedAt
```

Relations:

- One-to-one with Order

### Notification

Fields:

```text
id
userId
type
title
message
read
createdAt
readAt
```

Relations:

- Many-to-one with User

### AuditLog

Fields:

```text
id
eventType
actorId
aggregateType
aggregateId
action
metadata
createdAt
```

---

## 6. Package Structure

Use this package structure:

```text
com.example.oms
  common
    config
    exception
    response
    security
    util
  auth
    controller
    service
    dto
  user
    controller
    service
    repository
    entity
    dto
  catalog
    controller
    service
    repository
    entity
    dto
    mapper
  inventory
    controller
    service
    repository
    entity
    dto
  order
    controller
    service
    repository
    entity
    dto
    mapper
  payment
    controller
    service
    repository
    entity
    dto
  notification
    controller
    service
    repository
    entity
    dto
  audit
    controller
    service
    repository
    entity
    dto
  report
    controller
    service
    repository
    dto
  messaging
    event
    producer
    consumer
    config
```

---

## 7. Coding Standards

Follow these rules:

- Use clean architecture style by feature package.
- Controllers must not contain business logic.
- Services contain business logic.
- Repositories only access database.
- DTOs must be used for request/response.
- Do not expose entity directly in API response.
- Use MapStruct for mapping.
- Use constructor injection.
- Use `@Transactional` for write operations.
- Use pagination for list APIs.
- Use meaningful exception classes.
- Use standard API response wrapper.
- Use consistent naming.
- Avoid circular dependencies.
- Keep methods small and readable.

---

## 8. API Response Format

Successful response:

```json
{
  "success": true,
  "message": "Success",
  "data": {}
}
```

Paged response:

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "items": [],
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5
  }
}
```

---

## 9. Testing Plan

### 9.1 Unit Tests

Use JUnit 5 and Mockito.

Required tests:

```text
AuthServiceTest
ProductServiceTest
InventoryServiceTest
OrderServiceTest
PaymentServiceTest
NotificationServiceTest
```

Important test cases:

- Register user successfully.
- Login successfully.
- Login with invalid password.
- Create product successfully.
- Update product and evict cache.
- Create order successfully.
- Create order with insufficient stock.
- Cancel order and restore inventory.
- Pay order successfully.
- Payment failure event published.
- Invalid order status transition throws exception.

---

### 9.2 Integration Tests

Use:

- Spring Boot Test
- Testcontainers
- PostgreSQL container
- Redis container
- Kafka container

Required integration tests:

```text
AuthApiIntegrationTest
ProductApiIntegrationTest
OrderApiIntegrationTest
PaymentApiIntegrationTest
KafkaEventIntegrationTest
```

Important test cases:

- Login API returns JWT.
- Product API supports pagination and filtering.
- Order API deducts inventory.
- Kafka event is published after order creation.
- Kafka consumer saves notification.
- Redis cache works for product detail.

---

## 10. Docker Compose

Create `docker-compose.yml` with:

- PostgreSQL
- Redis
- Kafka
- Zookeeper or KRaft Kafka
- Application

Example services:

```text
postgres
redis
kafka
zookeeper
app
frontend
```

Expose ports:

```text
frontend: 3000
app: 8080
postgres: 5432
redis: 6379
kafka: 9092
```

---

## 11. Maven Requirements

Use Maven.

Required dependencies:

- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-security
- spring-boot-starter-validation
- spring-boot-starter-data-redis
- spring-kafka
- postgresql
- flyway-core
- jjwt
- lombok
- mapstruct
- springdoc-openapi
- junit-jupiter
- mockito
- testcontainers
- testcontainers-postgresql
- testcontainers-kafka

---

## 12. Flyway Migration

Create migration files:

```text
db/migration/V1__create_users_and_roles.sql
db/migration/V2__create_catalog_tables.sql
db/migration/V3__create_inventory_tables.sql
db/migration/V4__create_order_tables.sql
db/migration/V5__create_payment_tables.sql
db/migration/V6__create_notification_tables.sql
db/migration/V7__create_audit_log_tables.sql
db/migration/V8__create_indexes.sql
db/migration/V9__insert_seed_data.sql
```

Seed data:

- Admin user
- Staff user
- Customer user
- Default roles
- Sample categories
- Sample products
- Sample inventory

---

## 13. Jenkins Pipeline

Create `Jenkinsfile`.

Pipeline stages:

```text
Checkout
Build
Run unit tests
Run integration tests
Package jar
Build Docker image
Push Docker image
Deploy
```

For local demo, deployment can be simulated.

---

## 14. Kubernetes Optional

Create folder:

```text
k8s/
  app-deployment.yaml
  app-service.yaml
  postgres-deployment.yaml
  postgres-service.yaml
  redis-deployment.yaml
  redis-service.yaml
  kafka-deployment.yaml
  kafka-service.yaml
  configmap.yaml
  secret.yaml
```

Requirements:

- App deployment has environment variables.
- App service exposes port 8080.
- ConfigMap stores non-sensitive config.
- Secret stores DB password and JWT secret.

---

## 15. README Requirements

Create a detailed `README.md` with:

```text
Project overview
Tech stack
Architecture diagram
Feature list
Database design
Kafka topics
Redis cache strategy
API documentation
How to run locally
How to run tests
Docker Compose setup
Jenkins pipeline
Kubernetes deployment
Screenshots or sample API responses
Future improvements
```

---

## 16. Git Commit Plan

Use meaningful commits:

```text
init spring boot project
add database migration
add auth module
add user management module
add catalog module
add inventory module
add order module
add payment module
add kafka messaging
add redis caching
add reporting APIs
add unit tests
add integration tests
add docker compose
add jenkins pipeline
add kubernetes manifests
add readme
```

---

## 17. Definition of Done

The project is considered complete when:

- Application starts successfully.
- Docker Compose starts PostgreSQL, Redis, Kafka, backend app, and frontend app.
- Swagger is available.
- Auth APIs work.
- Product APIs work.
- Inventory APIs work.
- Order APIs work.
- Payment APIs work.
- Kafka producer and consumer work.
- Redis cache works.
- Reports return correct data.
- Unit tests pass.
- Integration tests pass.
- Jenkinsfile exists.
- Kubernetes manifests exist.
- README explains the project clearly.
- GitHub repository looks professional.

---

## 18. Suggested Implementation Order for AI Agent

Implement in this exact order:

1. Generate Spring Boot project structure.
2. Add Maven dependencies.
3. Add application configuration.
4. Add Docker Compose with PostgreSQL, Redis, Kafka.
5. Add Flyway migration scripts.
6. Add common response and exception handling.
7. Add security module with JWT.
8. Add user and role module.
9. Add catalog module.
10. Add inventory module.
11. Add order module.
12. Add payment module.
13. Add Kafka event model, producer, and consumers.
14. Add notification module.
15. Add audit log module.
16. Add report module.
17. Add Redis caching.
18. Add frontend React TypeScript app.
19. Add frontend auth, routing, customer pages, and admin pages.
20. Add frontend Dockerfile and docker-compose integration.
21. Add tests.
22. Add Jenkinsfile.
23. Add Kubernetes manifests.
24. Add README.

---

## 19. Important Non-Functional Requirements

- All APIs must return consistent response format.
- All write operations must be transactional.
- Use validation on request DTOs.
- Use pagination for list APIs.
- Use indexes for frequently queried columns.
- Do not expose password or sensitive data.
- Do not expose JPA entities directly.
- Use DTOs and mappers.
- Use environment variables for secrets.
- Code must be readable and easy to explain in interview.
- Keep business logic realistic but not overly complicated.

---

## 20. Interview Talking Points

This project should help explain:

- How Spring Security JWT works.
- How JPA relationships are designed.
- How transactions prevent inconsistent order/inventory data.
- How optimistic locking prevents overselling.
- How Redis improves read performance.
- How Kafka decouples order processing from notification/audit/reporting.
- How SQL indexes improve reporting queries.
- How unit tests and integration tests are structured.
- How Docker Compose helps local development.
- How Jenkins automates build/test/deploy.
- How Kubernetes can deploy the application.
