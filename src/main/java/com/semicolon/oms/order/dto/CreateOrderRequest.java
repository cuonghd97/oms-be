package com.semicolon.oms.order.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {
    @NotEmpty(message = "Order items must not be empty")
    private List<OrderItemRequest> items;
}
