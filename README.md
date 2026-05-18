# OMS - Order Management System

A production-ready **Order Management System** built with **Spring Boot 3.4** to demonstrate Java backend engineering best practices.

## 🏗️ Architecture

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Client     │────▶│  Spring Boot │────▶│ PostgreSQL  │
│   (REST)     │     │   Backend   │     │  Database   │
└─────────────┘     └──────┬──────┘     └─────────────┘
                           │
                    ┌──────┼──────┐
                    │      │      │
               ┌────▼──┐ ┌▼────┐ ┌▼─────┐
               │ Redis │ │Kafka│ │Swagger│
               │ Cache │ │Queue│ │  UI   │
               └───────┘ └──┬──┘ └──────┘
                            │
               ┌────────────┼────────────┐
               │            │            │
          ┌────▼────┐ ┌─────▼────┐ ┌─────▼──────┐
          │Notif    │ │ Audit    │ │ Reporting  │
          │Consumer │ │ Consumer │ │ Consumer   │
          └─────────┘ └──────────┘ └────────────┘
```

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Language |
| Spring Boot | 3.4.5 | Framework |
| Spring Security | - | JWT Authentication |
| Spring Data JPA | - | ORM / Repository |
| Hibernate | - | JPA Implementation |
| PostgreSQL | 16 | Database |
| Redis | 7 | Caching |
| Apache Kafka | 7.6.0 | Message Queue |
| Flyway | - | Database Migration |
| MapStruct | 1.6.3 | Object Mapping |
| Lombok | - | Boilerplate Reduction |
| SpringDoc OpenAPI | 2.8.0 | API Documentation |
| JUnit 5 + Mockito | - | Testing |
| Testcontainers | 1.20.4 | Integration Testing |
| Docker | - | Containerization |
| Jenkins | - | CI/CD Pipeline |
| Kubernetes | - | Orchestration |

## 📋 Features

- ✅ **Authentication & Authorization** - JWT + Refresh Token + Role-based access (ADMIN, STAFF, CUSTOMER)
- ✅ **User Management** - Admin user CRUD, status management, role assignment
- ✅ **Category Management** - CRUD with slug generation, Redis caching
- ✅ **Product Management** - CRUD, search, filter, pagination, Redis caching
- ✅ **Inventory Management** - Stock increase/decrease/adjust, optimistic locking
- ✅ **Order Management** - Create, cancel, status transitions, inventory deduction
- ✅ **Payment Simulation** - Pay/fail simulation with Kafka events
- ✅ **Kafka Event Processing** - Order/Payment events → Notification, Audit, Reporting consumers
- ✅ **Notification Module** - Mock notification system from Kafka events
- ✅ **Audit Log Module** - Event-driven audit logging with JSONB metadata
- ✅ **Reporting Module** - Revenue, top products, customer orders, status summary (complex SQL)
- ✅ **Redis Caching** - Product detail + category list caching with eviction
- ✅ **Global Error Handling** - Consistent error response format with traceId
- ✅ **Bean Validation** - Input validation on all request DTOs
- ✅ **API Documentation** - Swagger UI at `/swagger-ui.html`

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- Docker & Docker Compose

### Run with Docker Compose

```bash
# Start all services (PostgreSQL, Redis, Kafka, Zookeeper, App)
docker-compose up -d

# Check logs
docker-compose logs -f app
```

### Run Locally (Development)

```bash
# Start infrastructure only
docker-compose up -d postgres redis zookeeper kafka

# Run the application
mvn spring-boot:run
```

### Access

- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

## 🔑 Default Users

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@semicolon.com | Admin@123 |
| Staff | staff@semicolon.com | Staff@123 |
| Customer | customer@semicolon.com | Customer@123 |

## 📡 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/register` | Register customer |
| POST | `/api/v1/auth/login` | Login |
| POST | `/api/v1/auth/refresh-token` | Refresh access token |
| POST | `/api/v1/auth/logout` | Logout (revoke tokens) |
| GET | `/api/v1/auth/me` | Get current user |

### Products & Categories (Public)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/categories` | List categories |
| GET | `/api/v1/products` | Search products |
| GET | `/api/v1/products/{id}` | Product detail |

### Orders (Customer)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/orders` | Create order |
| GET | `/api/v1/orders` | My orders |
| POST | `/api/v1/orders/{id}/cancel` | Cancel order |

### Payments
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/orders/{id}/payments/pay` | Simulate payment |
| POST | `/api/v1/orders/{id}/payments/fail` | Simulate failure |

### Admin APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/admin/users` | List users |
| PATCH | `/api/v1/admin/orders/{id}/status` | Update order status |
| GET | `/api/v1/admin/reports/revenue` | Revenue report |
| GET | `/api/v1/admin/audit-logs` | Audit logs |
| DELETE | `/api/v1/admin/cache/all` | Clear all caches |

## 📊 Kafka Topics

| Topic | Producer | Consumers |
|-------|----------|-----------|
| `oms.order.created` | OrderService | Notification, Audit, Reporting |
| `oms.order.cancelled` | OrderService | Notification, Audit |
| `oms.order.status-changed` | OrderService | Audit |
| `oms.payment.succeeded` | PaymentService | Notification, Audit, Reporting |
| `oms.payment.failed` | PaymentService | Notification, Audit |

## 🧪 Testing

```bash
# Unit tests
mvn test

# All tests
mvn verify
```

## 🐳 Docker

```bash
docker build -t semicolon/oms .
```

## ☸️ Kubernetes

```bash
kubectl apply -f k8s/
```

## 📁 Project Structure

```
com.semicolon.oms
├── common (config, exception, response, security, util)
├── auth (controller, service, dto)
├── user (controller, service, repository, entity, dto)
├── catalog (controller, service, repository, entity, dto, mapper)
├── inventory (controller, service, repository, entity, dto)
├── order (controller, service, repository, entity, dto)
├── payment (controller, service, repository, entity, dto)
├── notification (controller, service, repository, entity, dto)
├── audit (controller, service, repository, entity, dto)
├── report (controller, service, dto)
└── messaging (event, producer, consumer, config)
```

## 📝 License

This project is for educational and portfolio purposes.

1. lấy docker hub token
2. push project lên github
3. set up github action
4. đặt biến repo secret
5. cài github runner
6. chạy lệnh sau để chạy runner background
sudo ./svc.sh install
sudo ./svc.sh start

Bước 1: Chuẩn bị môi trường trên VPS mới

Tạo user non-root (ví dụ: cuong).
Cài đặt Docker và Docker-compose lên VPS.
Cấp quyền Docker cho user: sudo usermod -aG docker cuong.
Copy file docker-compose.yml sang VPS mới và gõ lệnh để dựng lại hạ tầng DB/Redis: docker-compose up -d postgres redis zookeeper kafka (Lưu ý: Nếu cần dữ liệu cũ, bạn phải sao lưu (dump) database từ VPS cũ rồi import sang VPS mới).
Bước 2: Kết nối VPS mới với GitHub

Lên trang web GitHub của dự án -> Settings > Actions > Runners.
Xóa Runner cũ đi (cho đỡ rối).
Bấm New self-hosted runner để lấy Token mới.
Lên Terminal của VPS mới, làm lại đúng 4 bước tải, ./config.sh và cài ./svc.sh giống hệt như bạn vừa làm hôm nay.
Bước 3: Tận hưởng (Không cần sửa code hay Secret)

Nhờ việc chúng ta đã setup hệ thống gọi nhau bằng tên nội bộ (DB_HOST=postgres, REDIS_HOST=redis), nên khi sang VPS mới bạn KHÔNG CẦN phải đổi bất kỳ Secret nào trên Github cả! Mọi thứ vẫn nguyên vẹn.
Bạn chỉ cần push một commit mới, hoặc bấm Re-run pipeline, VPS mới sẽ tự động kéo code, tự build Image và tự chạy một cách hoàn hảo.

grafana loki dùng để:
+ Thu thập log từ các service
+ Lưu trữ log
+ Tìm kiếm log
+ Phân tích log
+ Trực quan log

promtail dùng để:
+ Đọc log từ container
+ Gửi log đến grafana loki

Grafana dùng để:
+ Trực quan log
+ Tìm kiếm log
+ Phân tích log