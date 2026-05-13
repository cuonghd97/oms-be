package com.semicolon.oms.payment.service;

import com.semicolon.oms.common.exception.BadRequestException;
import com.semicolon.oms.common.exception.ResourceNotFoundException;
import com.semicolon.oms.messaging.producer.EventProducer;
import com.semicolon.oms.payment.dto.PaymentResponse;
import com.semicolon.oms.payment.entity.Payment;
import com.semicolon.oms.payment.entity.PaymentStatus;
import com.semicolon.oms.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final EventProducer eventProducer;

    @Transactional
    public PaymentResponse payOrder(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "orderId", orderId));
        if (payment.getStatus() != PaymentStatus.UNPAID) {
            throw new BadRequestException("Payment already processed with status: " + payment.getStatus());
        }
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        payment = paymentRepository.save(payment);

        eventProducer.publishPaymentSucceeded(payment);
        return toResponse(payment);
    }

    @Transactional
    public PaymentResponse failPayment(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "orderId", orderId));
        if (payment.getStatus() != PaymentStatus.UNPAID) {
            throw new BadRequestException("Payment already processed with status: " + payment.getStatus());
        }
        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailedAt(LocalDateTime.now());
        payment = paymentRepository.save(payment);

        eventProducer.publishPaymentFailed(payment);
        return toResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "orderId", orderId));
        return toResponse(payment);
    }

    private PaymentResponse toResponse(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .orderId(p.getOrderId())
                .status(p.getStatus().name())
                .amount(p.getAmount())
                .paidAt(p.getPaidAt())
                .failedAt(p.getFailedAt())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
