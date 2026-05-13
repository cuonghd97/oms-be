package com.semicolon.oms.payment.controller;

import com.semicolon.oms.common.response.ApiResponse;
import com.semicolon.oms.payment.dto.PaymentResponse;
import com.semicolon.oms.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders/{orderId}/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment simulation APIs")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pay")
    @Operation(summary = "Simulate successful payment")
    public ResponseEntity<ApiResponse<PaymentResponse>> pay(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.payOrder(orderId)));
    }

    @PostMapping("/fail")
    @Operation(summary = "Simulate failed payment")
    public ResponseEntity<ApiResponse<PaymentResponse>> fail(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.failPayment(orderId)));
    }

    @GetMapping
    @Operation(summary = "Get payment status")
    public ResponseEntity<ApiResponse<PaymentResponse>> get(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentByOrderId(orderId)));
    }
}
