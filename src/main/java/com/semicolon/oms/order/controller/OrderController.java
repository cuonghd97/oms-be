package com.semicolon.oms.order.controller;

import com.semicolon.oms.common.response.ApiResponse;
import com.semicolon.oms.common.response.PagedResponse;
import com.semicolon.oms.common.security.UserPrincipal;
import com.semicolon.oms.order.dto.CreateOrderRequest;
import com.semicolon.oms.order.dto.OrderResponse;
import com.semicolon.oms.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Customer order APIs")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create a new order")
    public ResponseEntity<ApiResponse<OrderResponse>> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(orderService.createOrder(principal.getId(), request)));
    }

    @GetMapping
    @Operation(summary = "List my orders")
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponse>>> myOrders(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getCustomerOrders(principal.getId(), pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get my order detail")
    public ResponseEntity<ApiResponse<OrderResponse>> getById(
            @AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderById(id, principal.getId())));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel my pending order")
    public ResponseEntity<ApiResponse<OrderResponse>> cancel(
            @AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.cancelOrder(id, principal.getId())));
    }
}
