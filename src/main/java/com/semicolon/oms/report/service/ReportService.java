package com.semicolon.oms.report.service;

import com.semicolon.oms.report.dto.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public RevenueReportResponse getRevenueReport(LocalDate from, LocalDate to) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt = to.plusDays(1).atStartOfDay();

        Query q = entityManager.createNativeQuery(
                "SELECT COALESCE(SUM(CASE WHEN p.status = 'PAID' THEN p.amount ELSE 0 END), 0) as total_revenue, " +
                "COUNT(o.id) as total_orders, " +
                "COUNT(CASE WHEN o.status = 'COMPLETED' THEN 1 END) as completed, " +
                "COUNT(CASE WHEN o.status = 'CANCELLED' THEN 1 END) as cancelled " +
                "FROM orders o LEFT JOIN payments p ON o.id = p.order_id " +
                "WHERE o.created_at >= ?1 AND o.created_at < ?2");
        q.setParameter(1, fromDt);
        q.setParameter(2, toDt);

        Object[] row = (Object[]) q.getSingleResult();
        return RevenueReportResponse.builder()
                .totalRevenue((BigDecimal) row[0])
                .totalOrders(((Number) row[1]).longValue())
                .completedOrders(((Number) row[2]).longValue())
                .cancelledOrders(((Number) row[3]).longValue())
                .build();
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<TopProductResponse> getTopProducts(LocalDate from, LocalDate to) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt = to.plusDays(1).atStartOfDay();

        Query q = entityManager.createNativeQuery(
                "SELECT oi.product_id, oi.product_name, SUM(oi.quantity) as qty, SUM(oi.total_price) as revenue " +
                "FROM order_items oi JOIN orders o ON oi.order_id = o.id " +
                "WHERE o.created_at >= ?1 AND o.created_at < ?2 AND o.status != 'CANCELLED' " +
                "GROUP BY oi.product_id, oi.product_name ORDER BY revenue DESC LIMIT 10");
        q.setParameter(1, fromDt);
        q.setParameter(2, toDt);

        List<Object[]> rows = q.getResultList();
        return rows.stream().map(r -> TopProductResponse.builder()
                .productId(((Number) r[0]).longValue())
                .productName((String) r[1])
                .totalQuantitySold(((Number) r[2]).longValue())
                .totalRevenue((BigDecimal) r[3])
                .build()).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<CustomerOrderResponse> getCustomerOrders(LocalDate from, LocalDate to) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt = to.plusDays(1).atStartOfDay();

        Query q = entityManager.createNativeQuery(
                "SELECT u.id, u.full_name, u.email, COUNT(o.id) as cnt, COALESCE(SUM(o.total_amount), 0) as spent " +
                "FROM users u JOIN orders o ON u.id = o.user_id " +
                "WHERE o.created_at >= ?1 AND o.created_at < ?2 " +
                "GROUP BY u.id, u.full_name, u.email ORDER BY spent DESC");
        q.setParameter(1, fromDt);
        q.setParameter(2, toDt);

        List<Object[]> rows = q.getResultList();
        return rows.stream().map(r -> CustomerOrderResponse.builder()
                .userId(((Number) r[0]).longValue())
                .fullName((String) r[1])
                .email((String) r[2])
                .totalOrders(((Number) r[3]).longValue())
                .totalSpent((BigDecimal) r[4])
                .build()).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<OrderStatusSummaryResponse> getOrderStatusSummary() {
        Query q = entityManager.createNativeQuery(
                "SELECT status, COUNT(*) as cnt FROM orders GROUP BY status ORDER BY cnt DESC");
        List<Object[]> rows = q.getResultList();
        return rows.stream().map(r -> OrderStatusSummaryResponse.builder()
                .status((String) r[0])
                .count(((Number) r[1]).longValue())
                .build()).collect(Collectors.toList());
    }
}
